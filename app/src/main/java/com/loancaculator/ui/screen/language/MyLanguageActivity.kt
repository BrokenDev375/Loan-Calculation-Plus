package com.loancaculator.ui.screen.language

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.brian.base_application.language.LanguageRouter
import com.loancaculator.core.AppStorage
import com.loancaculator.ui.theme.AppTheme

class MyLanguageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialLanguageCode = AppStorage.languageCode(this)
        val isFromSettings = intent.getBooleanExtra(EXTRA_FROM_SETTINGS, false)

        onBackPressedDispatcher.addCallback(this) {
            if (isFromSettings) {
                finish()
            } else {
                LanguageRouter.confirmLanguageSelection(this@MyLanguageActivity, initialLanguageCode)
            }
        }

        setContent {
            AppTheme {
                LanguageScreen(
                    initialCode = initialLanguageCode,
                    onDone = { selectedCode ->
                        if (isFromSettings) {
                            LanguageRouter.confirmLanguageSelection(this, selectedCode, navigate = false)
                            finish()
                        } else {
                            LanguageRouter.confirmLanguageSelection(this, selectedCode)
                        }
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_FROM_SETTINGS = "extra_from_settings"
    }
}
