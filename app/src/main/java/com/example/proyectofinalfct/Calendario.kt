package com.example.proyectofinalfct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.proyectofinalfct.databinding.ActivityCalendarioBinding
import java.text.SimpleDateFormat
import java.util.*

class Calendario : AppCompatActivity() {
    lateinit var binding:ActivityCalendarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sdf = SimpleDateFormat("yyyy", Locale("es", "ES"))
        val currentyear = sdf.format(Date())
        Log.e("año actual",currentyear)//-----------------------------------------------------------------
        //binding.cvLaboral.minDate=stringtodate("01/01/"+currentyear).time


    }

    fun stringtodate(t:String): Date {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
        val datep = formatter.parse(t)
        return datep
    }
}