package com.example.matefairy01.memory.permanent

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.matefairy01.persona.FairySoulProfile
import com.example.matefairy01.persona.PersonalityLevel
import com.example.matefairy01.persona.PersonalitySelections
import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class PermanentStoreSoulProfileTest {
    private lateinit var context: Context
    private lateinit var memoryDir: File

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        memoryDir = File(context.filesDir, "memory")
        memoryDir.deleteRecursively()
    }

    @After
    fun tearDown() {
        memoryDir.deleteRecursively()
    }

    @Test
    fun writeSoulProfile_keepsUppercaseSoulFileAndRoundTrips() {
        val store = PermanentStore(context)
        val profile = FairySoulProfile(
            fairyName = "露米",
            userAddress = "小伙伴",
            personality = PersonalitySelections(
                extraversion = PersonalityLevel.EXTREME,
                conscientiousness = PersonalityLevel.HIGH
            )
        )

        store.ensureInitialized()
        store.writeSoulProfile(profile)

        val soulFile = File(memoryDir, MdTemplates.FILE_SOUL)
        assertTrue("SOUL.md 应继续作为人设文件名", soulFile.exists())
        assertEquals(profile.normalized(), store.readSoulProfile())
        assertTrue(store.readSoul().contains("外向性：超强（亢奋型）"))
    }
}
