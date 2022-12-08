package com.example.proyectofinalfct

import Adaptadores.AdaptadorHorasExtra
import Adaptadores.AdaptadorNotificacion
import Model.HExtra
import Model.Notifica
import Model.RegistroL
import Opciones.Opcion
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalfct.databinding.ActivityNotificacionBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore


class Notificacion : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityNotificacionBinding
    private lateinit var intenMenu: Intent
    private val db = FirebaseFirestore.getInstance()
    private lateinit var miRecyclerView : RecyclerView
    private var Notifi = ArrayList<Notifica>()
    private var auxNotifi = ArrayList<Notifica>()
    private var perf=""
    private var em=""
    @RequiresApi(Build.VERSION_CODES.P)
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

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        val per = bundle?.getString("perfil").toString()
        perf=per
        em=email

        miRecyclerView = binding.rvNotifi
        miRecyclerView.setHasFixedSize(true)
        miRecyclerView.layoutManager = LinearLayoutManager(this)

        mostrarNotificaciones()

        binding.btnB.setOnClickListener {
            intenMenu= Intent(this,Menu::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            startActivity(intenMenu)
        }
    }

    fun mostrarNotificaciones(){
        var nob=""
        db.collection("usuarios").document(em).get().addOnSuccessListener {
            nob=(it.get("Nombre").toString())
        }.addOnFailureListener{
        }
        try {
            recuperarNotificaciones(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun notificacionRecibido(notificacionNuevo: ArrayList<Notifica>) {
                    Notifi = notificacionNuevo
                    for (i in 0..Notifi.size-1){
                        var n=Notifi[i] as HashMap<String,String>
                        if (perf.equals("Admin")){
                            auxNotifi.add(Notifica(n.getValue("titulo").toString(),n.getValue("trabajador"),n.getValue("estado"),n.getValue("puestoTrabajador"),n.getValue("tipo"),n.getValue("d"),n.getValue("p")))
                        }else{
                            if (nob.equals(n.getValue("trabajador")) && n.getValue("estado").equals("Realizado")){
                                auxNotifi.add(Notifica(n.getValue("titulo").toString(),n.getValue("trabajador"),n.getValue("estado"),n.getValue("puestoTrabajador"),n.getValue("tipo"),n.getValue("d"),n.getValue("p")))
                            }
                        }
                    }
                }
            })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    interface RolCallback {
        fun notificacionRecibido(h: ArrayList<Notifica>)
    }

    fun recuperarNotificaciones( callback:RolCallback){
        val aux=ArrayList<Notifica>()
        db.collection("usuarios").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (i in 0 until task.result.documents.size){
                        if (task.result.documents[i].get("Notificacion")!=null && (task.result.documents[i].get("Notificacion") as ArrayList<Notifica>).isNotEmpty()){
                            val x= task.result.documents[i].get("Notificacion") as ArrayList<Notifica>
                            for (i in 0 until x.size){
                                aux.add(x[i])
                            }
                        }
                    }
                    //poner los demas para recuperar todos los datos
                    if (callback != null) {
                        callback.notificacionRecibido(aux);
                    }
                    val miAdapter = AdaptadorNotificacion(auxNotifi, this)
                    AdaptadorNotificacion.perfilActual=perf
                    miRecyclerView.adapter = miAdapter
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

    override fun onNavigationItemSelected( menuItem: MenuItem): Boolean {
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        val per = bundle?.getString("perfil").toString()
        intenMenu = when (menuItem.itemId) {
            R.id.opDatos -> Intent(this,DatosUsuario::class.java).apply { putExtra("email",email); putExtra("Mod","Modificar");putExtra("perfil",per) }
            R.id.opJornada -> Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            R.id.opExtra -> Intent(this,HorasExtra::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            R.id.opCalendario -> Intent(this,Calendario::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            R.id.opSolicitar -> Intent(this,SolicitarDias::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            R.id.opJustifi -> Intent(this,Justificante::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            R.id.opNotifi -> Intent(this,Notificacion::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            else -> throw IllegalArgumentException("menu option not implemented!!")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        startActivity(intenMenu)
        return true
    }
}