package com.example.proyectofinalfct

import Model.AJustificante
import Model.Dias
import Model.ImgPerfil
import Model.RegistroL
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivityJustificanteBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class Justificante : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityJustificanteBinding
    private lateinit var intenMenu: Intent
    private val File=1
    private var archivo=false
    private val database= Firebase.storage
    val ref2=database.reference
    private val db = FirebaseFirestore.getInstance()

    var dni:String=""
    var nombre:String=""
    var ape:String=""
    var dire:String=""
    var nac:String=""
    var f:String=""
    var rhoras = ArrayList<RegistroL>()
    var arch = ArrayList<AJustificante>()
    var Sdias= ArrayList<Dias>()
    //var arch:AJustificante= AJustificante("","","")

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


        binding.btnSubir.setOnClickListener {
            val intent= Intent(Intent.ACTION_GET_CONTENT)
            intent.type="*/*"
            startActivityForResult(intent, File)
        }

        binding.etxtFechaIni.setOnClickListener {
            DialogLogin(binding.etxtFechaIni)
        }

        binding.etxtFechaFin.setOnClickListener {
            DialogLogin(binding.etxtFechaFin)
        }

        binding.btnAcp.setOnClickListener {
            if (archivo){
                RecuperaryGuardar()
            }else{
                showAlert()
            }
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
                //val imagenRef = ref2.child("${FileUri!!.lastPathSegment}")
                //con esto subimos el archivo
                var uploadTask = archivoRef.putFile(FileUri)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    Log.e("Conseguido","Archivo subido con exito")
                    //perfil.nombre = FileUri!!.lastPathSegment.toString()
                    arch.add(AJustificante(binding.etxtFechaIni.text.toString(),binding.etxtFechaFin.text.toString(),FileUri!!.lastPathSegment.toString()))
                    //arch.FechaIni=binding.etxtFechaIni.text.toString()
                    //arch.FechaFin=binding.etxtFechaFin.text.toString()
                    //arch.nombre=FileUri!!.lastPathSegment.toString()
                    archivo=true
                    //arch.Fecha=binding.calendarView.date.toString()----------------------------------------------------------
                    /*try {
                        mostarimagenes(object : DatosUsuario.RolCallback {
                            override fun imagenes(imaNuevo: ImgPerfil) {
                                //var miAdapter = AdaptadorFotos(imaNuevo, this@Galeria)
                                //miRecyclerView.adapter = miAdapter
                            }
                        })

                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }*/
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                }

            }
        }

        //if (requestCode==cameraRequest){
        //val photo:Bitmap=data?.extras?.get("data") as Bitmap
        //binding.imgFoto.setImageBitmap(photo)
        //}
    }

    fun DialogLogin(txt: EditText): Boolean {
        val dialogo: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        val Myview=layoutInflater.inflate(R.layout.item_calendar, null)
        var calendar = Myview.findViewById<CalendarView>(R.id.calendarView)
        dialogo.setView(Myview)
        //val adaptador = ArrayAdapter(this, R.layout.item_spiner,R.id.txtOpcion,tipoRol)
        //srol.adapter = adaptador
        calendar.setOnDateChangeListener(object : CalendarView.OnDateChangeListener{
            override fun onSelectedDayChange(view: CalendarView,year: Int, month: Int, dayOfMonth: Int) {
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
            arch=it.get("Justificante") as ArrayList<AJustificante>
            Sdias=it.get("Dias") as ArrayList<Dias>
            Guardar()
            //Toast.makeText(this, "Recuperado",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(this, "Algo ha ido mal al recuperar",Toast.LENGTH_SHORT).show()
        }
    }
    fun Guardar(){
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
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
                binding.txtResult.text="Justificante subido con exito"
                //Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("NOTIFICACION")
        builder.setMessage("No se ha adjuntado ningún archivo")
        builder.setPositiveButton("Aceptar",null)
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
        when (menuItem.itemId) {
            R.id.opDatos -> intenMenu = Intent(this,DatosUsuario::class.java).apply { putExtra("email",email); putExtra("Mod","Modificar") }
            R.id.opJornada -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
            R.id.opExtra -> intenMenu = Intent(this,HorasExtra::class.java).apply { putExtra("email",email) }
            //R.id.opCalendario -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
            //R.id.opSolicitar -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
            R.id.opJustifi -> intenMenu = Intent(this,Justificante::class.java).apply { putExtra("email",email) }
            //R.id.opNotifi -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
            else -> throw IllegalArgumentException("menu option not implemented!!")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        startActivity(intenMenu)
        return true
    }

}