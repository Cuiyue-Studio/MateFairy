package com.example.matefairy01.interaction

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.pico.spatial.core.ecs.Entity

private const val OBJECT_ANIMATION_TAG = "ObjectAnimationPlayer"
private val objectAnimationHandler = Handler(Looper.getMainLooper())

fun playObjectAnimationOnTarget(
    entity: Entity,
    trackIndex: Int = 0,
    maxDurationMs: Long? = null
): Boolean {
    val preferredName = entity.components[InteractionObjectComponent::class.java]?.objectId
    val candidates = mutableListOf(entity)
    preferredName
        ?.let { entity.findEntity(it) }
        ?.takeIf { it != entity }
        ?.let { candidates.add(it) }

    candidates.forEach { candidate ->
        val resources = runCatching { candidate.getAnimationResources() }
            .onFailure {
                Log.w(
                    OBJECT_ANIMATION_TAG,
                    "Failed to query animation resources: entity=${candidate.getName()}",
                    it
                )
            }
            .getOrNull()
            ?: return@forEach

        if (trackIndex in resources.indices) {
            val controller = candidate.playAnimation(resources[trackIndex])
            maxDurationMs?.takeIf { it > 0L }?.let { durationMs ->
                objectAnimationHandler.postDelayed(
                    {
                        runCatching {
                            if (controller.valid && controller.isPlaying()) {
                                controller.stop()
                            }
                            if (controller.valid) {
                                controller.close()
                            }
                        }.onFailure {
                            Log.w(
                                OBJECT_ANIMATION_TAG,
                                "Failed to stop object animation: target=${candidate.getName()}",
                                it
                            )
                        }
                    },
                    durationMs
                )
            }
            Log.d(
                OBJECT_ANIMATION_TAG,
                "Play object animation: wrapper=${entity.getName()}, target=${candidate.getName()}, trackIndex=$trackIndex, maxDurationMs=$maxDurationMs"
            )
            return true
        }

        if (resources.isNotEmpty()) {
            Log.w(
                OBJECT_ANIMATION_TAG,
                "Animation track out of range: entity=${candidate.getName()}, trackIndex=$trackIndex, resources=${resources.size}"
            )
        }
    }

    Log.w(
        OBJECT_ANIMATION_TAG,
        "No playable object animation: wrapper=${entity.getName()}, preferredName=$preferredName, trackIndex=$trackIndex"
    )
    return false
}
