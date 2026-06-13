package dev.dariostrm.groshare

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeWriteMode
import eu.anifantakis.lib.ksafe.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

data class KSafeSetting<T>(
    val key: String,
    val state: StateFlow<T>,
    val setAndForget: (T) -> Unit,
    val set: suspend (T) -> Unit
) {
    companion object {
        inline operator fun <reified T> invoke(
            ksafe: KSafe,
            key: String,
            defaultValue: T,
            scope: CoroutineScope,
            mode: KSafeWriteMode
        ): KSafeSetting<T> {
            val stateFlow by ksafe.asStateFlow<T>(defaultValue, scope, key = key)
            return KSafeSetting(
                key = key,
                state = stateFlow,
                setAndForget = { ksafe.putDirect(key, it, mode = mode ) },
                set = {
                    ksafe.put(key, it)
                    println("Intended ${ksafe.protectionInfo.intendedLevel}, effective level ${ksafe.protectionInfo.effectiveLevel}")
                    println(ksafe.getKeyInfo(key))
                }
            )
        }
    }
}

val <T> KSafeSetting<T>.value: T
    get() = this.state.value

class SecureSettings(
    private val ksafe: KSafe,
    private val appScope: CoroutineScope
) {

    private inline fun <reified T> define(key: String, defaultValue: T): KSafeSetting<T> {
        return KSafeSetting<T>(
            ksafe = ksafe,
            key = key,
            defaultValue = defaultValue,
            scope = appScope,
            mode = KSafeWriteMode.Encrypted())
    }
    val authToken = define<String?>("authToken", null)
}
