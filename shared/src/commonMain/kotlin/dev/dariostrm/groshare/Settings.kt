package dev.dariostrm.groshare

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeWriteMode
import kotlinx.coroutines.CoroutineScope

class Settings(
    private val ksafe: KSafe,
    private val appScope: CoroutineScope
) {

    private inline fun <reified T> define(key: String, defaultValue: T): KSafeSetting<T> {
        return KSafeSetting<T>(
            ksafe = ksafe,
            key = key,
            defaultValue = defaultValue,
            scope = appScope,
            mode = KSafeWriteMode.Plain)
    }

}