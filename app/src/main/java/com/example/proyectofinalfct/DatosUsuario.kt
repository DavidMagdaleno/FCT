package com.example.proyectofinalfct

import Model.*
import Opciones.Opcion
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import com.example.proyectofinalfct.databinding.ActivityDatosUsuarioBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.ArrayList

class DatosUsuario : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val File=1
    private lateinit var intenMenu:Intent
    var rhoras = ArrayList<RegistroL>()
    var NJustifi = ArrayList<AJustificante>()
    var Sdias= ArrayList<Dias>()
    var Notifi = ArrayList<Notifica>()
    var puesto=""
    private val database= Firebase.storage
    val ref2=database.reference
    val ONE_MEGABYTE: Long = 1024 * 1024
    var perfil:ImgPerfil=ImgPerfil("",null)
    var antiImagen=""
    var p=""
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: ActivityDatosUsuarioBinding
    //private val cameraRequest=1888
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDatosUsuarioBinding.inflate(layoutInflater)
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
        val modi = bundle?.getString("Mod").toString()
        val per = bundle?.getString("perfil").toString()
        p=per
        binding.btnVolver.setOnClickListener {
            intenMenu= Intent(this,Menu::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            startActivity(intenMenu)
        }

        binding.cbUser.setOnClickListener {
            binding.cbAdmin.isChecked=false
        }
        binding.cbAdmin.setOnClickListener {
            binding.cbUser.isChecked=false
        }

        val adaptador = ArrayAdapter(this, R.layout.item_sppuesto,R.id.txtPuesto,Opcion.puestos)
        binding.spPuesto.adapter = adaptador

        //incluir los tipos de puesto en el spinner
        binding.spPuesto.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                var pu = Opcion.puestos.get(pos)
                puesto=pu
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })
        
        if (modi=="Modificar"){
            //recuperar los datos del usuario y mostralos en los respectivos campos
            db.collection("usuarios").document(email).get().addOnSuccessListener {
                //Si encuentra el documento será satisfactorio este listener y entraremos en él.
                binding.txtDNI.setText(it.get("DNI").toString())
                binding.txtName.setText(it.get("Nombre").toString())
                binding.txtApe.setText(it.get("Apellidos").toString())
                binding.txtDire.setText(it.get("Direccion").toString())
                binding.txtNac.setText(it.get("FechaNac").toString())
                if (it.get("Foto").toString()!=""){
                    perfil.nombre=it.get("Foto").toString()
                    antiImagen=it.get("Foto").toString()
                    recuperarFoto()
                }
                binding.txtP.text="Actual: "+it.get("Puesto").toString()
                puesto=it.get("Puesto").toString()
                rhoras=it.get("Registro") as ArrayList<RegistroL>
                NJustifi=it.get("Justificante") as ArrayList<AJustificante>
                Sdias=it.get("Dias") as ArrayList<Dias>
                Notifi=it.get("Notificacion") as ArrayList<Notifica>
                if (it.get("Perfil").toString().equals("Usuario")){
                    binding.cbUser.isChecked=true
                    binding.cbAdmin.isChecked=false
                }else{
                    binding.cbUser.isChecked=false
                    binding.cbAdmin.isChecked=true
                }
                //Toast.makeText(this, "Recuperado",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this, "Algo ha ido mal al recuperar",Toast.LENGTH_SHORT).show()
            }

            //impedir que el usuario pueda acceder o modificar el DNI
            binding.cbAdmin.isVisible=false
            binding.cbUser.isVisible=false
            binding.txtDNI.isFocusable=false
            binding.txtDNI.isFocusableInTouchMode=false
            if (!per.equals("Admin")){
                binding.spPuesto.isEnabled=false
            }
        }

        binding.btnAcept.setOnClickListener {
            //utilizado para evitar errores al guardar debido a los tiempos de la coroutinas de cambiar foto de perfil
            Thread.sleep(1000)
            if ((!binding.cbUser.isChecked && !binding.cbAdmin.isChecked) || (binding.cbUser.isChecked && binding.cbAdmin.isChecked)){
                //Log.e("ERROR","No has seleccionado un perfil o has seleccionado demasiados")
                showAlert(R.string.userError_1)
            }else{
                if (puesto.equals("")){
                    //Log.e("ERROR","El trabajador no tiene asignado un puesto")
                    showAlert(R.string.userError_2)
                }else{
                    //se guardan o sobreescriben los nuevos datos de usuario
                    if (binding.cbUser.isChecked){ p="Usuario" }
                    if (binding.cbAdmin.isChecked){ p="Admin" }

                    if (binding.txtName.text.isNullOrEmpty() || binding.txtApe.text.isNullOrEmpty() || binding.txtNac.text.isNullOrEmpty() || binding.txtDire.text.isNullOrEmpty()){
                        showAlert(R.string.Login_Error_2)
                    }else{
                        //Se guardarán en modo HashMap (clave, valor).
                        var user = hashMapOf(
                            "DNI" to binding.txtDNI.text.trim().toString(),
                            "Nombre" to binding.txtName.text.toString(),
                            "Apellidos" to binding.txtApe.text.toString(),
                            "Direccion" to binding.txtDire.text.toString(),
                            "FechaNac" to binding.txtNac.text.toString(),
                            "Foto" to perfil.nombre,
                            "Registro" to rhoras,
                            "Justificante" to NJustifi,
                            "Dias" to Sdias,
                            "Perfil" to p,
                            "Puesto" to puesto,
                            "Notificacion" to Notifi
                        )

                        db.collection("usuarios")//añade o sebreescribe
                            .document(email) //Será la clave del documento.
                            .set(user).addOnSuccessListener {
                                showAlert(R.string.personal_data_msg_1)
                                //Toast.makeText(this, "Almacenado", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener{
                                showAlert(R.string.personal_data_msg_2)
                                //Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }

        binding.btnSfoto.setOnClickListener{
            /*if(ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),cameraRequest)
            }
            val cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent,cameraRequest)
            */
            val intent= Intent(Intent.ACTION_GET_CONTENT)
            intent.type="*/*"
            startActivityForResult(intent,File)
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
                val imagenRef = ref2.child("Perfiles/"+email+"/${FileUri!!.lastPathSegment}")
                //val imagenRef = ref2.child("${FileUri!!.lastPathSegment}")
                //con esto subimos la imagen
                var uploadTask = imagenRef.putFile(FileUri)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    Log.e("Conseguido","Archivo subido con exito")
                    perfil.nombre = FileUri!!.lastPathSegment.toString()
                    try {
                        mostarimagenes(object : DatosUsuario.RolCallback {
                            override fun imagenes(imaNuevo: ImgPerfil) {
                                //var miAdapter = AdaptadorFotos(imaNuevo, this@Galeria)
                                //miRecyclerView.adapter = miAdapter
                            }
                        })
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    limpiarDatos(email)
                }
            }
        }
    }
    //cuando se guarda una nueva imagen de perfil se borrar la anterior para evitar almacenamiento masivo
    private fun limpiarDatos(email:String){
        val imagenRef = ref2.child("Perfiles/"+email+"/${antiImagen}")
        imagenRef.delete().addOnSuccessListener { taskSnapshot ->
            Log.e("Conseguido","Archivo antiguo eliminado con exito")
        }.addOnFailureListener {
            Log.e("ERROR","Archivo antiguo NO eliminado con exito")
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            mostarimagenes(object : DatosUsuario.RolCallback {
                override fun imagenes(imaNuevo: ImgPerfil) {
                    //var miAdapter = AdaptadorFotos(imaNuevo, this@Galeria)
                    //miRecyclerView.adapter = miAdapter
                }
            })
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }




    interface RolCallback {
        fun imagenes(ima: ImgPerfil)
    }

    fun getBitmap(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }
    //mostrar la imagen de perfil
    fun mostarimagenes( callback:RolCallback) {
        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        if(perfil.nombre!=""){
            val desRef = Firebase.storage.reference.child("Perfiles/").child(email + "/")//.child(perfil.nombre)
            desRef.listAll().addOnCompleteListener { lista ->
                if (lista.isSuccessful){
                    for (i in lista.result.items) {
                        i.getBytes(ONE_MEGABYTE).addOnCompleteListener() {
                            if (it.isSuccessful) {
                                val img = getBitmap(it.result)!!
                                perfil.img = img
                                binding.imgFoto.setImageBitmap(img)
                            }
                        }
                    }
                }
            }
        }
        if (callback != null) {
            callback.imagenes(perfil);
        }
    }
    //Recuperar la imagen de perfil si es que tiene alguna
    fun recuperarFoto(){
        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        if(perfil.nombre!=""){
            val desRef = Firebase.storage.reference.child("Perfiles/").child(email + "/")//.child(perfil.nombre)
            desRef.listAll().addOnCompleteListener { lista ->
                if (lista.isSuccessful){
                    for (i in lista.result.items) {
                        if (i.name.equals(perfil.nombre)){
                            i.getBytes(ONE_MEGABYTE).addOnCompleteListener() {
                                if (it.isSuccessful) {
                                    val img = getBitmap(it.result)!!
                                    perfil.img = img
                                    binding.imgFoto.setImageBitmap(img)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //mostrar notificaciones
    private fun showAlert(t:Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(t)
        builder.setPositiveButton(R.string.Accept,null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    override fun onNavigationItemSelected( menuItem: MenuItem): Boolean {
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        when (menuItem.itemId) {
            R.id.opDatos -> intenMenu = Intent(this,DatosUsuario::class.java).apply { putExtra("email",email); putExtra("Mod","Modificar");putExtra("perfil",p) }
            R.id.opJornada -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email);putExtra("perfil",p) }
            R.id.opExtra -> intenMenu = Intent(this,HorasExtra::class.java).apply { putExtra("email",email);putExtra("perfil",p) }
            R.id.opCalendario -> intenMenu = Intent(this,Calendario::class.java).apply { putExtra("email",email);putExtra("perfil",p) }
            R.id.opSolicitar -> intenMenu = Intent(this,SolicitarDias::class.java).apply { putExtra("email",email);putExtra("perfil",p) }
            R.id.opJustifi -> intenMenu = Intent(this,Justificante::class.java).apply { putExtra("email",email);putExtra("perfil",p) }
            R.id.opNotifi -> intenMenu = Intent(this,Notificacion::class.java).apply { putExtra("email",email);putExtra("perfil",p) }
            else -> throw IllegalArgumentException("menu option not implemented!!")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        startActivity(intenMenu)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}