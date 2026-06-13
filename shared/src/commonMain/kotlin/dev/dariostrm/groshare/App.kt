package dev.dariostrm.groshare

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.dariostrm.groshare.auth.LoginView
import dev.dariostrm.groshare.di.initializePlatform
import dev.dariostrm.groshare.di.sharedModule
import dev.dariostrm.groshare.theme.darkScheme
import dev.dariostrm.groshare.theme.lightScheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.KoinApplication
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@Serializable
data object Login : NavKey
@Serializable
data class Message(val message: String) : NavKey
private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Login::class, Login.serializer())
            subclass(Message::class, Message.serializer())
        }
    }
}

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
    val backStack = rememberNavBackStack(config, Login)
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
                entry<Login> {
                    LoginView(onLoggedIn = { backStack.add(Message("Logged In as $it")) })
                }
                entry<Message> { message ->
                    Column {
                        Text(message.message)
                        Button(onClick = { backStack.removeLastOrNull() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
        )
    }
}