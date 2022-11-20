package com.example.proyectofinalfct

import Adaptadores.AdaptadorHorasExtra
import Model.HExtra
import Model.RegistroL
import Opciones.Opcion
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalfct.databinding.ActivityHorasExtraBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HorasExtra : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityHorasExtraBinding
    private lateinit var intenMenu: Intent
    private var meses= arrayOf("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre")
    private var m = ArrayList<String>()
    private var rhoras = ArrayList<RegistroL>()
    private var rhorasextra = ArrayList<HExtra>()
    private lateinit var miRecyclerView : RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var em:String=""
    private val sdf = SimpleDateFormat("yyyy", Locale("es", "ES"))
    private val currentyear = sdf.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHorasExtraBinding.inflate(layoutInflater)
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
        em=email

        //miRecyclerView = findViewById(R.id.rvHe) as RecyclerView
        miRecyclerView = binding.rvHe
        miRecyclerView.setHasFixedSize(true)
        miRecyclerView.layoutManager = LinearLayoutManager(this)

        /*
            var miAdapter = AdaptadorHorasExtra(rhorasextra, this@HorasExtra)
            miRecyclerView.adapter = miAdapter
        */
        sacarMeses()

        binding.spMeses.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val p = m[pos]
                val num=meses.indexOf(p)+1
                mostrarHoras(num.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun stringtodate(t:String): Date {
        val formatter = SimpleDateFormat("HH", Locale("es", "ES"))
        val datep = formatter.parse(t)
        return datep
    }

    private fun sacarMeses(){
        var repme=""
        try {
            recuperarRegistro(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun horasRecibido(horasNuevo: ArrayList<RegistroL>) {
                    rhoras = horasNuevo
                    for (i in 0..rhoras.size-1){
                        val x=rhoras[i] as kotlin.collections.HashMap<String, String>
                        if (rhoras.isNotEmpty()){
                            val me=x.get("fecha")!!.replaceAfterLast("/","").replaceBefore("/","").substringAfter("/").substringBefore("/")
                            val y=x.get("fecha")!!.substringAfterLast("/")
                            if (y.equals(currentyear)){
                                if (me!=repme){
                                    m.add(meses[me.toInt()-1])
                                    repme=me
                                }
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
        fun horasRecibido(h: ArrayList<RegistroL>)
    }

    fun recuperarRegistro( callback:RolCallback){
        db.collection("usuarios").document(em).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //poner los demas para recuperar todos los datos
                    var x= task.result.data?.get("Registro") as ArrayList<RegistroL>
                    if (callback != null) {
                        callback.horasRecibido(x);
                    }
                    val adaptador = ArrayAdapter(this,R.layout.item_spmes,R.id.txtMeses,m)
                    binding.spMeses.adapter = adaptador
                } else {
                    Log.e("wh", "Error getting documents.", task.exception)
                }
            }
    }

    fun mostrarHoras(meop:String){
        var totalhExtra:Int=0
        try {
            recuperarRegistro2(object : RolCallback2 {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun horasRecibido2(horasNuevo: ArrayList<RegistroL>) {
                    rhoras = horasNuevo
                    for (i in 0..rhoras.size-1){
                        val x=rhoras[i] as kotlin.collections.HashMap<String, String>
                        if (rhoras.isNotEmpty()){
                            val me=x.get("fecha")!!.replaceAfterLast("/","").replaceBefore("/","").substringAfter("/").substringBefore("/")
                            val y=x.get("fecha")!!.substringAfterLast("/")
                            if ((me==meop) && (y.equals(currentyear))){
                                if (!x.get("horaFin").equals("")){
                                    totalhExtra= ((stringtodate(x.get("horaFin")!!).hours.toInt())-(stringtodate(x.get("horaIni")!!).hours.toInt()))-Opcion.HORASLABORALES
                                    if (totalhExtra<0){ totalhExtra = 0 }
                                }else{
                                    totalhExtra=0
                                }
                                rhorasextra.add(HExtra(x.get("fecha")!!,totalhExtra.toString()))
                            }
                        }
                    }
                }
            })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }



    interface RolCallback2 {
        fun horasRecibido2(h: ArrayList<RegistroL>)
    }

    fun recuperarRegistro2( callback:RolCallback2){
        db.collection("usuarios").document(em).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //poner los demas para recuperar todos los datos
                    val x= task.result.data?.get("Registro") as ArrayList<RegistroL>
                    if (callback != null) {
                        callback.horasRecibido2(x);
                    }
                    val miAdapter = AdaptadorHorasExtra(rhorasextra, this)
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