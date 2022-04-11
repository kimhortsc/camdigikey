package com.mk.camdigikey

import android.app.Application
import android.content.Context
import com.mk.camdigikey.utils.LocalizeHelper

class App : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocalizeHelper.onAttach(base))
    }
}