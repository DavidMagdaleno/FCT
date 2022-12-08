package Adaptadores

import Model.*
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalfct.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AdaptadorNotificacion(var Noti: ArrayList<Notifica>, var  context: Context): RecyclerView.Adapter<AdaptadorNotificacion.ViewHolder>() {
    companion object{
        var perfilActual=""
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Noti.get(position)
        holder.bind(item, context, position, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.item_notificacion, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return Noti.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titulo = view.findViewById(R.id.txtTitulo) as TextView
        private val nombre = view.findViewById(R.id.txtNom) as TextView
        private val pu = view.findViewById(R.id.txtPues) as TextView
        private val tipo = view.findViewById(R.id.txtTipo) as TextView
        private val di = view.findViewById(R.id.txtD) as TextView
        private val img = view.findViewById(R.id.imgRecycler) as ImageView
        private val fondo = view.findViewById(R.id.ContraintNoti) as ConstraintLayout


        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
        fun bind(pers: Notifica, context: Context, pos: Int, miAdaptadorRecycler: AdaptadorNotificacion){

            titulo.text=pers.Titulo
            nombre.text=pers.Trabajador
            pu.text=pers.PuestoTrabajador
            tipo.text=pers.tipo
            di.text=pers.d
            //avatar.setImageBitmap(pers.img)

            if (pers.Estado.equals("Pendiente")){
                fondo.setBackgroundColor(R.color.bluespecial_light)
            }
            if (pers.Estado.equals("Realizado")){
                fondo.setBackgroundColor(R.color.green)
            }

            if (pers.Titulo.equals("Solicitar Días")) {
                img.setImageResource(R.drawable.solicitud)
            }


            itemView.setOnClickListener(
                View.OnClickListener
                {
                    if (perfilActual.equals("Admin")){
                        var db=FirebaseFirestore.getInstance().collection("usuarios")
                        var dni:String="";var nombre:String="";var ape:String="";var dire:String="";var nac:String="";var f:String="";var rhoras = ArrayList<RegistroL>()
                        var NJustifi = ArrayList<AJustificante>();var perfil="";var puesto="";var x=ArrayList<Dias>();var z=ArrayList<Notifica>();var email=""
                        db.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val dialogo: AlertDialog.Builder = AlertDialog.Builder(context)
                                dialogo.setPositiveButton(R.string.ap,
                                    DialogInterface.OnClickListener { dialog, which ->
                                        for (i in 0 until task.result.documents.size){
                                            if (task.result.documents[i].get("Notificacion")!=null && (task.result.documents[i].get("Notificacion") as ArrayList<Notifica>).isNotEmpty()){
                                                dni=task.result.documents[i].get("DNI").toString()
                                                nombre=task.result.documents[i].get("Nombre").toString()
                                                ape=task.result.documents[i].get("Apellidos").toString()
                                                dire=task.result.documents[i].get("Direccion").toString()
                                                nac=task.result.documents[i].get("FechaNac").toString()
                                                if (task.result.documents[i].get("Foto").toString()!=""){f=task.result.documents[i].get("Foto").toString()}
                                                rhoras=task.result.documents[i].get("Registro") as ArrayList<RegistroL>
                                                NJustifi=task.result.documents[i].get("Justificante") as ArrayList<AJustificante>
                                                perfil=task.result.documents[i].get("Perfil").toString()
                                                puesto=task.result.documents[i].get("Puesto").toString()
                                                x= task.result.documents[i].get("Dias") as ArrayList<Dias>
                                                z= task.result.documents[i].get("Notificacion") as ArrayList<Notifica>
                                                for (i in 0 until x.size){
                                                    val n=x[i] as HashMap<String,String>
                                                    val n1=z[i] as HashMap<String,String>
                                                    if (n.getValue("fechaIni").equals(pers.d.substringBefore("-")) && n.getValue("fechaFin").equals(pers.d.substringAfter("-")) && n1.getValue("trabajador").equals(pers.Trabajador)){
                                                        n.replace("estado","Pendiente","Aprobado")
                                                        n1.replace("estado","Pendiente","Realizado")
                                                    }
                                                }
                                                email= task.result.documents[i].id

                                                val user = hashMapOf(
                                                    "DNI" to dni,
                                                    "Nombre" to nombre,
                                                    "Apellidos" to ape,
                                                    "Direccion" to dire,
                                                    "FechaNac" to nac,
                                                    "Foto" to f,
                                                    "Registro" to rhoras,
                                                    "Justificante" to NJustifi,
                                                    "Dias" to x,
                                                    "Perfil" to perfil,
                                                    "Puesto" to puesto,
                                                    "Notificacion" to z
                                                )
                                                db.document(email).set(user).addOnSuccessListener {
                                                }.addOnFailureListener{
                                                }
                                                //miAdaptadorRecycler.Noti.removeAt(pos)
                                            }
                                        }
                                    })
                                dialogo.setNegativeButton(R.string.dn,
                                    DialogInterface.OnClickListener { dialog, which ->
                                        for (i in 0 until task.result.documents.size){
                                            if (task.result.documents[i].get("Notificacion")!=null && (task.result.documents[i].get("Notificacion") as ArrayList<Notifica>).isNotEmpty()){
                                                dni=task.result.documents[i].get("DNI").toString()
                                                nombre=task.result.documents[i].get("Nombre").toString()
                                                ape=task.result.documents[i].get("Apellidos").toString()
                                                dire=task.result.documents[i].get("Direccion").toString()
                                                nac=task.result.documents[i].get("FechaNac").toString()
                                                if (task.result.documents[i].get("Foto").toString()!=""){f=task.result.documents[i].get("Foto").toString()}
                                                rhoras=task.result.documents[i].get("Registro") as ArrayList<RegistroL>
                                                NJustifi=task.result.documents[i].get("Justificante") as ArrayList<AJustificante>
                                                perfil=task.result.documents[i].get("Perfil").toString()
                                                puesto=task.result.documents[i].get("Puesto").toString()
                                                x= task.result.documents[i].get("Dias") as ArrayList<Dias>
                                                z= task.result.documents[i].get("Notificacion") as ArrayList<Notifica>
                                                for (i in 0 until x.size){
                                                    val n=x[i] as HashMap<String,String>
                                                    val n1=z[i] as HashMap<String,String>
                                                    if (n.getValue("fechaIni").equals(pers.d.substringBefore("-")) && n.getValue("fechaFin").equals(pers.d.substringAfter("-")) && n1.getValue("trabajador").equals(pers.Trabajador)){
                                                        n.replace("estado","Pendiente","Denegado")
                                                        n1.replace("estado","Pendiente","Realizado")
                                                    }
                                                }
                                                email= task.result.documents[i].id

                                                val user = hashMapOf(
                                                    "DNI" to dni,
                                                    "Nombre" to nombre,
                                                    "Apellidos" to ape,
                                                    "Direccion" to dire,
                                                    "FechaNac" to nac,
                                                    "Foto" to f,
                                                    "Registro" to rhoras,
                                                    "Justificante" to NJustifi,
                                                    "Dias" to x,
                                                    "Perfil" to perfil,
                                                    "Puesto" to puesto,
                                                    "Notificacion" to z
                                                )
                                                db.document(email).set(user).addOnSuccessListener {
                                                }.addOnFailureListener{
                                                }
                                                //miAdaptadorRecycler.Noti.removeAt(pos)
                                            }
                                        }
                                    })
                                dialogo.setTitle(R.string.Notificacion)
                                dialogo.setMessage(pers.Titulo)
                                dialogo.show()
                            }else{ Log.e("wh", "Error getting documents.", task.exception) }
                        }
                    }
                    miAdaptadorRecycler.notifyDataSetChanged()
                })
        }
    }

}