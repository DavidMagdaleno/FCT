package com.example.proyectofinalfct

import Model.AJustificante
import Model.Dias
import Model.ImgPerfil
import Model.RegistroL
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
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
    private val database= Firebase.storage
    val ref2=database.reference
    //private val cameraRequest=1888
    val ONE_MEGABYTE: Long = 1024 * 1024
    var perfil:ImgPerfil=ImgPerfil("",null)
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: ActivityDatosUsuarioBinding

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
        if (modi=="Modificar"){

            db.collection("usuarios").document(email).get().addOnSuccessListener {
                //Si encuentra el documento será satisfactorio este listener y entraremos en él.
                binding.txtDNI.setText(it.get("DNI").toString())
                binding.txtName.setText(it.get("Nombre").toString())
                binding.txtApe.setText(it.get("Apellidos").toString())
                binding.txtDire.setText(it.get("Direccion").toString())
                binding.txtNac.setText(it.get("FechaNac").toString())
                if (it.get("Foto").toString()!=""){perfil.nombre=it.get("Foto").toString()}
                rhoras=it.get("Registro") as ArrayList<RegistroL>
                NJustifi=it.get("Justificante") as ArrayList<AJustificante>
                Sdias=it.get("Dias") as ArrayList<Dias>
                //Toast.makeText(this, "Recuperado",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this, "Algo ha ido mal al recuperar",Toast.LENGTH_SHORT).show()
            }

            binding.txtDNI.isFocusable=false
            binding.txtDNI.isFocusableInTouchMode=false

        }

        binding.btnAcept.setOnClickListener {
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
                "Dias" to Sdias
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

    override fun onNavigationItemSelected(@NonNull menuItem: MenuItem): Boolean {
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email").toString()
        when (menuItem.itemId) {
            R.id.opDatos -> intenMenu = Intent(this,DatosUsuario::class.java).apply { putExtra("email",email); putExtra("Mod","Modificar") }
            R.id.opJornada -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
            R.id.opExtra -> intenMenu = Intent(this,HorasExtra::class.java).apply { putExtra("email",email) }
            R.id.opCalendario -> intenMenu = Intent(this,Calendario::class.java).apply { putExtra("email",email) }
            R.id.opSolicitar -> intenMenu = Intent(this,SolicitarDias::class.java).apply { putExtra("email",email) }
            R.id.opJustifi -> intenMenu = Intent(this,Justificante::class.java).apply { putExtra("email",email) }
            //R.id.opNotifi -> intenMenu = Intent(this,RegistroLaboral::class.java).apply { putExtra("email",email) }
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

    fun volver(view: View){
        this.finish()
    }
}