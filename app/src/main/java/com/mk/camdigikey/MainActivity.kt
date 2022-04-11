package com.mk.camdigikey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mk.camdigikey.utils.LocaleUtils
import com.mk.camdigikey.utils.LocalizeHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnRegister: Button
    private lateinit var btnSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init ui element
        init()

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    private fun init(){
        btnRegister = findViewById(R.id.btn_register)
        btnSettings = findViewById(R.id.btn_settings)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.updateLocal(newBase))
    }
}