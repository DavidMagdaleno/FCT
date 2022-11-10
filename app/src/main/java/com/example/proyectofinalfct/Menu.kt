package com.example.proyectofinalfct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectofinalfct.databinding.ActivityDatosUsuarioBinding
import com.example.proyectofinalfct.databinding.ActivityMenuBinding

class Menu : AppCompatActivity() {
    lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()

        binding.imgCalendario.setOnClickListener {
            /*val calendarIntent = Intent(this,Calendario::class.java).apply {
                putExtra("email",email)
                putExtra("Mod","None")
            }
            startActivity(calendarIntent)*/
        }

        binding.imgFichar.setOnClickListener {
            val ficharIntent = Intent(this,RegistroLaboral::class.java).apply {
                putExtra("email",email)
                //putExtra("Mod","None")
            }
            startActivity(ficharIntent)
        }

        binding.imgHextra.setOnClickListener {
            /*val extraIntent = Intent(this,Horasextra::class.java).apply {
                putExtra("email",email)
                putExtra("Mod","None")
            }
            startActivity(extraIntent)*/
        }

        binding.imgNotifi.setOnClickListener {
            /*val notifiIntent = Intent(this,Notificaciones::class.java).apply {
                putExtra("email",email)
                putExtra("Mod","None")
            }
            startActivity(notifiIntent)*/
        }

        binding.imgVaca.setOnClickListener {
            /*val vacaIntent = Intent(this,Vacaciones::class.java).apply {
                putExtra("email",email)
                putExtra("Mod","None")
            }
            startActivity(vacaIntent)*/
        }
        binding.imgMedico.setOnClickListener {
            /*val docIntent = Intent(this,fAsistencia::class.java).apply {
                putExtra("email",email)
                putExtra("Mod","None")
            }
            startActivity(docIntent)*/
        }

    }
}