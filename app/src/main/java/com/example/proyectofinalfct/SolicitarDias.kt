package com.example.proyectofinalfct

import Model.AJustificante
import Model.Dias
import Model.RegistroL
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivitySolicitarDiasBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SolicitarDias : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivitySolicitarDiasBinding
    private lateinit var intenMenu: Intent
    private val db = FirebaseFirestore.getInstance()
    private var aux:Int=0

    private var dni:String=""
    private var nombre:String=""
    private var ape:String=""
    private var dire:String=""
    private var nac:String=""
    private var f:String=""
    private var rhoras = ArrayList<RegistroL>()
    private var arch = ArrayList<AJustificante>()
    private var NJustifi = java.util.ArrayList<AJustificante>()
    private var Sdias= ArrayList<Dias>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySolicitarDiasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,
            R.string.ok,
            R.string.cancel
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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
            }else{
                showAlert(R.string.Dias_MSG_8)
            }
        }


    }

    private fun DialogLogin(txt: TextView): Boolean {
        val dialogo: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        val Myview=layoutInflater.inflate(R.layout.item_calendar, null)
        val calendar = Myview.findViewById<CalendarView>(R.id.calendarView)
        dialogo.setView(Myview)
        //val adaptador = ArrayAdapter(this, R.layout.item_spiner,R.id.txtOpcion,tipoRol)
        //srol.adapter = adaptador
        calendar.setOnDateChangeListener(object : CalendarView.OnDateChangeListener{
            override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
                txt.setText(dayOfMonth.toString()+"/"+(month+1).toString()+"/"+year.toString())
            }
        })
        dialogo.setPositiveButton(R.string.Accept,
            DialogInterface.OnClickListener { dialog, which ->

            })
        dialogo.setNegativeButton(R.string.Cancel_txt,
            DialogInterface.OnClickListener { dialog, which ->

                dialog.dismiss()
            })
        dialogo.show()
        return true
    }

    private fun RecuperaryGuardar(){
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
            comprobaryGuardar()
            //Toast.makeText(this, "Recuperado",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Algo ha ido mal al recuperar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun comprobaryGuardar(){
        if (!binding.txtFini.text.isNullOrEmpty() && !binding.txtFFin.text.isNullOrEmpty()){
            if (diferenciaHoras(binding.txtFini.text.toString(),binding.txtFFin.text.toString(),null)>=0){
                if (diferenciaHoras(binding.txtFini.text.toString(),Date().toString(),Date())<0){
                    if (binding.chAsuntos.isChecked){
                        if (binding.txtFini.text==binding.txtFFin.text){
                            Guardar("Asuntos Propios")
                        }else{
                            showAlert(R.string.Dias_MSG_7)
                        }
                    }
                    if (binding.chVacaciones.isChecked){
                        if (!Sdias.isEmpty()){
                            for (i in 0 until Sdias.size){
                                val d=Sdias[i] as HashMap<String, String>
                                if (d.getValue("tipo").equals("Vacaciones")){
                                    aux=aux+diferenciaHoras(d.getValue("fechaIni"),d.getValue("fechaFin"),null).toInt()
                                }
                            }
                            if ((aux+diferenciaHoras(binding.txtFini.text.toString(),binding.txtFFin.text.toString(),null))<30){
                                Guardar("Vacaciones")
                            }else{
                                showAlert(R.string.Dias_MSG_6)
                            }
                        }else{
                            if (diferenciaHoras(binding.txtFini.text.toString(),binding.txtFFin.text.toString(),null)<30){
                                Guardar("Vacaciones")
                            }else{
                                showAlert(R.string.Dias_MSG_5)
                            }
                        }
                    }
                }else{
                    showAlert(R.string.Dias_MSG_4)
                    binding.txtFini.text=""
                }
            }else{
                showAlert(R.string.Dias_MSG_3)
                binding.txtFFin.text=""
            }
        }else{
            showAlert(R.string.Dias_MSG_2)
        }
    }


    private fun diferenciaHoras(fe1: String, fe2: String, fe3: Date?): Long {
        val f1 = stringtodate(fe1)
        var diff: Long = 0
        diff = if (!fe2.contains("GMT")) {
            val f2 = stringtodate(fe2)
            f2.time - f1.time
        } else {
            fe3!!.time - f1.time
        }
        return (((diff / 1000) / 60) / 60) / 24
    }

    private fun stringtodate(t: String): Date {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
        return formatter.parse(t)
    }

    private fun Guardar(t:String){
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        Sdias.add(Dias(binding.txtFini.text.toString(),binding.txtFFin.text.toString(),t,"Pendiente"))
        //Se guardarán en modo HashMap (clave, valor).
        val user = hashMapOf(
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
                showAlert(R.string.Dias_MSG_1)
                binding.txtFini.text=""
                binding.txtFFin.text=""
                //Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
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