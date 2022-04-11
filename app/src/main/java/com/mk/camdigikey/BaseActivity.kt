package com.mk.camdigikey

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val config = Configuration()
        applyOverrideConfiguration(config)
    }

    override fun applyOverrideConfiguration(newConfig: Configuration) {
        super.applyOverrideConfiguration(updateConfigurationIfSupported(newConfig))
    }

    open fun updateConfigurationIfSupported(config: Configuration): Configuration? {
        // Configuration.getLocales is added after 24 and Configuration.locale is deprecated in 24
        if (Build.VERSION.SDK_INT >= 24) {
            if (!config.locales.isEmpty) {
                return config
            }
        } else {
            if (config.locale != null) {
                return config
            }
        }
        // Please Get your language code from some storage like shared preferences
        val languageCode = "fa"
        val locale = Locale(languageCode)
        if (locale != null) {
            // Configuration.setLocale is added after 17 and Configuration.locale is deprecated
            // after 24
            if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(locale)
            } else {
                config.locale = locale
            }
        }
        return config
    }
}