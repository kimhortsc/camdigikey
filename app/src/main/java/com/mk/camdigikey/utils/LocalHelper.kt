package com.mk.camdigikey.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*


class LocalHelper {
    companion object {
        private var context: Context? = null
        private var sharedPreferences: SharedPreferences? = null

        fun LocalHelper(context: Context) {
            this.context = context
            sharedPreferences = context.getSharedPreferences("language", Context.MODE_PRIVATE)
        }

        fun updateResource(code: String?) {
            val locale = Locale(code)
            Locale.setDefault(locale)
            val resources = context?.resources
            val configuration = resources?.configuration
            configuration?.setLocale(locale)
            resources?.updateConfiguration(configuration, resources.displayMetrics)
            setLang(code)
        }

        private fun setLang(code: String?) {
            val editor = sharedPreferences!!.edit()
            editor.putString("lang", code)
            editor.apply()
        }

        fun getLang(): String? {
            return sharedPreferences!!.getString("lang", "en")
        }
    }
}