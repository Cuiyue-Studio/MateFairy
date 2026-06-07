package com.example.matefairy01.interaction

object PlayFootballPreconditions {
    const val MAX_HORIZONTAL_DISTANCE_METERS = 10f
    const val TOO_FAR_REPLY = "球太远了，我找不到了"

    @Volatile
    private var latestHorizontalDistance: Float? = null

    fun updateHorizontalDistance(distance: Float) {
        latestHorizontalDistance = distance
    }

    fun clear() {
        latestHorizontalDistance = null
    }

    fun canActivate(): Boolean {
        return latestHorizontalDistance?.let { it <= MAX_HORIZONTAL_DISTANCE_METERS } ?: true
    }
}

