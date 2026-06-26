package com.example.matefairy01.perception

import android.os.Handler
import android.util.Log
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.lifecycle.Cancellable
import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.sense.base.AnchorUpdate
import com.pico.spatial.sense.base.SemanticLabelType
import com.pico.spatial.sense.mesh.MeshAnchor
import com.pico.spatial.sense.mesh.MeshTrackingManager
import com.pico.spatial.sense.plane.PlaneAnchor
import com.pico.spatial.sense.plane.PlaneOrientation
import com.pico.spatial.sense.plane.PlaneTrackingManager
import java.util.UUID
import kotlin.math.sqrt

enum class RealWorldSemanticSource {
    MESH,
    PLANE
}

data class SemanticObjectSnapshot(
    val anchorUUID: UUID,
    val source: RealWorldSemanticSource,
    val semantics: Set<SemanticLabelType>,
    val primarySemantic: SemanticLabelType,
    val position: Vector3,
    val rotation: Quat,
    val boundingBoxSize: Vector3,
    val planeOrientation: PlaneOrientation? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    fun hasSemantic(semantic: SemanticLabelType): Boolean {
        return semantics.contains(semantic)
    }

    fun distanceTo(position: Vector3): Float {
        return sqrt(distanceSquaredTo(position))
    }

    fun distanceSquaredTo(position: Vector3): Float {
        val dx = this.position.x - position.x
        val dy = this.position.y - position.y
        val dz = this.position.z - position.z
        return dx * dx + dy * dy + dz * dz
    }
}

interface RealWorldSemanticQuery {
    fun requestSemanticScan(scanWindowMillis: Long = DEFAULT_SEMANTIC_SCAN_WINDOW_MILLIS)

    fun finishSemanticScanRequest()

    fun getSnapshot(): List<SemanticObjectSnapshot>

    fun findAll(
        semantic: SemanticLabelType,
        origin: Vector3? = null,
        maxDistance: Float = Float.POSITIVE_INFINITY,
        sources: Set<RealWorldSemanticSource> = RealWorldSemanticSource.entries.toSet()
    ): List<SemanticObjectSnapshot>

    fun findNearest(
        semantic: SemanticLabelType,
        origin: Vector3,
        maxDistance: Float,
        sources: Set<RealWorldSemanticSource> = RealWorldSemanticSource.entries.toSet()
    ): SemanticObjectSnapshot?
}

const val DEFAULT_SEMANTIC_SCAN_WINDOW_MILLIS = 2500L

object EmptyRealWorldSemanticQuery : RealWorldSemanticQuery {
    override fun requestSemanticScan(scanWindowMillis: Long) = Unit

    override fun finishSemanticScanRequest() = Unit

    override fun getSnapshot(): List<SemanticObjectSnapshot> = emptyList()

    override fun findAll(
        semantic: SemanticLabelType,
        origin: Vector3?,
        maxDistance: Float,
        sources: Set<RealWorldSemanticSource>
    ): List<SemanticObjectSnapshot> = emptyList()

    override fun findNearest(
        semantic: SemanticLabelType,
        origin: Vector3,
        maxDistance: Float,
        sources: Set<RealWorldSemanticSource>
    ): SemanticObjectSnapshot? = null
}

object RealWorldSemanticRuntimeDependencies {
    var query: RealWorldSemanticQuery = EmptyRealWorldSemanticQuery
        private set

    fun bind(query: RealWorldSemanticQuery) {
        this.query = query
    }

    fun clear() {
        query = EmptyRealWorldSemanticQuery
    }
}

class RealWorldSemanticManager(private val mainHandler: Handler) : RealWorldSemanticQuery {
    private val lock = Any()
    private var parentEntity: Entity? = null
    private var meshSubscription: Cancellable? = null
    private var planeSubscription: Cancellable? = null
    private val semanticObjects = mutableMapOf<SemanticObjectKey, SemanticObjectSnapshot>()
    private var semanticScanActiveUntilMillis: Long = 0L

    fun attachSceneRoot(parentEntity: Entity) {
        this.parentEntity = parentEntity
    }

    fun start(parentEntity: Entity) {
        attachSceneRoot(parentEntity)
        requestSemanticScan()
    }

    override fun requestSemanticScan(scanWindowMillis: Long) {
        subscribeIfNeeded()
        semanticScanActiveUntilMillis = System.currentTimeMillis() + scanWindowMillis
        MeshTrackingManager.start()
        PlaneTrackingManager.start()
        Log.i(TAG, "Real world semantic scan requested")
    }

    override fun finishSemanticScanRequest() {
        semanticScanActiveUntilMillis = 0L
        runCatching { PlaneTrackingManager.stop() }
            .onFailure { Log.w(TAG, "Failed to stop plane semantic tracking", it) }
    }

    fun stop(clearSemantics: Boolean = false) {
        finishSemanticScanRequest()
        if (clearSemantics) {
            synchronized(lock) {
                semanticObjects.clear()
            }
        }
    }

    fun dispose() {
        stop(clearSemantics = true)
        meshSubscription?.cancel()
        planeSubscription?.cancel()
        meshSubscription = null
        planeSubscription = null
        parentEntity = null
    }

    override fun getSnapshot(): List<SemanticObjectSnapshot> {
        stopPlaneTrackingIfScanExpired()
        return synchronized(lock) {
            semanticObjects.values.toList()
        }
    }

    override fun findAll(
        semantic: SemanticLabelType,
        origin: Vector3?,
        maxDistance: Float,
        sources: Set<RealWorldSemanticSource>
    ): List<SemanticObjectSnapshot> {
        val maxDistanceSquared = maxDistance * maxDistance
        return getSnapshot()
            .asSequence()
            .filter { it.source in sources }
            .filter { it.hasSemantic(semantic) }
            .filter { origin == null || it.distanceSquaredTo(origin) <= maxDistanceSquared }
            .sortedWith(compareBy<SemanticObjectSnapshot> { it.source.priority }.thenBy {
                if (origin == null) 0f else it.distanceSquaredTo(origin)
            })
            .toList()
    }

    override fun findNearest(
        semantic: SemanticLabelType,
        origin: Vector3,
        maxDistance: Float,
        sources: Set<RealWorldSemanticSource>
    ): SemanticObjectSnapshot? {
        val maxDistanceSquared = maxDistance * maxDistance
        return getSnapshot()
            .asSequence()
            .filter { it.source in sources }
            .filter { it.hasSemantic(semantic) }
            .filter { it.distanceSquaredTo(origin) <= maxDistanceSquared }
            .minWithOrNull(compareBy<SemanticObjectSnapshot> { it.source.priority }.thenBy {
                it.distanceSquaredTo(origin)
            })
    }

    private fun subscribeIfNeeded() {
        if (meshSubscription == null) {
            meshSubscription = MeshTrackingManager.subscribeAnchorUpdate { update ->
                mainHandler.post { handleMeshAnchorUpdate(update) }
            }
        }
        if (planeSubscription == null) {
            planeSubscription = PlaneTrackingManager.subscribeAnchorUpdate { update ->
                mainHandler.post { handlePlaneAnchorUpdate(update) }
            }
        }
    }

    private fun handleMeshAnchorUpdate(update: AnchorUpdate<MeshAnchor>) {
        when (update.event) {
            AnchorUpdate.Event.ADDED,
            AnchorUpdate.Event.UPDATED,
            AnchorUpdate.Event.LOADED -> upsertMeshSemantic(update.anchor)
            AnchorUpdate.Event.REMOVED -> removeSemantic(
                update.anchor.anchorUUID,
                RealWorldSemanticSource.MESH
            )
        }
    }

    private fun handlePlaneAnchorUpdate(update: AnchorUpdate<PlaneAnchor>) {
        when (update.event) {
            AnchorUpdate.Event.ADDED,
            AnchorUpdate.Event.UPDATED,
            AnchorUpdate.Event.LOADED -> upsertPlaneSemantic(update.anchor)
            AnchorUpdate.Event.REMOVED -> removeSemantic(
                update.anchor.anchorUUID,
                RealWorldSemanticSource.PLANE
            )
        }
    }

    private fun upsertMeshSemantic(anchor: MeshAnchor) {
        val parent = parentEntity ?: return
        val semantics = anchor.semantics.normalizedSemantics()
        val snapshot = SemanticObjectSnapshot(
            anchorUUID = anchor.anchorUUID,
            source = RealWorldSemanticSource.MESH,
            semantics = semantics,
            primarySemantic = anchor.semantics.primarySemantic(),
            position = parent.convertPositionFrom(anchor.transform.position, null),
            rotation = parent.convertRotationFrom(anchor.transform.rotation.toQuat(), null),
            boundingBoxSize = anchor.boundingBoxSize
        )
        upsertSemantic(snapshot)
    }

    private fun upsertPlaneSemantic(anchor: PlaneAnchor) {
        val parent = parentEntity ?: return
        val semantic = anchor.semantics
        val snapshot = SemanticObjectSnapshot(
            anchorUUID = anchor.anchorUUID,
            source = RealWorldSemanticSource.PLANE,
            semantics = setOf(semantic),
            primarySemantic = semantic,
            position = parent.convertPositionFrom(anchor.transform.position, null),
            rotation = parent.convertRotationFrom(anchor.transform.rotation.toQuat(), null),
            boundingBoxSize = Vector3(anchor.boundingBoxSize.x, 0f, anchor.boundingBoxSize.y),
            planeOrientation = anchor.planeOrientation
        )
        upsertSemantic(snapshot)
    }

    private fun upsertSemantic(snapshot: SemanticObjectSnapshot) {
        synchronized(lock) {
            semanticObjects[SemanticObjectKey(snapshot.anchorUUID, snapshot.source)] = snapshot
        }
        Log.d(
            TAG,
            "Semantic ${snapshot.source}/${snapshot.anchorUUID}: " +
                "primary=${snapshot.primarySemantic}, all=${snapshot.semantics}"
        )
    }

    private fun removeSemantic(anchorUUID: UUID, source: RealWorldSemanticSource) {
        synchronized(lock) {
            semanticObjects.remove(SemanticObjectKey(anchorUUID, source))
        }
    }

    private fun stopPlaneTrackingIfScanExpired() {
        val activeUntil = semanticScanActiveUntilMillis
        if (activeUntil <= 0L || System.currentTimeMillis() <= activeUntil) return
        finishSemanticScanRequest()
    }

    private data class SemanticObjectKey(
        val anchorUUID: UUID,
        val source: RealWorldSemanticSource
    )

    private companion object {
        private const val TAG = "RealWorldSemanticManager"
    }
}

private val RealWorldSemanticSource.priority: Int
    get() = when (this) {
        RealWorldSemanticSource.PLANE -> 0
        RealWorldSemanticSource.MESH -> 1
    }

private fun List<SemanticLabelType>.normalizedSemantics(): Set<SemanticLabelType> {
    val known = filter { it != SemanticLabelType.UNKNOWN }.toSet()
    return known.ifEmpty { setOf(SemanticLabelType.UNKNOWN) }
}

private fun List<SemanticLabelType>.primarySemantic(): SemanticLabelType {
    return filter { it != SemanticLabelType.UNKNOWN }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
        ?: SemanticLabelType.UNKNOWN
}
