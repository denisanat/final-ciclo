package com.deanil.proyecto.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deanil.proyecto.R
import com.deanil.proyecto.data.entities.FacturaEntity
import com.deanil.proyecto.data.interfaces.OnClickListener
import com.deanil.proyecto.databinding.ItemFacturaBinding

class FacturasAdapter (
    private var facturas: MutableList<FacturaEntity>,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<FacturasAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_factura, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val factura = facturas.get(position)
        with(holder) {
            setClickListener(factura)
            binding.tNumeroFactura.text = factura.numeroFactura
        }
    }

    override fun getItemCount(): Int = facturas.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemFacturaBinding.bind(view)

        fun setClickListener(factura: FacturaEntity) {
            binding.root.setOnClickListener { onClickListener.onClick(factura) }
        }
    }

    fun updateList(facturas: MutableList<FacturaEntity>) {
        this.facturas = facturas
        notifyDataSetChanged()
    }

}