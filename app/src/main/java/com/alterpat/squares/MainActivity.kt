package com.alterpat.squares

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingsBtn.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        reflexBtn.setOnClickListener {
            startActivity(Intent(this, GameReflexActivity::class.java))
        }

        memoryBtn.setOnClickListener {
            startActivity(Intent(this, GameMemoryActivity::class.java))
        }
    }
}