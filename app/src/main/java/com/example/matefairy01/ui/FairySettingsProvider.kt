package com.example.matefairy01.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.matefairy01.persona.FairySoulProfile
import com.example.matefairy01.persona.PersonalityLevel
import com.example.matefairy01.persona.PersonalityTrait

/**
 * 精灵属性设置面板的 UI 状态。
 *
 * 只保存 Compose 可观察状态，不直接做文件 IO，避免全局 UI 状态绕过 runtime 持久层。
 */
class FairySettingsProvider {
    var currentProfile by mutableStateOf(FairySoulProfile.default())
        private set

    var draftProfile by mutableStateOf(FairySoulProfile.default())
        private set

    var showSettingsPanel by mutableStateOf(false)
        private set

    var isPersonalityExpanded by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var statusMessage by mutableStateOf<String?>(null)
        private set

    fun loadProfile(profile: FairySoulProfile) {
        val normalized = profile.normalized()
        currentProfile = normalized
        if (!showSettingsPanel && !isSaving) {
            draftProfile = normalized
        }
    }

    fun openPanel() {
        draftProfile = currentProfile
        showSettingsPanel = true
        isPersonalityExpanded = false
        statusMessage = null
    }

    fun closePanel() {
        showSettingsPanel = false
        isPersonalityExpanded = false
        draftProfile = currentProfile
        statusMessage = null
        isSaving = false
    }

    fun togglePersonalityExpanded() {
        isPersonalityExpanded = !isPersonalityExpanded
        statusMessage = null
    }

    fun updateFairyName(value: String) {
        draftProfile = draftProfile.copy(fairyName = value)
        statusMessage = null
    }

    fun updateUserAddress(value: String) {
        draftProfile = draftProfile.copy(userAddress = value)
        statusMessage = null
    }

    fun selectPersonalityLevel(trait: PersonalityTrait, level: PersonalityLevel) {
        draftProfile = draftProfile.copy(
            personality = draftProfile.personality.with(trait, level)
        )
        statusMessage = null
    }

    fun startSaving() {
        isSaving = true
        statusMessage = "正在保存..."
    }

    fun markSaved(profile: FairySoulProfile) {
        val normalized = profile.normalized()
        currentProfile = normalized
        draftProfile = normalized
        isSaving = false
        statusMessage = "已保存，下一次回复开始生效"
    }

    fun markSaveFailed(message: String) {
        isSaving = false
        statusMessage = message
    }
}
