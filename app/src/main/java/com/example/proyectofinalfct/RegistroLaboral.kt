package com.example.proyectofinalfct

import Model.RegistroL
import android.graphics.Bitmap
import android.nfc.tech.NfcBarcode
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalfct.databinding.ActivityRegistroLaboralBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*


class RegistroLaboral : AppCompatActivity() {
    lateinit var binding: ActivityRegistroLaboralBinding

    var dni:String=""
    var nombre:String=""
    var ape:String=""
    var dire:String=""
    var nac:String=""
    var f:String=""
    var em:String=""
    var rhoras = ArrayList<RegistroL>()

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    val sdf2 = SimpleDateFormat("HH:mm:ss", Locale("es", "ES"))
    val currentdate = sdf.format(Date())
    val currenthour = sdf2.format(Date())


    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegistroLaboralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        em=email

        binding.txtFecha.text=currentdate.toString()
        binding.txtHora.text=currenthour.toString()

        binding.btnRegistro.setOnClickListener {
            RegistroOnline()
        }

        binding.btnQr.setOnClickListener {
            sacarRegistroQr()
        }

    }



    fun RegistroOnline(){
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
            sacarRegistro()
            //Toast.makeText(this, "Recuperado",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(this, "Algo ha ido mal al recuperar",Toast.LENGTH_SHORT).show()
        }
    }
    fun sacarRegistro(){
        var contiene:Boolean=false
        try {
            recuperarRegistro(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun horasRecibido(horasNuevo: ArrayList<RegistroL>) {
                    rhoras = horasNuevo
                    for (i in 0..rhoras.size-1){
                        var x=rhoras[i] as kotlin.collections.HashMap<String, String>
                        if (rhoras.isNotEmpty()){
                            x.forEach { (key,value) ->
                                if ((key.equals("horaFin") && value.equals("")) && (key.equals("fecha") && !value.equals(currentdate.toString())) ){
                                    showAlert()
                                }
                                if (key.equals("fecha") && value.equals(currentdate.toString())){
                                        x.replace("horaFin", currenthour.toString())
                                    contiene=true
                                    guardado(em)
                                }
                            }
                        }
                    }
                    if (!contiene){
                        rhoras.add(RegistroL(currentdate.toString(),currenthour.toString(),""))
                        guardado(em)
                    }
                }
            })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun sacarRegistroQr(){
        var contiene:Boolean=false
        try {
            recuperarRegistro(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun horasRecibido(horasNuevo: ArrayList<RegistroL>) {
                    rhoras = horasNuevo
                    for (i in 0..rhoras.size-1){
                        var x=rhoras[i] as kotlin.collections.HashMap<String, String>
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
                        //rhoras.add(RegistroL(currentdate.toString(),currenthour.toString(),""))
                        GenerarQr("horaIni:"+currenthour.toString(),"horaFin:"+"")
                    }
                }
            })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun GenerarQr(hi:String,hf:String){
        var texto:String="Fecha:"+em+"-----"+hi+"-----"+hf
        try {
            var barcodeEncoder:BarcodeEncoder = BarcodeEncoder()
            var bitmap = barcodeEncoder.encodeBitmap(texto,BarcodeFormat.QR_CODE,227,227)
            binding.imgQr.setImageBitmap(bitmap)
        }catch (e:Exception){
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
                } else {
                    Log.e("wh", "Error getting documents.", task.exception)
                }
            }
    }

    fun guardado(email:String){
        //Se guardarán en modo HashMap (clave, valor).
        var user = hashMapOf(
            "DNI" to dni,
            "Nombre" to nombre,
            "Apellidos" to ape,
            "Direccion" to dire,
            "FechaNac" to nac,
            "Foto" to f,
            "Registro" to rhoras
        )

        db.collection("usuarios")//añade o sebreescribe
            .document(email) //Será la clave del documento.
            .set(user).addOnSuccessListener {
                Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener{
                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("NOTIFICACION")
        builder.setMessage("Una jornada laboral anterior esta sin cerrar, por favor pongase en contacto con RRHH")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}