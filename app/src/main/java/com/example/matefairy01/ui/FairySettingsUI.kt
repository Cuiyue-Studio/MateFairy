package com.example.matefairy01.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matefairy01.persona.FairyPersonalityCatalog
import com.example.matefairy01.persona.FairySoulProfile
import com.example.matefairy01.persona.PersonalityLevel
import com.example.matefairy01.persona.PersonalityTrait
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.design.TextField
import com.pico.spatial.ui.foundation.material.backgroundMaterial
import com.pico.spatial.ui.platform.Material

object FairySettingsUI {

    @Composable
    fun SettingsEntryButton(
        onClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .width(156.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .backgroundMaterial(enable = true, style = Material.Regular)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "设置精灵属性",
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun SettingsPanel(
        profile: FairySoulProfile,
        expandedPersonality: Boolean,
        isSaving: Boolean,
        statusMessage: String?,
        onFairyNameChange: (String) -> Unit,
        onUserAddressChange: (String) -> Unit,
        onTraitLevelSelected: (PersonalityTrait, PersonalityLevel) -> Unit,
        onTogglePersonality: () -> Unit,
        onSave: () -> Unit,
        onCancel: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .width(740.dp)
                .clip(RoundedCornerShape(32.dp))
                .backgroundMaterial(enable = true, style = Material.Regular)
                .padding(28.dp)
        ) {
            if (expandedPersonality) {
                PersonalityExpandedContent(
                    profile = profile,
                    onTraitLevelSelected = onTraitLevelSelected,
                    onCollapse = onTogglePersonality
                )
            } else {
                BasicSettingsContent(
                    profile = profile,
                    isSaving = isSaving,
                    statusMessage = statusMessage,
                    onFairyNameChange = onFairyNameChange,
                    onUserAddressChange = onUserAddressChange,
                    onTogglePersonality = onTogglePersonality,
                    onSave = onSave,
                    onCancel = onCancel
                )
            }
        }
    }

    @Composable
    private fun BasicSettingsContent(
        profile: FairySoulProfile,
        isSaving: Boolean,
        statusMessage: String?,
        onFairyNameChange: (String) -> Unit,
        onUserAddressChange: (String) -> Unit,
        onTogglePersonality: () -> Unit,
        onSave: () -> Unit,
        onCancel: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "精灵属性设置",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))
            LabeledTextField(
                label = "精灵名称",
                value = profile.fairyName,
                placeholder = "例如：MateFairy",
                onValueChange = onFairyNameChange
            )

            Spacer(modifier = Modifier.height(18.dp))
            LabeledTextField(
                label = "精灵对你的称呼",
                value = profile.userAddress,
                placeholder = "例如：你、主人、小伙伴",
                onValueChange = onUserAddressChange
            )

            Spacer(modifier = Modifier.height(22.dp))
            PersonalitySummaryCard(
                profile = profile,
                onTogglePersonality = onTogglePersonality
            )

            statusMessage?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCancel,
                    enabled = !isSaving
                ) {
                    Text("取消")
                }
                Button(
                    onClick = onSave,
                    enabled = !isSaving
                ) {
                    Text(if (isSaving) "保存中" else "保存")
                }
            }
        }
    }

    @Composable
    private fun PersonalityExpandedContent(
        profile: FairySoulProfile,
        onTraitLevelSelected: (PersonalityTrait, PersonalityLevel) -> Unit,
        onCollapse: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "精灵性格",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = onCollapse) {
                    Text("完成")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Column(
                modifier = Modifier
                    .height(448.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FairyPersonalityCatalog.traitSpecs.forEach { spec ->
                    TraitCard(
                        profile = profile,
                        trait = spec.trait,
                        onTraitLevelSelected = onTraitLevelSelected
                    )
                }
            }
        }
    }

    @Composable
    private fun LabeledTextField(
        label: String,
        value: String,
        placeholder: String,
        onValueChange: (String) -> Unit
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun PersonalitySummaryCard(
        profile: FairySoulProfile,
        onTogglePersonality: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "精灵性格",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = onTogglePersonality) {
                    Text("展开设置")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            FairyPersonalityCatalog.traitSpecs.forEach { spec ->
                val option = spec.option(profile.personality.get(spec.trait))
                Text(
                    text = "${spec.trait.title}：${option.displayTitle}",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }

    @Composable
    private fun TraitCard(
        profile: FairySoulProfile,
        trait: PersonalityTrait,
        onTraitLevelSelected: (PersonalityTrait, PersonalityLevel) -> Unit
    ) {
        val spec = FairyPersonalityCatalog.specFor(trait)
        val selectedLevel = profile.personality.get(trait)
        val selectedOption = spec.option(selectedLevel)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(16.dp)
        ) {
            Text(
                text = trait.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                spec.options.forEach { option ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (option.level == selectedLevel) {
                                    Color.White.copy(alpha = 0.22f)
                                } else {
                                    Color.White.copy(alpha = 0.08f)
                                }
                            )
                            .clickable {
                                onTraitLevelSelected(trait, option.level)
                            }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.displayTitle,
                            color = Color.White,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = selectedOption.description,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
