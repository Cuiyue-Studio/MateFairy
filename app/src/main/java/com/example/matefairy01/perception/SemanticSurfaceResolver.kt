package com.example.matefairy01.perception

import com.pico.spatial.core.ecs.Scene
import com.pico.spatial.core.ecs.simulation.CollisionCastHitMode
import com.pico.spatial.core.ecs.simulation.CollisionCastResult
import com.pico.spatial.core.ecs.simulation.CollisionGroup
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.sense.plane.PlaneOrientation
import kotlin.math.max
import kotlin.math.sqrt

enum class SemanticSurfacePlacementSource {
    PLANE_ANCHOR,
    MESH_RAYCAST,
    BOUNDS_FALLBACK
}

data class SemanticSurfacePlacement(
    val position: Vector3,
    val normal: Vector3,
    val source: SemanticSurfacePlacementSource,
    val confidence: SemanticTargetConfidenceTier
)

class SemanticSurfaceResolver {
    fun resolvePlacement(
        scene: Scene,
        target: SemanticInteractionTarget,
        surfaceOffset: Float = DEFAULT_FAIRY_SURFACE_OFFSET
    ): SemanticSurfacePlacement {
        resolvePlaneSurface(target, surfaceOffset)?.let { return it }
        resolveMeshSurfaceByRaycast(scene, target, surfaceOffset)?.let { return it }
        return resolveBoundsFallback(target, surfaceOffset)
    }

    private fun resolvePlaneSurface(
        target: SemanticInteractionTarget,
        surfaceOffset: Float
    ): SemanticSurfacePlacement? {
        val snapshot = target.sourceSnapshot
        if (
            snapshot.source != RealWorldSemanticSource.PLANE ||
            snapshot.planeOrientation != PlaneOrientation.HORIZONTAL_UPWARD
        ) {
            return null
        }
        return SemanticSurfacePlacement(
            position = Vector3(
                snapshot.position.x,
                snapshot.position.y + surfaceOffset,
                snapshot.position.z
            ),
            normal = Vector3(0f, 1f, 0f),
            source = SemanticSurfacePlacementSource.PLANE_ANCHOR,
            confidence = SemanticTargetConfidenceTier.HIGH
        )
    }

    private fun resolveMeshSurfaceByRaycast(
        scene: Scene,
        target: SemanticInteractionTarget,
        surfaceOffset: Float
    ): SemanticSurfacePlacement? {
        val meshQuery = SpatialMeshRuntimeDependencies.query
        val snapshot = target.sourceSnapshot
        val meshEntity = meshQuery.getMeshEntity(snapshot.anchorUUID)

        val candidates = mutableListOf<MeshSurfaceCandidate>()
        sampleOffsets(snapshot.boundingBoxSize).forEachIndexed { index, offset ->
            val origin = Vector3(
                snapshot.position.x + offset.x,
                snapshot.position.y + rayStartYOffset(snapshot.boundingBoxSize),
                snapshot.position.z + offset.z
            )
            val hit = scene.rayCast(
                origin = origin,
                direction = Vector3(0f, -1f, 0f),
                length = rayLength(snapshot.boundingBoxSize),
                hitMode = CollisionCastHitMode.ALL,
                group = CollisionGroup(CollisionGroup.COLLISION_GROUP_ALL)
            ).results
                .asSequence()
                .filter { it.normal.y >= MIN_UPWARD_NORMAL_Y }
                .filter { it.position.y >= minAcceptedSurfaceY(snapshot) }
                .map { result ->
                    val exactAnchorMatch = meshEntity != null &&
                        (result.entity == meshEntity || meshQuery.getAnchorUUID(result.entity) == snapshot.anchorUUID)
                    MeshSurfaceCandidate(
                        hit = result,
                        sampleIndex = index,
                        horizontalDistanceToCenter = horizontalDistance(result.position, snapshot.position),
                        heightPenalty = heightPenalty(result.position.y, snapshot),
                        exactAnchorMatch = exactAnchorMatch
                    )
                }
                .minWithOrNull(
                    compareBy<MeshSurfaceCandidate> { if (it.exactAnchorMatch) 0 else 1 }
                        .thenBy { it.hit.distance }
                )

            if (hit != null) candidates += hit
        }

        val best = candidates.minWithOrNull(
            compareBy<MeshSurfaceCandidate> { if (it.exactAnchorMatch) 0 else 1 }
                .thenBy { it.heightPenalty }
                .thenBy { it.horizontalDistanceToCenter }
                .thenBy { it.sampleIndex }
        ) ?: return null

        return SemanticSurfacePlacement(
            position = Vector3(
                best.hit.position.x,
                best.hit.position.y + surfaceOffset,
                best.hit.position.z
            ),
            normal = best.hit.normal,
            source = SemanticSurfacePlacementSource.MESH_RAYCAST,
            confidence = SemanticTargetConfidenceTier.MEDIUM
        )
    }

    private fun resolveBoundsFallback(
        target: SemanticInteractionTarget,
        surfaceOffset: Float
    ): SemanticSurfacePlacement {
        val snapshot = target.sourceSnapshot
        val fallbackY = if (snapshot.source == RealWorldSemanticSource.PLANE) {
            snapshot.position.y
        } else {
            snapshot.position.y
        }
        return SemanticSurfacePlacement(
            position = Vector3(
                snapshot.position.x,
                fallbackY + surfaceOffset,
                snapshot.position.z
            ),
            normal = Vector3(0f, 1f, 0f),
            source = SemanticSurfacePlacementSource.BOUNDS_FALLBACK,
            confidence = SemanticTargetConfidenceTier.LOW
        )
    }

    private fun sampleOffsets(bounds: Vector3): List<Vector3> {
        val x = max(MIN_SAMPLE_SPAN, bounds.x * SAMPLE_SPAN_RATIO)
        val z = max(MIN_SAMPLE_SPAN, bounds.z * SAMPLE_SPAN_RATIO)
        return listOf(
            Vector3(0f, 0f, 0f),
            Vector3(x, 0f, 0f),
            Vector3(-x, 0f, 0f),
            Vector3(0f, 0f, z),
            Vector3(0f, 0f, -z),
            Vector3(x, 0f, z),
            Vector3(-x, 0f, z),
            Vector3(x, 0f, -z),
            Vector3(-x, 0f, -z)
        )
    }

    private fun rayStartYOffset(bounds: Vector3): Float {
        return max(MIN_RAY_START_Y_OFFSET, bounds.y * 0.5f + RAY_START_PADDING)
    }

    private fun rayLength(bounds: Vector3): Float {
        return max(MIN_RAY_LENGTH, bounds.y + RAY_LENGTH_PADDING)
    }

    private fun heightPenalty(y: Float, snapshot: SemanticObjectSnapshot): Float {
        val centerY = snapshot.position.y
        val upperPreferredY = centerY + snapshot.boundingBoxSize.y * PREFERRED_UPPER_SURFACE_RATIO
        return if (y <= upperPreferredY) 0f else y - upperPreferredY
    }

    private fun minAcceptedSurfaceY(snapshot: SemanticObjectSnapshot): Float {
        return snapshot.position.y - max(MIN_SURFACE_BELOW_CENTER, snapshot.boundingBoxSize.y * 0.25f)
    }

    private data class MeshSurfaceCandidate(
        val hit: CollisionCastResult,
        val sampleIndex: Int,
        val horizontalDistanceToCenter: Float,
        val heightPenalty: Float,
        val exactAnchorMatch: Boolean
    )

    private companion object {
        private const val DEFAULT_FAIRY_SURFACE_OFFSET = 0.0f
        private const val MIN_UPWARD_NORMAL_Y = 0.55f
        private const val SAMPLE_SPAN_RATIO = 0.28f
        private const val MIN_SAMPLE_SPAN = 0.08f
        private const val MIN_RAY_START_Y_OFFSET = 0.7f
        private const val RAY_START_PADDING = 0.5f
        private const val MIN_RAY_LENGTH = 1.4f
        private const val RAY_LENGTH_PADDING = 1.2f
        private const val PREFERRED_UPPER_SURFACE_RATIO = 0.35f
        private const val MIN_SURFACE_BELOW_CENTER = 0.12f
    }
}

private fun horizontalDistance(a: Vector3, b: Vector3): Float {
    val dx = a.x - b.x
    val dz = a.z - b.z
    return sqrt(dx * dx + dz * dz)
}
