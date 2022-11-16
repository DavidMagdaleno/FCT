package com.example.proyectofinalfct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var intentVS=Intent(this,MainActivity::class.java)
        startActivity(intentVS)
        finish()
    }
}