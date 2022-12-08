package com.example.proyectofinalfct

import Model.Dias
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.proyectofinalfct.databinding.ActivityCalendarioBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import sun.bob.mcalendarview.MarkStyle
import java.text.SimpleDateFormat
import java.util.*


class Calendario : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding:ActivityCalendarioBinding
    private lateinit var intenMenu: Intent
    private val db = FirebaseFirestore.getInstance()
    var sDias = ArrayList<Dias>()
    var em=""
    var context=this


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCalendarioBinding.inflate(layoutInflater)
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
        em=email
        val per = bundle?.getString("perfil").toString()

        sacarRegistro()

        binding.btnVolve.setOnClickListener {
           intenMenu= Intent(this,Menu::class.java).apply { putExtra("email",email);putExtra("perfil",per) }
            startActivity(intenMenu)
        }

    }


    fun sacarRegistro(){
        var dias=0
        var me="";var d="";var y=""
        try {
            recuperarRegistro(object : RolCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun diasRecibido(DiasNuevo: ArrayList<Dias>) {
                    sDias = DiasNuevo
                    //Manipular el arraylist de Dias para seleccionar los dias en el calendario en funcion de su tipo y estado
                    for (i in 0 until sDias.size){
                        val x=sDias[i] as HashMap<String, String>
                        if (sDias.isNotEmpty()){
                            if (x.getValue("tipo").equals("Asuntos Propios")){
                                me=x.get("fechaIni")!!.replaceAfterLast("/","").replaceBefore("/","").substringAfter("/").substringBefore("/")
                                y=x.get("fechaIni")!!.substringAfterLast("/")
                                d=x.get("fechaIni")!!.substringBefore("/")
                                if (x.getValue("estado").equals("Pendiente")){
                                    binding.calendar.markDate(y.toInt(), me.toInt(), d.toInt()).setMarkedStyle(MarkStyle.BACKGROUND,Color.YELLOW)
                                }
                                if (x.getValue("estado").equals("Aprobado")){
                                    binding.calendar.markDate(y.toInt(), me.toInt(), d.toInt()).setMarkedStyle(MarkStyle.BACKGROUND,Color.GREEN)
                                }
                                if (x.getValue("estado").equals("Denegado")){
                                    binding.calendar.markDate(y.toInt(), me.toInt(), d.toInt()).setMarkedStyle(MarkStyle.BACKGROUND,Color.RED)
                                }
                            }
                            if (x.getValue("tipo").equals("Vacaciones")){
                                dias=diferenciaHoras(x.getValue("fechaIni"),x.getValue("fechaFin")).toInt()
                                me=x.get("fechaIni")!!.replaceAfterLast("/","").replaceBefore("/","").substringAfter("/").substringBefore("/")
                                y=x.get("fechaIni")!!.substringAfterLast("/")
                                d=x.get("fechaIni")!!.substringBefore("/")
                                if (x.getValue("estado").equals("Pendiente")){
                                    for (i in 0..dias){
                                        //showAlert("p: "+(d.toInt()+i))
                                        binding.calendar.markDate(y.toInt(), pasarMes(me,d.toInt()+i).toInt(), d.toInt()+i).setMarkedStyle(MarkStyle.BACKGROUND,Color.YELLOW)
                                    }
                                }
                                if (x.getValue("estado").equals("Aprobado")){
                                    for (i in 0..dias){
                                        binding.calendar.markDate(y.toInt(), pasarMes(me,d.toInt()+i).toInt(), d.toInt()+i).setMarkedStyle(MarkStyle.BACKGROUND,Color.GREEN)
                                    }
                                }
                                if (x.getValue("estado").equals("Denegado")){
                                    for (i in 0..dias){
                                        binding.calendar.markDate(y.toInt(), pasarMes(me,d.toInt()+i).toInt(), d.toInt()+i).setMarkedStyle(MarkStyle.BACKGROUND,Color.RED)
                                    }
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
        fun diasRecibido(DiasNuevo: ArrayList<Dias>)
    }

    fun recuperarRegistro( callback:RolCallback){
        db.collection("usuarios").document(em).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //poner los demas para recuperar todos los datos
                    val x= task.result.data?.get("Dias") as ArrayList<Dias>
                    if (callback != null) {
                        callback.diasRecibido(x);
                    }
                } else {
                    Log.e("wh", "Error getting documents.", task.exception)
                }
            }
    }

    //sacar el numero de dias entre la fecha de inicio y la de fin
    fun diferenciaHoras(fe1:String, fe2:String): Long{
        var f1=stringtodate(fe1)
        var f2=stringtodate(fe2)
        var diff=f2.time-f1.time
        var dias=(((diff/1000)/60)/60)/24
        return dias
    }

    private fun stringtodate(t:String): Date {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
        val datep = formatter.parse(t)
        return datep
    }

    private fun showAlert(t:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.Notificacion)
        builder.setMessage(t)
        builder.setPositiveButton(R.string.Accept,null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //dado que la fechas estan en string es necesario este metodo para evitar errores al pasar de mes
    private fun pasarMes(m:String, d:Int):String{
        var me:String=""
        if ((m.toInt())%2==0){
            me = if (m.toInt()==2){
                if (d>28){
                    (m.toInt()+1).toString()
                }else{ m }
            }else{
                if (d>30){
                    (m.toInt()+1).toString()
                }else{ m }
            }
        }else{
            me = if (d>31){
                (m.toInt()+1).toString()
            }else{ m }
        }
        return me
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