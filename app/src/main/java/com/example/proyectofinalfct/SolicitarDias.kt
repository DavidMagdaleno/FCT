package com.example.proyectofinalfct

import Model.AJustificante
import Model.Dias
import Model.RegistroL
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinalfct.databinding.ActivitySolicitarDiasBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SolicitarDias : AppCompatActivity() {
    lateinit var binding: ActivitySolicitarDiasBinding
    private lateinit var intenMenu: Intent
    private val db = FirebaseFirestore.getInstance()
    var aux:Int=0

    var dni:String=""
    var nombre:String=""
    var ape:String=""
    var dire:String=""
    var nac:String=""
    var f:String=""
    var rhoras = ArrayList<RegistroL>()
    var arch = ArrayList<AJustificante>()
    var NJustifi = java.util.ArrayList<AJustificante>()
    var Sdias= ArrayList<Dias>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySolicitarDiasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDias.setOnClickListener {
            if (binding.txtFini.text.isNullOrEmpty()){
                DialogLogin(binding.txtFini)
            }else{
                DialogLogin(binding.txtFFin)
            }
        }
        binding.chAsuntos.setOnClickListener {
            binding.chVacaciones.isChecked=false
        }
        binding.chVacaciones.setOnClickListener {
            binding.chAsuntos.isChecked=false
        }

        binding.btnSave.setOnClickListener {
            if (binding.chVacaciones.isChecked || binding.chAsuntos.isChecked){
                RecuperaryGuardar()
                binding.txtFini.text=""
                binding.txtFFin.text=""
            }else{
                showAlert("Selecciona el tipo de días que quieres solicitar")
            }
        }


    }

    fun DialogLogin(txt: TextView): Boolean {
        val dialogo: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        val Myview=layoutInflater.inflate(R.layout.item_calendar, null)
        var calendar = Myview.findViewById<CalendarView>(R.id.calendarView)
        dialogo.setView(Myview)
        //val adaptador = ArrayAdapter(this, R.layout.item_spiner,R.id.txtOpcion,tipoRol)
        //srol.adapter = adaptador
        calendar.setOnDateChangeListener(object : CalendarView.OnDateChangeListener{
            override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
                txt.setText(dayOfMonth.toString()+"/"+(month+1).toString()+"/"+year.toString())
            }
        })
        dialogo.setPositiveButton("Entrar",
            DialogInterface.OnClickListener { dialog, which ->

            })
        dialogo.setNegativeButton("Salir",
            DialogInterface.OnClickListener { dialog, which ->

                dialog.dismiss()
            })
        dialogo.show()
        return true
    }

    fun RecuperaryGuardar(){
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
            if (!binding.txtFini.text.isNullOrEmpty() && !binding.txtFFin.text.isNullOrEmpty()){
                if (binding.chAsuntos.isChecked){
                    if (binding.txtFini.text==binding.txtFFin.text){
                        Guardar("Asuntos Propios")
                    }else{
                        showAlert("Solo se pueden pedir un dias de asuntos propios por separado")
                    }
                }
                if (binding.chVacaciones.isChecked){
                    if (!Sdias.isEmpty()){
                        for (i in 0..Sdias.size){
                            var d=Sdias[i] as HashMap<String, String>
                            if (d.getValue("tipo").equals("Vacaciones")){
                                var f1=stringtodate(d.getValue("FechaIni"))
                                var f2=stringtodate(d.getValue("FechaFin"))
                                Log.e("f1",f1.toString())//------------------------------------------------------
                                Log.e("f2",f2.toString())//------------------------------------------------------
                                aux=aux+((f2.time-f1.time).toInt())
                                Log.e("aux",aux.toString())//----------------------------------------------------
                            }
                        }
                        if (aux<30){
                            Guardar("Vacaciones")
                        }else{
                            showAlert("Has superado el número maximo de días de vacaciones")
                        }
                    }else{
                        var f1=stringtodate(binding.txtFini.text.toString())
                        var f2=stringtodate(binding.txtFFin.text.toString())
                        Log.e("f1",f1.toString())//------------------------------------------------------------
                        Log.e("f2",f2.toString())//------------------------------------------------------------
                        Log.e("result",(f2.time-f1.time).toInt().toString())//---------------------------------
                        if ((f2.time-f1.time).toInt()<30){
                            Guardar("Vacaciones")
                        }else{
                            showAlert("Has seleccionado mas de 30 días de vacaciones")
                        }
                    }
                }
            }else{
                showAlert("No has seleccionado las fechas")
            }
            //Toast.makeText(this, "Recuperado",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Algo ha ido mal al recuperar", Toast.LENGTH_SHORT).show()
        }
    }

    fun stringtodate(t:String): Date {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
        val datep = formatter.parse(t)
        return datep
    }

    fun Guardar(t:String){
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        Sdias.add(Dias(binding.txtFini.text.toString(),binding.txtFFin.text.toString(),t,"Pendiente"))
        //Se guardarán en modo HashMap (clave, valor).
        var user = hashMapOf(
            "DNI" to dni,
            "Nombre" to nombre,
            "Apellidos" to ape,
            "Direccion" to dire,
            "FechaNac" to nac,
            "Foto" to f,
            "Registro" to rhoras,
            "Justificante" to arch,
            "Dias" to Sdias
        )
        db.collection("usuarios")//añade o sebreescribe
            .document(email) //Será la clave del documento.
            .set(user).addOnSuccessListener {
                showAlert("Días solicitados con exito")
                //Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAlert(txt:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("NOTIFICACION")
        builder.setMessage(txt)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}