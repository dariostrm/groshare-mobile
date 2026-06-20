package dev.dariostrm.groshare

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.dariostrm.groshare.auth.AuthService
import dev.dariostrm.groshare.auth.AuthState
import dev.dariostrm.groshare.auth.LoginView
import dev.dariostrm.groshare.di.initializePlatform
import dev.dariostrm.groshare.di.sharedModule
import dev.dariostrm.groshare.home.HomeView
import dev.dariostrm.groshare.shared.ifError
import dev.dariostrm.groshare.theme.darkScheme
import dev.dariostrm.groshare.theme.lightScheme
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@Composable
fun TestApp() {
    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
    ) {
        LoginView()
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
    val authService = koinInject<AuthService>()
    val authState by authService.state.collectAsState()
    LaunchedEffect(Unit) {
        authService.verifySession()
    }
    when (authState) {
        is AuthState.Loading -> LoadingView()
        is AuthState.SignedIn -> HomeView()
        is AuthState.SignedOut -> LoginView()
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}