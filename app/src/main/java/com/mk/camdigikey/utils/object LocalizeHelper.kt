package com.mk.camdigikey.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

object LocalizeHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private const val USER_PREFER_LANGUAGE_HAS_SET = "Locale.Helper.User.Prefer.Language.Set"
    private const val LANG_KEY = "language"

    fun onAttach(context: Context): Context {
        if (userPreferLanguageHasSet(context)) {
            val lang = getPersistedData(context, Locale.getDefault().language)
            return setLocale(context, lang)
        }
        return context
    }


    fun getLanguage(context: Context): String {
        return getPersistedData(context, Locale.getDefault().language)
    }


    fun setLocale(context: Context, language: String): Context {
        persist(context, language)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            updateResources(context, language)
//            updateResourcesLegacy(context, language)
        }
        else updateResourcesLegacy(context, language)
    }

    fun userPreferLanguageHasSet(context: Context): Boolean {
        val preferences = context.getSharedPreferences(LANG_KEY, MODE_PRIVATE)
        return preferences.getBoolean(USER_PREFER_LANGUAGE_HAS_SET, false)
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String {
        val preferences = context.getSharedPreferences(LANG_KEY, MODE_PRIVATE)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)!!
    }

    private fun persist(context: Context, language: String?) {
        val preferences = context.getSharedPreferences(LANG_KEY, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.putBoolean(USER_PREFER_LANGUAGE_HAS_SET, true)
        editor.apply()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        val mContext = context.createConfigurationContext(configuration)
        context.resources.apply {
            this.updateConfiguration(configuration, this.displayMetrics)
        }

        return mContext
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}