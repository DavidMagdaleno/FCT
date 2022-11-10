package com.example.proyectofinalfct

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivityMenuBinding


class Menu : AppCompatActivity() {
    lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,
            R.string.ok,
            R.string.cancel
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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
            val extraIntent = Intent(this,HorasExtra::class.java).apply {
                putExtra("email",email)
                //putExtra("Mod","None")
            }
            startActivity(extraIntent)
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
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}