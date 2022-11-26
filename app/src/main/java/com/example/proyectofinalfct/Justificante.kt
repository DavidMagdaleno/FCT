package com.example.proyectofinalfct

import Model.AJustificante
import Model.Dias
import Model.Notifica
import Model.RegistroL
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivityJustificanteBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class Justificante : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityJustificanteBinding
    private lateinit var intenMenu: Intent
    private val File=1
    private var archivo=false
    private val database= Firebase.storage
    private val ref2=database.reference
    private val db = FirebaseFirestore.getInstance()

    private var dni:String=""
    private var nombre:String=""
    private var ape:String=""
    private var dire:String=""
    private var nac:String=""
    private var f:String=""
    private var rhoras = ArrayList<RegistroL>()
    private var arch = ArrayList<AJustificante>()
    private var Sdias= ArrayList<Dias>()
    private var perfil=""
    private var puesto=""
    private var Notifi = ArrayList<Notifica>()

    private var NomArch:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJustificanteBinding.inflate(layoutInflater)
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

        binding.btnAcp.isEnabled=false

        binding.btnSubir.setOnClickListener {
            val intent= Intent(Intent.ACTION_GET_CONTENT)
            intent.type="*/*"
            startActivityForResult(intent, File)
        }

        binding.btnFechaIni.setOnClickListener {
            DialogLogin(binding.btnFechaIni)
        }

        binding.btnFechaFin.setOnClickListener {
            DialogLogin(binding.btnFechaFin)
        }

        binding.btnAcp.setOnClickListener {
            if (archivo){
                Guardar()
            }else{
                showAlert(R.string.Alert_justify_2)
            }
        }
        binding.btnVol.setOnClickListener {
            intenMenu= Intent(this,Menu::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            startActivity(intenMenu)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==File){
            if(resultCode== RESULT_OK){
                val FileUri=data!!.data
                //val FileUri=data?.extras?.get("data")
                //para decirle la carpeta donde se almacenara y su nombre
                //si en el ref se lo pone un path se crean subcarpertas
                val archivoRef = ref2.child("Perfiles/"+email+"/Justificantes/${FileUri!!.lastPathSegment}")
                //con esto subimos el archivo
                var uploadTask = archivoRef.putFile(FileUri)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    Log.e("Conseguido","Archivo subido con exito")
                    //perfil.nombre = FileUri!!.lastPathSegment.toString()
                    NomArch=FileUri!!.lastPathSegment.toString()
                    Recuperar()
                }
            }
        }
    }

    private fun DialogLogin(txt: Button): Boolean {
        val dialogo: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        val Myview=layoutInflater.inflate(R.layout.item_calendar, null)
        val calendar = Myview.findViewById<CalendarView>(R.id.calendarView)
        dialogo.setView(Myview)

        calendar.setOnDateChangeListener(object : CalendarView.OnDateChangeListener{
            override fun onSelectedDayChange(view: CalendarView,year: Int, month: Int, dayOfMonth: Int) {
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

    private fun Recuperar(){
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
            arch=it.get("Justificante") as ArrayList<AJustificante>
            Sdias=it.get("Dias") as ArrayList<Dias>
            perfil=it.get("Perfil").toString()
            puesto=it.get("Puesto").toString()
            Notifi=it.get("Notificacion") as ArrayList<Notifica>

            binding.btnAcp.isEnabled=true
            archivo=true
            //Toast.makeText(this, "Recuperado",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Algo ha ido mal al recuperar",Toast.LENGTH_SHORT).show()
        }
    }
    private fun Guardar(){
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        arch.add(AJustificante(binding.btnFechaIni.text.toString(),binding.btnFechaFin.text.toString(),NomArch))
        //Toast.makeText(this, "añadido",Toast.LENGTH_SHORT).show()
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
            "Dias" to Sdias,
            "Perfil" to perfil,
            "Puesto" to puesto,
            "Notificacion" to Notifi
        )
        db.collection("usuarios")//añade o sebreescribe
            .document(email) //Será la clave del documento.
            .set(user).addOnSuccessListener {
                binding.txtResult.setText(R.string.Alert_justify_1)
                showAlert(R.string.Alert_justify_1)
                //Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAlert(t:Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.Notificacion)
        builder.setMessage(t)
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