package com.mk.camdigikey

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mk.camdigikey.utils.LocaleUtils
import com.mk.camdigikey.utils.LocalizeHelper

class SettingActivity : AppCompatActivity () {

    private lateinit var toolbar: Toolbar
    private lateinit var tvLanguage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // initialize ui element
        init()

        // get lang choice
        val langChoices = resources.getStringArray(R.array.lang_choice)

        toolbar.setNavigationOnClickListener { finish() }
        tvLanguage.setOnClickListener {
            val builder = AlertDialog.Builder(this@SettingActivity)
            builder.setTitle("Choose Language")

            val chosenLang = if (LocaleUtils.getLocal(this).language == "km") 0 else 1

            builder.setSingleChoiceItems(langChoices, chosenLang) { dialogInterface, i ->
                if( i == 0) {
                    LocaleUtils.setLocalCode(this, LocaleUtils.KHMER)

                   // LocalizeHelper.setLocale(this, "km")
                    dialogInterface.dismiss()
                    recreate()
                } else{
                     LocaleUtils.setLocalCode(this, LocaleUtils.ENGLISH)
                    // LocalizeHelper.setLocale(this, "en")
                    dialogInterface.dismiss()
                    recreate()
                }
            }

            builder.create().show()

            // recreate activity after choosing lang
        }
    }

    private fun init(){
        toolbar = findViewById(R.id.toolbar)
        tvLanguage = findViewById(R.id.tv_language)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.updateLocal(newBase))
    }
}