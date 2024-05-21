package com.deanil.proyecto.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deanil.proyecto.R
import com.deanil.proyecto.data.entities.ProductoEntity
import com.deanil.proyecto.databinding.ItemProductoBinding

class ProductosAdapter(
    private var productos: MutableList<ProductoEntity>
) : RecyclerView.Adapter<ProductosAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos.get(position)
        with(holder) {
            binding.Nombre.text = producto.nombre
        }
    }

    override fun getItemCount(): Int = productos.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemProductoBinding.bind(view)
    }

    fun updateList(productos: MutableList<ProductoEntity>) {
        this.productos = productos
        notifyDataSetChanged()
    }
}