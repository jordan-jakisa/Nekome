package com.chesire.nekome.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.chesire.nekome.core.flags.UserSeriesStatus
import com.chesire.nekome.core.preferences.flags.HomeScreenOptions
import com.chesire.nekome.core.preferences.flags.Theme
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationPreferencesTests {
    private val mockContext = mockk<Context> {
        every { getString(R.string.key_default_home) } returns "key_default_home"
        every { getString(R.string.key_default_series_state) } returns "key_default_series_state"
        every { getString(R.string.key_theme) } returns "key_theme"
    }

    @Test
    fun `can get defaultHomeScreen`() {
        val mockPreferences = mockk<SharedPreferences> {
            every { getString("key_default_home", "0") } returns "1"
        }
        val testObject = ApplicationPreferences(
            mockContext,
            mockPreferences
        )

        assertEquals(HomeScreenOptions.Manga, testObject.defaultHomeScreen)
    }

    @Test
    fun `defaultHomeScreen with Unknown value returns Anime`() {
        val mockEditor = mockk<SharedPreferences.Editor> {
            every { putString("key_default_home", "0") } returns this
            every { apply() } just Runs
        }
        val mockPreferences = mockk<SharedPreferences> {
            every { getString("key_default_home", "0") } returns "-1"
            every { edit() } returns mockEditor
        }
        val testObject = ApplicationPreferences(
            mockContext,
            mockPreferences
        )

        val result = testObject.defaultHomeScreen

        assertEquals(HomeScreenOptions.Anime, result)
    }

    @Test
    fun `can set defaultHomeScreen`() {
        val mockEditor = mockk<SharedPreferences.Editor> {
            every { putString("key_default_home", any()) } returns this
            every { apply() } just Runs
        }
        val mockPreferences = mockk<SharedPreferences> {
            every { getString("key_default_home", "0") } returns "-1"
            every { edit() } returns mockEditor
        }
        val testObject = ApplicationPreferences(
            mockContext,
            mockPreferences
        )

        testObject.defaultHomeScreen = HomeScreenOptions.Manga

        verify {
            mockEditor.putString(
                "key_default_home",
                HomeScreenOptions.Manga.index.toString()
            )
        }
    }

    @Test
    fun `can get defaultSeriesState`() {
        val mockPreferences = mockk<SharedPreferences> {
            every { getString("key_default_series_state", "0") } returns "2"
        }
        val testObject = ApplicationPreferences(
            mockContext,
            mockPreferences
        )

        assertEquals(UserSeriesStatus.OnHold, testObject.defaultSeriesState)
    }

    @Test
    fun `defaultSeriesState with Unknown value resets preference value to Current`() {
        val mockEditor = mockk<SharedPreferences.Editor> {
            every { putString("key_default_series_state", "0") } returns this
            every { apply() } just Runs
        }
        val mockPreferences = mockk<SharedPreferences> {
            every { getString("key_default_series_state", "0") } returns "-1"
            every { edit() } returns mockEditor
        }
        val testObject = ApplicationPreferences(
            mockContext,
            mockPreferences
        )

        testObject.defaultSeriesState

        verify { mockEditor.putString("key_default_series_state", "0") }
    }

    @Test
    fun `defaultSeriesState with Unknown value returns Current`() {
        val mockEditor = mockk<SharedPreferences.Editor> {
            every { putString("key_default_series_state", "0") } returns this
            every { apply() } just Runs
        }
        val mockPreferences = mockk<SharedPreferences> {
            every { getString("key_default_series_state", "0") } returns "-1"
            every { edit() } returns mockEditor
        }
        val testObject = ApplicationPreferences(
            mockContext,
            mockPreferences
        )

        val result = testObject.defaultSeriesState

        assertEquals(UserSeriesStatus.Current, result)
    }

    @Test
    fun `can get theme`() {
        val mockPreferences = mockk<SharedPreferences> {
            every { getString("key_theme", "-1") } returns "2"
        }
        val testObject = ApplicationPreferences(
            mockContext,
            mockPreferences
        )

        assertEquals(Theme.Dark, testObject.theme)
    }
}
