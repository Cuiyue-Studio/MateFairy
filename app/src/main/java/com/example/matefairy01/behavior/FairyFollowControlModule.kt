package com.example.matefairy01.behavior

import android.util.Log
import com.example.matefairy01.interaction.InteractionActionListener
import com.example.matefairy01.interaction.InteractionActionSource
import com.example.matefairy01.interaction.InteractionActionStatus

/**
 * 跟随逻辑的 action 生命周期订阅者。
 *
 * action 执行期间关闭跟随/随机巡航控制，结束后恢复。后续如果只想监听部分 action，
 * 可在注册 listener 时传入 actionId 过滤集合。
 */
object FairyFollowControlModule : InteractionActionListener {
    @Volatile
    var isFollowEnabled: Boolean = true
        private set

    override fun onActionStarted(
        actionId: String,
        controllerId: String,
        source: InteractionActionSource
    ) {
        isFollowEnabled = false
        Log.d(TAG, "Disable fairy follow while action is running: $actionId")
    }

    override fun onActionFinished(
        actionId: String,
        controllerId: String,
        source: InteractionActionSource,
        status: InteractionActionStatus
    ) {
        isFollowEnabled = true
        Log.d(TAG, "Restore fairy follow after action=$actionId, status=$status")
    }

    private const val TAG = "FairyFollowControl"
}

