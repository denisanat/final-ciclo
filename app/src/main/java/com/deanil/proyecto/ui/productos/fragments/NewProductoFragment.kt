package com.deanil.proyecto.ui.productos.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.deanil.proyecto.R
import com.deanil.proyecto.data.db.DataApplication
import com.deanil.proyecto.data.entities.ProductoEntity
import com.deanil.proyecto.databinding.FragmentNewProductoBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NewProductoFragment : Fragment() {

    private lateinit var binding: FragmentNewProductoBinding
    private lateinit var appbar: BottomAppBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewProductoBinding.inflate(layoutInflater, container, false)
        appbar = requireActivity().findViewById(R.id.appbar)
        initAppbar()

        return binding.root
    }

    private fun initAppbar() {
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.GONE
        appbar.menu.add("add").apply {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_24dp)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                crearProducto()
                true
            }
        }
    }

    private fun crearProducto() {
        if (!validarForm()) {
            Toast.makeText(context, "Faltan campos por rellenar", Toast.LENGTH_SHORT)
        } else {
            val producto = ProductoEntity(
                nombre = binding.tNombre.text.toString(),
                precioUnitario = binding.tPrecio.text.toString().toFloat(),
                iva = binding.tIva.text.toString().toFloat()
            )
            Thread {
                DataApplication.database.productoDao().insertProducto(producto)
            }.start()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }

    private fun validarForm(): Boolean {
        return !binding.tNombre.text.isNullOrBlank() &&
                !binding.tIva.text.isNullOrBlank() &&
                !binding.tPrecio.text.isNullOrBlank()
    }

}