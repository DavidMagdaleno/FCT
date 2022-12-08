package com.example.proyectofinalfct

import Model.AJustificante
import Model.Dias
import Model.Notifica
import Model.RegistroL
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivityRegistroLaboralBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*


class RegistroLaboral : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var binding: ActivityRegistroLaboralBinding
    private lateinit var intenMenu: Intent
    private var accion:Int=-1
    private var Rbarra:Int=-1
    private var r:Boolean=false

    private var dni:String=""
    private var nombre:String=""
    private var ape:String=""
    private var dire:String=""
    private var nac:String=""
    private var f:String=""
    private var em:String=""
    private var rhoras = ArrayList<RegistroL>()
    private var NJustifi = ArrayList<AJustificante>()
    private var Sdias= ArrayList<Dias>()
    private var perfil=""
    private var puesto=""
    private var Notifi = ArrayList<Notifica>()

    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    private val sdf2 = SimpleDateFormat("HH:mm:ss", Locale("es", "ES"))
    private val currentdate = sdf.format(Date())
    private val currenthour = sdf2.format(Date())


    private val db = FirebaseFirestore.getInstance()
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegistroLaboralBinding.inflate(layoutInflater)
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


        binding.txtFecha.text=currentdate.toString()
        binding.txtHora.text=currenthour.toString()

        RegistroOnline()

        binding.btnRegistro.setOnClickListener {
            r=true
            RegistroOnline()
        }

        binding.btnQr.setOnClickListener {
            sacarRegistroQr()
        }

        binding.btnVo.setOnClickListener {
            intenMenu= Intent(this,Menu::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            startActivity(intenMenu)
        }

    }


    //Sacar datos del usuario
    private fun RegistroOnline(){
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        db.collection("usuarios").document(email).get().addOnSuccessListener {
            //Si encuentra el documento será satisfactorio este listener y entraremos en él.
            dni=(it.get("DNI").toString())
            nombre=(it.get("Nombre").toString())
            ape=(it.get("Apellidos").toString())
            dire=(it.get("Direccion").toString())
            nac=(it.get("FechaNac").toString())
            if (it.get("Foto").toString()!=""){f=it.get("Foto").toString()}
            rhoras=it.get("Registro") as ArrayList<RegistroL>
            NJustifi=it.get("Justificante") as ArrayList<AJustificante>
            Sdias=it.get("Dias") as ArrayList<Dias>
            perfil=it.get("Perfil").toString()
            puesto=it.get("Puesto").toString()
            Notifi=it.get("Notificacion") as ArrayList<Notifica>
            sacarRegistro()
        }.addOnFailureListener{
            Toast.makeText(this, "Algo ha ido mal al recuperar",Toast.LENGTH_SHORT).show()
        }
    }
    //sacar los registros laborales y actualizarlos
    private fun sacarRegistro(){
        var contiene:Boolean=false
        try {
            recuperarRegistro(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun horasRecibido(h: ArrayList<RegistroL>) {
                    rhoras = h
                    for (i in 0 until rhoras.size){
                        val x=rhoras[i] as HashMap<String, String>
                        if (rhoras.isNotEmpty()){
                            x.forEach { (key,value) ->
                                //comprueba que no hay ningun registro anterior sin cerrar en caso contrario muestra una notificacion
                                if ((key.equals("horaFin") && value.equals("")) && (key.equals("fecha") && !value.equals(currentdate.toString())) ){
                                    showAlert(R.string.Jornada_msg_3)
                                }
                                //si hay un registro existente en la fecha actual pone la fecha de finalizacion
                                if (key.equals("fecha") && value.equals(currentdate.toString())){
                                    contiene=true
                                    if (Rbarra!=0){
                                        binding.txtPrimer.text=x.getValue("horaIni")
                                        binding.txtPrimer.setBackgroundColor(Color.GREEN)
                                        if (!x.getValue("horaFin").equals("")){
                                            binding.txtSecond.text=x.getValue("horaFin")
                                            binding.txtSecond.setBackgroundColor(Color.GREEN)
                                        }
                                        Rbarra=0
                                    }else{
                                        if (!x.getValue("horaFin").equals("")){
                                            showAlert(R.string.Jornada_msg_4)
                                        }else{
                                            x.replace("horaFin", currenthour.toString())
                                            contiene=true
                                            accion=1
                                            guardado(em)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //en caso de no haber registro de la fecha actual crea uno
                    if (!contiene && r){
                        rhoras.add(RegistroL(currentdate.toString(),currenthour.toString(),""))
                        accion=0
                        guardado(em)
                    }
                }
            })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    //genera qr con el registro de inicio o finalizacion segun corresponda
    private fun sacarRegistroQr(){
        var contiene:Boolean=false
        try {
            recuperarRegistro(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun horasRecibido(h: ArrayList<RegistroL>) {
                    rhoras = h
                    for (i in 0 until rhoras.size){
                        val x=rhoras[i] as kotlin.collections.HashMap<String, String>
                        if (rhoras.isNotEmpty()){
                            x.forEach { (key,value) ->
                                if (key.equals("fecha") && value.equals(currentdate.toString())){
                                    x.replace("horaFin", currenthour.toString())
                                    contiene=true
                                    GenerarQr("horaIni:"+x.getValue("horaIni"),"horaFin:"+currenthour.toString())
                                }
                            }
                        }
                    }
                    if (!contiene){
                        GenerarQr("horaIni:"+currenthour.toString(),"horaFin:"+"")
                    }
                }
            })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun GenerarQr(hi:String,hf:String){
        val texto:String="Fecha:"+em+"-----"+hi+"-----"+hf
        try {
            val barcodeEncoder:BarcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(texto,BarcodeFormat.QR_CODE,227,227)
            binding.imgQr.setImageBitmap(bitmap)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    interface RolCallback {
        fun horasRecibido(h: ArrayList<RegistroL>)
    }

    private fun recuperarRegistro( callback:RolCallback){
        db.collection("usuarios").document(em).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //poner los demas para recuperar todos los datos
                    val x= task.result.data?.get("Registro") as ArrayList<RegistroL>
                    if (callback != null) {
                        callback.horasRecibido(x);
                    }
                } else {
                    Log.e("wh", "Error getting documents.", task.exception)
                }
            }
    }
    //guarda el registro del día
    fun guardado(email:String){
        //Se guardarán en modo HashMap (clave, valor).
        val user = hashMapOf(
            "DNI" to dni,
            "Nombre" to nombre,
            "Apellidos" to ape,
            "Direccion" to dire,
            "FechaNac" to nac,
            "Foto" to f,
            "Registro" to rhoras,
            "Justificante" to NJustifi,
            "Dias" to Sdias,
            "Perfil" to perfil,
            "Puesto" to puesto,
            "Notificacion" to Notifi
        )

        db.collection("usuarios")//añade o sebreescribe
            .document(email) //Será la clave del documento.
            .set(user).addOnSuccessListener {
                //Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
                if (accion==0){
                    showAlert(R.string.Jornada_msg_1)
                    binding.txtPrimer.text=binding.txtHora.text
                    binding.txtPrimer.setBackgroundColor(Color.GREEN)
                }
                if (accion==1){
                    showAlert(R.string.Jornada_msg_2)
                    binding.txtSecond.text=binding.txtHora.text
                    binding.txtSecond.setBackgroundColor(Color.GREEN)
                    Rbarra=-1
                }
                //finish()
            }.addOnFailureListener{
                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAlert(txt:Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.Notificacion)
        builder.setMessage(txt)
        builder.setPositiveButton(R.string.Accept,null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
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