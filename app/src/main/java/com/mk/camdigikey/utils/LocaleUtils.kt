package com.mk.camdigikey.utils

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import java.util.*

public class LocaleUtils(base: Context?) : ContextWrapper(base) {

    companion object {

        const val KHMER = "km"
        const val ENGLISH = "en"

        // Set locale code into SharedPreferences
        public fun setLocalCode(context: Context, localCode: String) {
            val prefs = context.getSharedPreferences("language", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("lang", localCode)
            editor.apply()

//            val locale = Locale(localCode)
//            Locale.setDefault(locale)
//            val resources =  context.applicationContext.getResources()
//            val config = resources.getConfiguration()
//            config.setLocale(locale)
//            resources.updateConfiguration(config, resources.getDisplayMetrics())
            updateLocal(context)
        }

        // Get locale obj based on locale on in SharedPreferences
        fun getLocal(context: Context): Locale {
            val localCode = context
                .getSharedPreferences("language", MODE_PRIVATE)
                .getString("lang", KHMER)

            return Locale(localCode ?: KHMER)
        }

        fun updateLocal(context: Context): ContextWrapper {

            // 1.Create resource object
            val resource = context.resources

            // 2. create config object
            val config = resource.configuration

            config.setLocale(getLocal(context))
            config.setLayoutDirection(getLocal(context))

            return LocaleUtils(context.createConfigurationContext(config))
        }
    }
}