package com.example.proyectofinalfct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinalfct.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance() //Variable con la que accederemos a Firestore. Será una instancia a la bd.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Con esto lanzamos eventos personalizados a GoogleAnalytics que podemos ver en nuestra consola de FireBase.
        val analy: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message","Integración completada")
        analy.logEvent("InitScreen",bundle)

        title = "Autenticación"
        binding.btnPruebaGuardar.setOnClickListener(){
            if (!binding.txtUser.text.trim().isNullOrEmpty() && !binding.txtPwd.text.trim().isNullOrEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.txtUser.text.trim().toString(),binding.txtPwd.text.trim().toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        irRegistro(it.result?.user?.email?:"")  //Esto de los interrogantes es por si está vacío el email. se envia a recoger los datos del usuario
                    } else {
                        showAlert(R.string.Login_Error_1)
                    }
                }
            }else{
                showAlert(R.string.Login_Error_2)
            }
        }

        binding.btnLogin.setOnClickListener(){
            if (!binding.txtPwd.text.trim().isNullOrEmpty() && !binding.txtUser.text.trim().isNullOrEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.txtUser.text.trim().toString(),binding.txtPwd.text.trim().toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        irMenu()
                        //irHome(it.result?.user?.email?:"",ProviderType.BASIC)  //Esto de los interrogantes es por si está vacío el email.
                    } else {
                        showAlert(R.string.Login_Error_3)
                    }
                }
            }
        }



    }

    private fun irMenu(){
        val homeIntent = Intent(this,Menu::class.java).apply {
            putExtra("email",binding.txtUser.text.trim().toString())
            //putExtra("Mod","NONE")
            //putExtra("provider",provider.name)
            HeadFragment.email=binding.txtUser.text.trim().toString()
        }
        startActivity(homeIntent)
    }

    private fun irRegistro(email:String){
        val homeIntent = Intent(this,DatosUsuario::class.java).apply {
            putExtra("email",email)
            putExtra("Mod","None")
        }
        startActivity(homeIntent)
    }

    private fun showAlert(t:Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(t)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}