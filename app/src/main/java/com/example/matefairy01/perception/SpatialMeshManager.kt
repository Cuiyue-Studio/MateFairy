package com.example.matefairy01.perception

import android.os.Handler
import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.ModelComponent
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.resource.MeshResource
import com.pico.spatial.core.ecs.resource.PhysicsMaterialResource
import com.pico.spatial.core.ecs.resource.PolygonFillMode
import com.pico.spatial.core.ecs.resource.ShapeResource
import com.pico.spatial.core.ecs.resource.UnlitMaterial
import com.pico.spatial.core.ecs.simulation.CollisionFilter
import com.pico.spatial.core.ecs.simulation.CollisionInfoDetailLevel
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.lifecycle.Cancellable
import com.pico.spatial.core.math.Color4
import com.pico.spatial.sense.base.AnchorUpdate
import com.pico.spatial.sense.mesh.MeshAnchor
import com.pico.spatial.sense.mesh.MeshTrackingManager
import java.util.UUID

class SpatialMeshManager(
    private val mainHandler: Handler
) {
    private val meshEntities = mutableMapOf<UUID, Entity>()
    private var subscription: Cancellable? = null
    private var parentEntity: Entity? = null
    var isScanning: Boolean = false
        private set

    fun start(parentEntity: Entity) {
        this.parentEntity = parentEntity
        if (subscription == null) {
            subscription = MeshTrackingManager.subscribeAnchorUpdate { update ->
                mainHandler.post { handleAnchorUpdate(update) }
            }
        }
        if (!isScanning) {
            MeshTrackingManager.start()
            isScanning = true
        }
    }

    fun stop(clearMeshes: Boolean = false) {
        if (isScanning) {
            MeshTrackingManager.stop()
            isScanning = false
        }
        if (clearMeshes) {
            clear()
        }
    }

    fun dispose() {
        stop(clearMeshes = true)
        subscription?.cancel()
        subscription = null
        parentEntity = null
    }

    private fun handleAnchorUpdate(update: AnchorUpdate<MeshAnchor>) {
        when (update.event) {
            AnchorUpdate.Event.ADDED,
            AnchorUpdate.Event.UPDATED,
            AnchorUpdate.Event.LOADED -> upsertMeshEntity(update.anchor)
            AnchorUpdate.Event.REMOVED -> removeMeshEntity(update.anchor.anchorUUID)
        }
    }

    private fun upsertMeshEntity(anchor: MeshAnchor) {
        val parent = parentEntity ?: return
        removeMeshEntity(anchor.anchorUUID)

        val mesh = MeshResource.loadFromMeshAnchor(anchor.anchorUUID)
        val shape = ShapeResource.createStaticMesh(mesh)
        val debugMaterial = UnlitMaterial.create().apply {
            setBaseColor(Color4(0.1f, 1.0f, 0.55f, 0.75f))
            setPolygonFillMode(PolygonFillMode.LINE)
        }
        val entity = Entity().apply {
            components[TransformComponent::class.java]?.apply {
                position = anchor.transform.position
                quaternion = anchor.transform.quaternion
            }
            components.set(ModelComponent(mesh, debugMaterial))
            components.set(
                CollisionComponent(
                    collisionShape = listOf(shape),
                    physicsMaterial = PhysicsMaterialResource(),
                    collisionResponseMode = CollisionResponseMode.COLLIDER_FULL,
                    collisionFilter = CollisionFilter.COLLISION_FILTER_DEFAULT,
                    collisionInfoDetailLevel = CollisionInfoDetailLevel.BRIEF
                )
            )
        }

        parent.addChild(entity)
        meshEntities[anchor.anchorUUID] = entity
    }

    private fun removeMeshEntity(anchorUUID: UUID) {
        meshEntities.remove(anchorUUID)?.destroy()
    }

    private fun clear() {
        meshEntities.values.forEach { it.destroy() }
        meshEntities.clear()
    }
}
