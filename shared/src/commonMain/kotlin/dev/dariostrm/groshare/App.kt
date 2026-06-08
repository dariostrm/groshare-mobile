package dev.dariostrm.groshare

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.dariostrm.groshare.auth.LoginView
import dev.dariostrm.groshare.auth.LoginViewModel
import org.jetbrains.compose.resources.painterResource

import groshare.shared.generated.resources.Res
import groshare.shared.generated.resources.compose_multiplatform
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data object Home : NavKey
@Serializable
data class Message(val message: String) : NavKey
private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Home::class, Home.serializer())
            subclass(Message::class, Message.serializer())
        }
    }
}

@Composable
fun TestApp() {
    MaterialTheme {
        LoginView(
            viewModel = remember { LoginViewModel() },
            onLoggedIn = {}
        )
    }
}
@Composable
@Preview
fun App() {
    TestApp()
    return
    MaterialTheme {
        val backStack = rememberNavBackStack(config, Home)
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NavDisplay(
                backStack = backStack,
                modifier = Modifier.fillMaxSize(),
                entryProvider = entryProvider {
                    entry<Home> {
                        Column {
                            Text("Home Screen")
                            Button(onClick = { backStack.add(Message("Test message")) }) {
                                Text("Test")
                            }
                        }
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
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
        }
    }
}