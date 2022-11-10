package com.example.proyectofinalfct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectofinalfct.databinding.ActivityHorasExtraBinding
import com.example.proyectofinalfct.databinding.ActivityRegistroLaboralBinding

class HorasExtra : AppCompatActivity() {
    lateinit var binding: ActivityHorasExtraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHorasExtraBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}