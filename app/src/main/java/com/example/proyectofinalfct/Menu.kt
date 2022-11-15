package com.example.proyectofinalfct


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivityMenuBinding
import com.google.android.material.navigation.NavigationView


class Menu : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    lateinit var binding: ActivityMenuBinding
    private lateinit var intenMenu:Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,
            R.string.ok,
            R.string.cancel
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()

        binding.imgCalendario.setOnClickListener {
            intenMenu = Intent(this,Calendario::class.java).apply {
                putExtra("email",email)
            }
            startActivity(intenMenu)
        }

        binding.imgFichar.setOnClickListener {
            //val ficharIntent = Intent(this,RegistroLaboral::class.java).apply {
            intenMenu = Intent(this,RegistroLaboral::class.java).apply {
                putExtra("email",email)
                //putExtra("Mod","None")
            }
            startActivity(intenMenu)
        }

        binding.imgHextra.setOnClickListener {
            intenMenu = Intent(this,HorasExtra::class.java).apply {
                putExtra("email",email)
                //putExtra("Mod","None")
            }
            startActivity(intenMenu)
        }

        binding.imgNotifi.setOnClickListener {
            /*intenMenu = Intent(this,Notificaciones::class.java).apply {
                putExtra("email",email)
                putExtra("Mod","None")
            }
            startActivity(intenMenu)*/
        }

        binding.imgVaca.setOnClickListener {
            intenMenu = Intent(this,SolicitarDias::class.java).apply {
                putExtra("email",email)
            }
            startActivity(intenMenu)
        }
        binding.imgMedico.setOnClickListener {
            intenMenu = Intent(this,Justificante::class.java).apply {
                putExtra("email",email)
                //putExtra("Mod","None")
            }
            startActivity(intenMenu)
        }

    }
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(@NonNull menuItem: MenuItem): Boolean {
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        when (menuItem.itemId) {
            R.id.opDatos -> intenMenu = Intent(this,DatosUsuario::class.java).apply { putExtra("email",email); putExtra("Mod","Modificar") }
            R.id.opJornada -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
            R.id.opExtra -> intenMenu = Intent(this,HorasExtra::class.java).apply { putExtra("email",email) }
            R.id.opCalendario -> intenMenu = Intent(this,Calendario::class.java).apply { putExtra("email",email) }
            R.id.opSolicitar -> intenMenu = Intent(this,SolicitarDias::class.java).apply { putExtra("email",email) }
            R.id.opJustifi -> intenMenu = Intent(this,Justificante::class.java).apply { putExtra("email",email) }
            //R.id.opNotifi -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
            else -> throw IllegalArgumentException("menu option not implemented!!")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        startActivity(intenMenu)
        return true
    }

}