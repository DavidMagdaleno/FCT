package com.example.proyectofinalfct

import Adaptadores.AdaptadorHorasExtra
import Model.HExtra
import Model.RegistroL
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalfct.databinding.ActivityHorasExtraBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HorasExtra : AppCompatActivity() {
    lateinit var binding: ActivityHorasExtraBinding
    var meses= arrayOf("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre")
    var m = ArrayList<String>()
    var rhoras = ArrayList<RegistroL>()
    var rhorasextra = ArrayList<HExtra>()
    lateinit var miRecyclerView : RecyclerView
    private val db = FirebaseFirestore.getInstance()
    var em:String=""
    val sdf = SimpleDateFormat("yyyy", Locale("es", "ES"))
    val currentyear = sdf.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHorasExtraBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                var p = m[pos]
                var num=meses.indexOf(p)+1
                mostrarHoras(num.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun stringtodate(t:String): Date {
        val formatter = SimpleDateFormat("HH", Locale("es", "ES"))
        val datep = formatter.parse(t)
        return datep
    }

    fun sacarMeses(){
        var repme=""
        try {
            recuperarRegistro(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun horasRecibido(horasNuevo: ArrayList<RegistroL>) {
                    rhoras = horasNuevo
                    //m.add("Elije un Mes")
                    for (i in 0..rhoras.size-1){
                        var x=rhoras[i] as kotlin.collections.HashMap<String, String>
                        if (rhoras.isNotEmpty()){
                            var me=x.get("fecha")!!.removeRange(0,3).removeRange(2,7)
                            var y=x.get("fecha")!!.substringAfterLast("/")
                            //var f:Date= stringtodate(x.get("fecha")!!)
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
                        var x=rhoras[i] as kotlin.collections.HashMap<String, String>
                        if (rhoras.isNotEmpty()){
                            var me=x.get("fecha")!!.removeRange(0,3).removeRange(2,7)
                            var y=x.get("fecha")!!.substringAfterLast("/")
                            if ((me==meop) && (y.equals(currentyear))){
                                if (!x.get("horaFin").equals("")){
                                    totalhExtra= ((stringtodate(x.get("horaFin")!!).hours.toInt())-(stringtodate(x.get("horaIni")!!).hours.toInt()))-8
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
                    var x= task.result.data?.get("Registro") as ArrayList<RegistroL>
                    if (callback != null) {
                        callback.horasRecibido2(x);
                    }
                    var miAdapter = AdaptadorHorasExtra(rhorasextra, this)
                    miRecyclerView.adapter = miAdapter
                } else {
                    Log.e("wh", "Error getting documents.", task.exception)
                }
            }
    }
}