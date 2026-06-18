package dev.dariostrm.groshare

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dev.dariostrm.groshare.auth.AuthService
import dev.dariostrm.groshare.auth.LoginView
import dev.dariostrm.groshare.di.initializePlatform
import dev.dariostrm.groshare.di.sharedModule
import dev.dariostrm.groshare.home.HomeView
import dev.dariostrm.groshare.nav.HomePage
import dev.dariostrm.groshare.nav.LoginPage
import dev.dariostrm.groshare.nav.navConfig
import dev.dariostrm.groshare.settings.SecureSettings
import dev.dariostrm.groshare.settings.value
import dev.dariostrm.groshare.theme.darkScheme
import dev.dariostrm.groshare.theme.lightScheme
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun TestApp() {
    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
    ) {
        LoginView(
            onLoggedIn = {}
        )
    }
}
@Composable
@Preview
fun App(
    lightColorSchemeOverride: ColorScheme? = null,
    darkColorSchemeOverride: ColorScheme? = null,
) {
    @OptIn(KoinExperimentalAPI::class)
    KoinMultiplatformApplication(
        config = koinConfiguration {
            modules(sharedModule + initializePlatform())
        }
    ) {
        val colorScheme =
            if (isSystemInDarkTheme()) darkColorSchemeOverride ?: darkScheme
            else lightColorSchemeOverride ?: lightScheme
        MaterialTheme(
            colorScheme = colorScheme,
        ) {
            Surface {
                //TestApp()
                ActualApp()
            }
        }
    }
}

@Composable
fun ActualApp() {
    val secureSettings = koinInject<SecureSettings>()
    val startPage = if(secureSettings.authToken.value == null) LoginPage else HomePage
    val backStack = rememberNavBackStack(navConfig, startPage)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
    ) {
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            entryProvider = entryProvider {
                entry<LoginPage> {
                    LoginView(onLoggedIn = {
                        backStack.clear()
                        backStack.add(HomePage)
                    })
                }
                entry<HomePage> { HomeView() }
            }
        )
    }
}