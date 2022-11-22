package Adaptadores

import Model.HExtra
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalfct.R


class AdaptadorHorasExtra(var horas : ArrayList<HExtra>, var  context: Context): RecyclerView.Adapter<AdaptadorHorasExtra.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = horas.get(position)
        holder.bind(item, context, position, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_extra,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return horas.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fecha = view.findViewById(R.id.txtFechaHe) as TextView
        private val he = view.findViewById(R.id.txtNhe) as TextView
        private val pu = view.findViewById(R.id.txtPuest) as TextView


        @SuppressLint("ResourceAsColor")
        fun bind(pers: HExtra, context: Context, pos: Int, miAdaptadorRecycler: AdaptadorHorasExtra){

            fecha.text=pers.Fecha
            he.text=pers.Horas
            pu.text=pers.Puesto
            //avatar.setImageBitmap(pers.img)

            if (pers.Horas.toInt() > 0) {
                fecha.setTextColor(R.color.green)
                he.setTextColor(R.color.green)
            }


            itemView.setOnClickListener(
                View.OnClickListener
                {


                })

        }
    }

}