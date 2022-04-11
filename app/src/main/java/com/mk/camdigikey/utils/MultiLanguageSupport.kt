package com.mk.camdigikey.utils

import android.app.Application
import android.content.Context
import android.content.res.Configuration

class MultiLanguageSupport : Application () {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtils.updateLocal(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig!!)
        LocaleUtils.updateLocal(this)
    }
}