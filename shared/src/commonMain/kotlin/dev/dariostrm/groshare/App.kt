package dev.dariostrm.groshare

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.dariostrm.groshare.auth.AuthService
import dev.dariostrm.groshare.auth.AuthState
import dev.dariostrm.groshare.auth.LoginView
import dev.dariostrm.groshare.di.initializePlatform
import dev.dariostrm.groshare.di.sharedModule
import dev.dariostrm.groshare.home.HomeView
import dev.dariostrm.groshare.shared.NetworkHealthStore
import dev.dariostrm.groshare.shared.ifError
import dev.dariostrm.groshare.settings.Settings
import dev.dariostrm.groshare.settings.value
import dev.dariostrm.groshare.theme.darkScheme
import dev.dariostrm.groshare.theme.lightScheme
import groshare.shared.generated.resources.Res
import groshare.shared.generated.resources.ic_error_filled
import org.jetbrains.compose.resources.painterResource
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
        ActualApp()
    }
}

@Composable
fun ActualApp(
    networkHealthStore: NetworkHealthStore = koinInject(),
    authService: AuthService = koinInject(),
    settings: Settings = koinInject()
) {
    val authState by authService.state.collectAsState()
    val networkStatus by networkHealthStore.networkHealth.collectAsState()
    val isDarkModeSetting by settings.isDarkMode.state.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isDarkMode = isDarkModeSetting ?: isSystemDark

    MaterialTheme(
        colorScheme = if (isDarkMode) darkScheme else lightScheme
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = !networkStatus,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onError)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_error_filled),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Server unreachable, check your internet connection",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            LaunchedEffect(Unit) {
                authService.verifySession()
            }
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AnimatedContent(
                    targetState = authState,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { targetAuthState ->
                    when (targetAuthState) {
                        is AuthState.Loading -> LoadingView()
                        is AuthState.SignedIn -> HomeView()
                        is AuthState.SignedOut -> LoginView()
                    }
                }
            }
        }
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