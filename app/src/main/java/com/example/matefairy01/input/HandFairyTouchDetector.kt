package com.example.matefairy01.input

import android.os.SystemClock
import android.util.Log
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.tracking.hand.HandJoint
import com.pico.spatial.tracking.hand.HandPose
import com.pico.spatial.tracking.hand.HandTrackingData
import kotlin.math.sqrt

/**
 * Detects direct virtual-hand contact with the fairy body.
 *
 * This stays independent from the player-fairy action scheduler: callers decide
 * what to do when a touch enters the fairy volume.
 */
class HandFairyTouchDetector(
    private val fairyPositionProvider: () -> Vector3?,
    private val onFairyTouched: () -> Unit
) {
    private var wasLeftTouching = false
    private var wasRightTouching = false
    private var lastTouchTimeMs = 0L

    fun processHandTrackingData(handTrackingData: HandTrackingData) {
        val fairyPosition = fairyPositionProvider() ?: return
        processHand(handTrackingData.left, fairyPosition, isLeft = true)
        processHand(handTrackingData.right, fairyPosition, isLeft = false)
    }

    private fun processHand(handPose: HandPose?, fairyPosition: Vector3, isLeft: Boolean) {
        if (handPose == null) {
            setTouching(isLeft, false)
            return
        }

        val isTouching = HAND_TOUCH_JOINTS.any { joint ->
            jointPosition(handPose, joint)?.let { distance(it, fairyPosition) <= TOUCH_RADIUS_METERS }
                ?: false
        }
        val wasTouching = if (isLeft) wasLeftTouching else wasRightTouching
        if (isTouching && !wasTouching) {
            onTouchEntered()
        }
        setTouching(isLeft, isTouching)
    }

    private fun onTouchEntered() {
        val now = SystemClock.elapsedRealtime()
        if (now - lastTouchTimeMs < TOUCH_COOLDOWN_MS) return
        lastTouchTimeMs = now
        Log.d(TAG, "Virtual hand touched fairy")
        onFairyTouched()
    }

    private fun setTouching(isLeft: Boolean, isTouching: Boolean) {
        if (isLeft) {
            wasLeftTouching = isTouching
        } else {
            wasRightTouching = isTouching
        }
    }

    private fun jointPosition(handPose: HandPose, index: HandJoint.Index): Vector3? {
        return try {
            handPose[index].position
        } catch (_: Exception) {
            null
        }
    }

    private fun distance(a: Vector3, b: Vector3): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        val dz = a.z - b.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    private companion object {
        private const val TAG = "HandFairyTouchDetector"
        private const val TOUCH_RADIUS_METERS = 0.24f
        private const val TOUCH_COOLDOWN_MS = 1200L

        private val HAND_TOUCH_JOINTS = listOf(
            HandJoint.Index.INDEX_TIP,
            HandJoint.Index.MIDDLE_TIP,
            HandJoint.Index.PALM
        )
    }
}
