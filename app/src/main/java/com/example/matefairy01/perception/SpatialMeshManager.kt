package com.example.matefairy01.perception

import android.os.Handler
import android.util.Log
import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.resource.Material
import com.pico.spatial.core.ecs.resource.MeshResource
import com.pico.spatial.core.ecs.resource.PhysicsMaterialResource
import com.pico.spatial.core.ecs.resource.ShapeResource
import com.pico.spatial.core.ecs.simulation.CollisionFilter
import com.pico.spatial.core.ecs.simulation.CollisionInfoDetailLevel
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.lifecycle.Cancellable
import com.pico.spatial.sense.base.AnchorUpdate
import com.pico.spatial.sense.mesh.MeshAnchor
import com.pico.spatial.sense.mesh.MeshTrackingManager
import java.util.UUID

class SpatialMeshManager(private val mainHandler: Handler) {
    private var parentEntity: Entity? = null
    private var subscription: Cancellable? = null
    private var occlusionMaterial: Material? = null
    private val meshEntities = mutableMapOf<UUID, Entity>()

    fun setOcclusionMaterial(material: Material?) {
        occlusionMaterial = material
    }

    fun start(parentEntity: Entity) {
        this.parentEntity = parentEntity
        if (subscription == null) {
            subscription = MeshTrackingManager.subscribeAnchorUpdate { update ->
                mainHandler.post { handleAnchorUpdate(update) }
            }
        }
        MeshTrackingManager.start()
    }

    fun stop(clearMeshes: Boolean = false) {
        runCatching { MeshTrackingManager.stop() }
            .onFailure { Log.w(TAG, "Failed to stop mesh tracking", it) }
        if (clearMeshes) {
            clearMeshEntities()
        }
    }

    fun dispose() {
        stop(clearMeshes = true)
        subscription?.cancel()
        subscription = null
        occlusionMaterial = null
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
        val existing = meshEntities.remove(anchor.anchorUUID)
        existing?.destroy()

        runCatching {
            val mesh = MeshResource.loadFromMeshAnchor(anchor.anchorUUID)
            val shape = ShapeResource.createStaticMesh(mesh)
            Entity().apply {
                components[TransformComponent::class.java]?.apply {
                    setPosition(parent.convertPositionFrom(anchor.transform.position, null))
                    setQuaternion(parent.convertRotationFrom(anchor.transform.rotation.toQuat(), null))
                }
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
        }.onSuccess { entity ->
            parent.addChild(entity)
            meshEntities[anchor.anchorUUID] = entity
        }.onFailure {
            Log.w(TAG, "Failed to upsert mesh anchor ${anchor.anchorUUID}", it)
        }
    }

    private fun removeMeshEntity(anchorUUID: UUID) {
        meshEntities.remove(anchorUUID)?.destroy()
    }

    private fun clearMeshEntities() {
        meshEntities.values.forEach { it.destroy() }
        meshEntities.clear()
    }

    private companion object {
        private const val TAG = "SpatialMeshManager"
    }
}
