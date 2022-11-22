package com.example.proyectofinalfct


import Model.RegistroL
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivityMenuBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore


class Menu : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var binding: ActivityMenuBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var intenMenu:Intent
    private var em=""
    private var perfil=""
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
        val per = bundle?.getString("perfil").toString()
        em=email
        perfil=per
        sacarPerfil()

        binding.imgCalendario.setOnClickListener {
            if (!perfil.equals("")){
                intenMenu = Intent(this,Calendario::class.java).apply {
                    putExtra("email",email)
                    putExtra("perfil",perfil)
                }
                startActivity(intenMenu)
            }
        }

        binding.imgFichar.setOnClickListener {
            if (!perfil.equals("")){
                intenMenu = Intent(this,RegistroLaboral::class.java).apply {
                    putExtra("email",email)
                    putExtra("perfil",perfil)
                }
                startActivity(intenMenu)
            }
        }

        binding.imgHextra.setOnClickListener {
            if (!perfil.equals("")){
                intenMenu = Intent(this,HorasExtra::class.java).apply {
                    putExtra("email",email)
                    putExtra("perfil",perfil)
                }
                startActivity(intenMenu)
            }
        }

        binding.imgNotifi.setOnClickListener {
            if (!perfil.equals("")){
                intenMenu = Intent(this,Notificacion::class.java).apply {
                    putExtra("email",email)
                    putExtra("perfil",perfil)
                }
                startActivity(intenMenu)
            }
        }

        binding.imgVaca.setOnClickListener {
            if (!perfil.equals("")){
                intenMenu = Intent(this,SolicitarDias::class.java).apply {
                    putExtra("email",email)
                    putExtra("perfil",perfil)
                }
                startActivity(intenMenu)
            }
        }
        binding.imgMedico.setOnClickListener {
            if (!perfil.equals("")){
                intenMenu = Intent(this,Justificante::class.java).apply {
                    putExtra("email",email)
                    putExtra("perfil",perfil)
                }
                startActivity(intenMenu)
            }
        }

        binding.btnClose.setOnClickListener {
            intenMenu= Intent(this,MainActivity::class.java).apply {}
            startActivity(intenMenu)
        }

    }

    private fun sacarPerfil(){
        try {
            recuperarPerfil(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun perfilRecibido(perfilNuevo: String) {
                    perfil = perfilNuevo
                }
            })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    interface RolCallback {
        fun perfilRecibido(p: String)
    }

    fun recuperarPerfil( callback:RolCallback){
        db.collection("usuarios").document(em).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //poner los demas para recuperar todos los datos
                    var x= task.result.data?.get("Perfil").toString()
                    if (callback != null) {
                        callback.perfilRecibido(x);
                    }
                    //val adaptador = ArrayAdapter(this,R.layout.item_spmes,R.id.txtMeses,m)
                    //binding.spMeses.adapter = adaptador
                } else {
                    Log.e("wh", "Error getting documents.", task.exception)
                }
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
        intenMenu = when (menuItem.itemId) {
            R.id.opDatos -> Intent(this,DatosUsuario::class.java).apply { putExtra("email",email); putExtra("Mod","Modificar");putExtra("perfil",perfil) }
            R.id.opJornada -> Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email);putExtra("perfil",perfil) }
            R.id.opExtra -> Intent(this,HorasExtra::class.java).apply { putExtra("email",email);putExtra("perfil",perfil) }
            R.id.opCalendario -> Intent(this,Calendario::class.java).apply { putExtra("email",email);putExtra("perfil",perfil) }
            R.id.opSolicitar -> Intent(this,SolicitarDias::class.java).apply { putExtra("email",email);putExtra("perfil",perfil) }
            R.id.opJustifi -> Intent(this,Justificante::class.java).apply { putExtra("email",email);putExtra("perfil",perfil) }
            R.id.opNotifi -> Intent(this,Notificacion::class.java).apply { putExtra("email",email);putExtra("perfil",perfil) }
            else -> throw IllegalArgumentException("menu option not implemented!!")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        startActivity(intenMenu)
        return true
    }

}