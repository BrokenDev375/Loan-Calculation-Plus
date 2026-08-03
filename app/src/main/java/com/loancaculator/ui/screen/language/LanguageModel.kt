package com.loancaculator.ui.screen.language

import androidx.annotation.DrawableRes
import com.loancaculator.R

data class LanguageItem(
    val code: String,
    val nativeName: String,
    val englishName: String,
    @DrawableRes val flagRes: Int,
)

object SupportedLanguages {
    val list = listOf(
        LanguageItem("en", "English (US)", "English", R.drawable.ic_flag_us),
        LanguageItem("vi", "Tiếng Việt", "Vietnamese", R.drawable.ic_flag_vi),
        LanguageItem("es", "Español", "Spanish", R.drawable.ic_flag_es),
        LanguageItem("pt", "Português", "Portuguese", R.drawable.ic_flag_pt),
        LanguageItem("de", "Deutsch", "German", R.drawable.ic_flag_de),
        LanguageItem("fr", "Français", "French", R.drawable.ic_flag_fr),
        LanguageItem("id", "Bahasa Indonesia", "Indonesian", R.drawable.ic_flag_id),
        LanguageItem("ar", "عربي", "Arabic", R.drawable.ic_flag_ar),
        LanguageItem("ja", "日本語", "Japanese", R.drawable.ic_flag_ja),
        LanguageItem("ko", "한국어", "Korean", R.drawable.ic_flag_ko),
        LanguageItem("hi", "हिन्दी", "Hindi", R.drawable.ic_flag_hi),
        LanguageItem("th", "ไทย", "Thai", R.drawable.ic_flag_th),
        LanguageItem("zh", "简体中文", "Chinese (Simplified)", R.drawable.ic_flag_zh),
    )
}
