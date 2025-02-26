package com.chesire.nekome

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.chesire.nekome.core.preferences.ApplicationPreferences
import com.chesire.nekome.core.preferences.flags.HomeScreenOptions
import com.chesire.nekome.datasource.auth.AccessTokenRepository
import com.chesire.nekome.datasource.user.UserRepository
import com.chesire.nekome.testing.CoroutinesMainDispatcherRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ActivityViewModelTests {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutinesMainDispatcherRule()
    private val testDispatcher = coroutineRule.testDispatcher

    private val settings = mockk<ApplicationPreferences>(relaxed = true)

    @Test
    fun `userLoggedIn failure returns false`() {
        val mockAccessTokenRepository = mockk<AccessTokenRepository> {
            every { accessToken } returns ""
        }
        val mockLogoutHandler = mockk<LogoutHandler>()
        val mockUserRepository = mockk<UserRepository> {
            every { user } returns mockk()
        }

        val classUnderTest = ActivityViewModel(
            mockAccessTokenRepository,
            mockLogoutHandler,
            settings,
            testDispatcher,
            mockUserRepository
        )

        assertFalse(classUnderTest.userLoggedIn)
    }

    @Test
    fun `userLoggedIn success returns true`() {
        val mockAccessTokenRepository = mockk<AccessTokenRepository> {
            every { accessToken } returns "access token"
        }
        val mockLogoutHandler = mockk<LogoutHandler>()
        val mockUserRepository = mockk<UserRepository> {
            every { user } returns mockk()
        }

        val classUnderTest = ActivityViewModel(
            mockAccessTokenRepository,
            mockLogoutHandler,
            settings,
            testDispatcher,
            mockUserRepository
        )

        assertTrue(classUnderTest.userLoggedIn)
    }

    @Test
    fun `logout tells the logoutHandler to execute`() {
        val mockAccessTokenRepository = mockk<AccessTokenRepository>() {
            every { accessToken } returns "access token"
        }
        val mockLogoutHandler = mockk<LogoutHandler> {
            every { executeLogout() } just Runs
        }
        val mockUserRepository = mockk<UserRepository> {
            every { user } returns mockk()
        }

        val classUnderTest = ActivityViewModel(
            mockAccessTokenRepository,
            mockLogoutHandler,
            settings,
            testDispatcher,
            mockUserRepository
        )

        classUnderTest.logout()

        verify { mockLogoutHandler.executeLogout() }
    }

    @Test
    fun `if user is not logged in then details screen navigation event is emitted`() {
        val mockAccessTokenRepository = mockk<AccessTokenRepository>() {
            every { accessToken } returns ""
        }
        val mockLogoutHandler = mockk<LogoutHandler> {
            every { executeLogout() } just Runs
        }
        val mockUserRepository = mockk<UserRepository> {
            every { user } returns mockk()
        }

        val classUnderTest = ActivityViewModel(
            mockAccessTokenRepository,
            mockLogoutHandler,
            settings,
            testDispatcher,
            mockUserRepository
        )

        assertTrue(
            classUnderTest.navigation.value == OverviewNavGraphDirections.globalToLoginFlow()
        )
    }

    @Test
    fun `if user is logged in then home screen navigation event is emitted`() {
        val mockAccessTokenRepository = mockk<AccessTokenRepository>() {
            every { accessToken } returns "khinkali"
        }

        val mockUserRepository = mockk<UserRepository> {
            every { user } returns mockk()
        }

        every { settings.defaultHomeScreen } returns HomeScreenOptions.Anime

        val classUnderTest = ActivityViewModel(
            mockAccessTokenRepository,
            mockk(),
            settings,
            testDispatcher,
            mockUserRepository
        )

        assertTrue(
            classUnderTest.navigation.value == OverviewNavGraphDirections.globalToAnimeFragment()
        )
    }

    @Test
    fun `if user is logged in and has default home screen changed then manga screen navigation event is emitted`() {
        val mockAccessTokenRepository = mockk<AccessTokenRepository>() {
            every { accessToken } returns "FuHuaBestWaifu"
        }

        val mockUserRepository = mockk<UserRepository> {
            every { user } returns mockk()
        }

        every { settings.defaultHomeScreen } returns HomeScreenOptions.Manga

        val classUnderTest = ActivityViewModel(
            mockAccessTokenRepository,
            mockk(),
            settings,
            testDispatcher,
            mockUserRepository
        )

        assertTrue(
            classUnderTest.navigation.value == OverviewNavGraphDirections.globalToMangaFragment()
        )
    }
}
