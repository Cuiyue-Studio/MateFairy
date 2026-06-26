package com.example.matefairy01.input

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.tracking.hand.HandJoint
import com.pico.spatial.tracking.hand.HandPose
import com.pico.spatial.tracking.hand.HandTrackingData
import kotlin.math.sqrt

/**
 * Detects a double pinch from raw hand tracking data.
 *
 * This is intentionally independent of Spatial Pointer hit testing because an
 * "air pinch" may not produce a targetable pointer event.
 */
class HandDoublePinchDetector(
    private val onDoublePinchDetected: () -> Unit
) {
    companion object {
        private const val TAG = "HandDoublePinchDetector"
        private const val PINCH_CLOSE_THRESHOLD = 0.05f
        private const val PINCH_OPEN_THRESHOLD = 0.08f
        private const val PINCH_COOLDOWN_MS = 220L
        private const val DOUBLE_PINCH_TIMEOUT_MS = 700L
        private const val REQUIRED_PINCH_COUNT = 2
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var resetRunnable: Runnable? = null
    private var pinchCount = 0
    private var lastPinchTime = 0L
    private var isLeftPinching = false
    private var isRightPinching = false

    fun processHandTrackingData(handTrackingData: HandTrackingData) {
        processHand(handTrackingData.left, isLeft = true)
        processHand(handTrackingData.right, isLeft = false)
    }

    private fun processHand(handPose: HandPose?, isLeft: Boolean) {
        if (handPose == null) {
            setPinching(isLeft, false)
            return
        }

        val thumbTip = jointPosition(handPose, HandJoint.Index.THUMB_TIP)
        val indexTip = jointPosition(handPose, HandJoint.Index.INDEX_TIP)
        if (thumbTip == null || indexTip == null) {
            setPinching(isLeft, false)
            return
        }

        val distance = distance(thumbTip, indexTip)
        val wasPinching = if (isLeft) isLeftPinching else isRightPinching
        val isPinching = when {
            wasPinching -> distance < PINCH_OPEN_THRESHOLD
            else -> distance < PINCH_CLOSE_THRESHOLD
        }

        if (isPinching && !wasPinching) {
            onPinchDown(SystemClock.elapsedRealtime())
        }
        setPinching(isLeft, isPinching)
    }

    private fun onPinchDown(currentTime: Long) {
        if (currentTime - lastPinchTime < PINCH_COOLDOWN_MS) {
            return
        }
        lastPinchTime = currentTime

        pinchCount += 1
        startSequenceTimeout()
        Log.d(TAG, "Air pinch detected. Count: $pinchCount")

        if (pinchCount >= REQUIRED_PINCH_COUNT) {
            Log.d(TAG, "Double air pinch detected. Request text input.")
            resetPinchSequence()
            onDoublePinchDetected()
        }
    }

    private fun startSequenceTimeout() {
        resetRunnable?.let { mainHandler.removeCallbacks(it) }
        resetRunnable = Runnable {
            resetPinchSequence()
        }.also {
            mainHandler.postDelayed(it, DOUBLE_PINCH_TIMEOUT_MS)
        }
    }

    private fun resetPinchSequence() {
        pinchCount = 0
        resetRunnable?.let { mainHandler.removeCallbacks(it) }
        resetRunnable = null
    }

    private fun setPinching(isLeft: Boolean, isPinching: Boolean) {
        if (isLeft) {
            isLeftPinching = isPinching
        } else {
            isRightPinching = isPinching
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

    fun cleanup() {
        resetRunnable?.let { mainHandler.removeCallbacks(it) }
        resetRunnable = null
    }
}
