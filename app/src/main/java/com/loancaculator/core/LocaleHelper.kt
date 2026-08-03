package com.loancaculator.core

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * Áp per-app locale — mirror vga_48 (uti/LocaleHelper.kt).
 * API 33+ (TIRAMISU): LocaleManager.applicationLocales; cũ hơn: AppCompatDelegate.setApplicationLocales.
 */
object LocaleHelper {
    fun updateLocale(context: Context, languageCode: String) {
        if (languageCode.isBlank()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }

    fun wrap(context: Context, languageCode: String = AppStorage.languageCode(context)): Context {
        if (languageCode.isBlank()) return context

        val locale = languageCode.toLocale()
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        configuration.setLayoutDirection(Locale.ENGLISH)
        return context.createConfigurationContext(configuration)
    }

    private fun String.toLocale(): Locale {
        val languageTag = replace('_', '-')
            .let { tag ->
                when (tag) {
                    "en-UK" -> "en-GB"
                    "iw-IL" -> "he-IL"
                    "in" -> "id"
                    else -> tag
                }
            }
        return Locale.forLanguageTag(languageTag)
    }
}
