package com.example.matefairy01.perception

import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.sense.base.SemanticLabelType
import com.pico.spatial.sense.plane.PlaneOrientation
import java.util.UUID

enum class SemanticInteractionPurpose {
    STAY_ON_CHAIR
}

enum class SemanticTargetConfidenceTier {
    HIGH,
    MEDIUM,
    LOW
}

data class SemanticInteractionTarget(
    val semantic: SemanticLabelType,
    val anchorUUID: UUID,
    val source: RealWorldSemanticSource,
    val position: Vector3,
    val rotation: Quat,
    val boundingBoxSize: Vector3,
    val confidenceTier: SemanticTargetConfidenceTier,
    val sourceSnapshot: SemanticObjectSnapshot
)

class RealWorldSemanticTargetLocator(
    private val query: RealWorldSemanticQuery
) {
    fun requestTarget(
        semantic: SemanticLabelType,
        origin: Vector3,
        maxDistance: Float,
        purpose: SemanticInteractionPurpose
    ): SemanticInteractionTarget? {
        return query.findAll(semantic = semantic, origin = origin, maxDistance = maxDistance)
            .asSequence()
            .map { snapshot -> snapshot.toInteractionTarget(semantic, purpose) }
            .sortedWith(
                compareBy<SemanticInteractionTarget> { it.confidenceTier.priority }
                    .thenBy { it.source.priority }
                    .thenBy { it.sourceSnapshot.distanceSquaredTo(origin) }
            )
            .firstOrNull()
    }

    private fun SemanticObjectSnapshot.toInteractionTarget(
        semantic: SemanticLabelType,
        purpose: SemanticInteractionPurpose
    ): SemanticInteractionTarget {
        val targetPosition = when (purpose) {
            SemanticInteractionPurpose.STAY_ON_CHAIR -> resolveChairStayPosition()
        }
        return SemanticInteractionTarget(
            semantic = semantic,
            anchorUUID = anchorUUID,
            source = source,
            position = targetPosition,
            rotation = rotation,
            boundingBoxSize = boundingBoxSize,
            confidenceTier = confidenceForPurpose(purpose),
            sourceSnapshot = this
        )
    }

    private fun SemanticObjectSnapshot.resolveChairStayPosition(): Vector3 {
        // The semantic locator only returns a coarse object candidate.
        // SemanticSurfaceResolver later resolves the real surface height via plane/mesh sampling.
        return position
    }

    private fun SemanticObjectSnapshot.confidenceForPurpose(
        purpose: SemanticInteractionPurpose
    ): SemanticTargetConfidenceTier {
        return when (purpose) {
            SemanticInteractionPurpose.STAY_ON_CHAIR -> when {
                source == RealWorldSemanticSource.PLANE &&
                    planeOrientation == PlaneOrientation.HORIZONTAL_UPWARD -> SemanticTargetConfidenceTier.HIGH
                source == RealWorldSemanticSource.PLANE -> SemanticTargetConfidenceTier.MEDIUM
                else -> SemanticTargetConfidenceTier.LOW
            }
        }
    }

    private val SemanticTargetConfidenceTier.priority: Int
        get() = when (this) {
            SemanticTargetConfidenceTier.HIGH -> 0
            SemanticTargetConfidenceTier.MEDIUM -> 1
            SemanticTargetConfidenceTier.LOW -> 2
        }

    private val RealWorldSemanticSource.priority: Int
        get() = when (this) {
            RealWorldSemanticSource.PLANE -> 0
            RealWorldSemanticSource.MESH -> 1
        }

}
