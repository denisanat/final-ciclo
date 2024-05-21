package com.deanil.proyecto.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deanil.proyecto.R
import com.deanil.proyecto.data.db.DataApplication
import com.deanil.proyecto.data.entities.Linea
import com.deanil.proyecto.data.entities.ProductoEntity
import com.deanil.proyecto.databinding.ItemLineaBinding
import java.util.concurrent.LinkedBlockingQueue

class LineasAdapter (
    private var lineas: MutableList<Linea>
) : RecyclerView.Adapter<LineasAdapter.ViewHolder>(){

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_linea, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val linea = lineas.get(position)
        with(holder) {
            val queue = LinkedBlockingQueue<ProductoEntity>()
            Thread {
                val producto = DataApplication.database.productoDao().getProductoById(linea.codigoProducto)
                queue.add(producto)
            }.start()
            val productoLinea = queue.take()

            binding.ivaProducto.text = productoLinea.iva.toString()
            binding.precioProducto.text = productoLinea.precioUnitario.toString()
            binding.nombreProducto.text = productoLinea.nombre
            binding.cantidadProducto.text = linea.cantidadProducto.toString()
            binding.descuentoProducto.text = linea.descuentoAplicado.toString()
        }
    }

    override fun getItemCount(): Int = lineas.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemLineaBinding.bind(view)
    }

    fun updateList(lineas: MutableList<Linea>) {
        this.lineas = lineas
        notifyDataSetChanged()
    }
}