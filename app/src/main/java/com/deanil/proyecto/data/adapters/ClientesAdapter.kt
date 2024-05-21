package com.deanil.proyecto.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deanil.proyecto.R
import com.deanil.proyecto.data.entities.ClienteEntity
import com.deanil.proyecto.databinding.ItemClienteBinding

class ClientesAdapter(
    private var clientes: MutableList<ClienteEntity>
) : RecyclerView.Adapter<ClientesAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cliente = clientes.get(position)
        with(holder) {
            binding.Nombre.text = cliente.nombre
        }
    }

    override fun getItemCount() = clientes.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemClienteBinding.bind(view)
    }

    fun updateList(clientes: MutableList<ClienteEntity>) {
        this.clientes = clientes
        notifyDataSetChanged()
    }
}