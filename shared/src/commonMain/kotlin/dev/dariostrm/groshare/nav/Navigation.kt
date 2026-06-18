package dev.dariostrm.groshare.nav

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data object HomePage : NavKey
@Serializable
data object LoginPage : NavKey

val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomePage::class, HomePage.serializer())
            subclass(LoginPage::class, LoginPage.serializer())
        }
    }
}