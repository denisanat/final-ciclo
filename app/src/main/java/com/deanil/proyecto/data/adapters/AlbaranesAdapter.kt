package com.deanil.proyecto.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deanil.proyecto.R
import com.deanil.proyecto.data.entities.AlbaranEntity
import com.deanil.proyecto.data.entities.FacturaEntity
import com.deanil.proyecto.data.interfaces.OnClickListener
import com.deanil.proyecto.data.interfaces.onAlbaranClickListener
import com.deanil.proyecto.databinding.ItemAlbaranBinding

class AlbaranesAdapter (
    private var albaranes: List<AlbaranEntity>,
    private val onClickListener: onAlbaranClickListener
) : RecyclerView.Adapter<AlbaranesAdapter.ViewHolder>(){

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_albaran, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val albaran = albaranes.get(position)

        with(holder) {
            setClickListener(albaran)
            binding.numero.text = albaran.numeroAlbaran
        }
    }

    override fun getItemCount(): Int = albaranes.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemAlbaranBinding.bind(view)

        fun setClickListener(albaran: AlbaranEntity) {
            binding.root.setOnClickListener { onClickListener.onClick(albaran) }
        }
    }

    fun updateList(albaranes: List<AlbaranEntity>) {
        this.albaranes = albaranes
        notifyDataSetChanged()
    }
}