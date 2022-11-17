package com.example.proyectofinalfct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivityNotificacionBinding
import com.google.android.material.navigation.NavigationView

class Notificacion : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityNotificacionBinding
    private lateinit var intenMenu: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotificacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,
            R.string.ok,
            R.string.cancel
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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
        intenMenu = when (menuItem.itemId) {
            R.id.opDatos -> Intent(this,DatosUsuario::class.java).apply { putExtra("email",email); putExtra("Mod","Modificar") }
            R.id.opJornada -> Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
            R.id.opExtra -> Intent(this,HorasExtra::class.java).apply { putExtra("email",email) }
            R.id.opCalendario -> Intent(this,Calendario::class.java).apply { putExtra("email",email) }
            R.id.opSolicitar -> Intent(this,SolicitarDias::class.java).apply { putExtra("email",email) }
            R.id.opJustifi -> Intent(this,Justificante::class.java).apply { putExtra("email",email) }
            R.id.opNotifi -> Intent(this,Notificacion::class.java).apply { putExtra("email",email) }
            else -> throw IllegalArgumentException("menu option not implemented!!")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        startActivity(intenMenu)
        return true
    }
}