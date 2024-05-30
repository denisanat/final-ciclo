package com.deanil.proyecto.ui.clientes.fragments

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
import com.deanil.proyecto.data.entities.ClienteEntity
import com.deanil.proyecto.databinding.FragmentNewClienteBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NewClienteFragment : Fragment() {

    lateinit var binding: FragmentNewClienteBinding
    private lateinit var appbar: BottomAppBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewClienteBinding.inflate(inflater, container, false)
        appbar = requireActivity().findViewById(R.id.appbar)
        setupAppbar()

        return binding.root
    }

    private fun clearItems() {
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
    }

    private fun setupAppbar() {
        clearItems()
        appbar.menu.getItem(0).setVisible(true)
        appbar.menu.getItem(1).setVisible(true)
        appbar.menu.add("add").apply {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_24dp)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                crearCliente()
                true
            }
        }
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.GONE
    }

    fun validarForm(): Boolean {
        return !binding.tNombre.text.isNullOrBlank() &&
                !binding.tNif.text.isNullOrBlank() &&
                !binding.tDomicilio.text.isNullOrBlank() &&
                !binding.tCorreo.text.isNullOrBlank() &&
                !binding.tTelefono.text.isNullOrBlank() &&
                !binding.tCiudad.text.isNullOrBlank() &&
                !binding.tProvincia.text.isNullOrBlank()
    }

    fun crearCliente() {
        if (!validarForm()) {
            Toast.makeText(context, "Los datos no son correctos", Toast.LENGTH_SHORT).show()
        } else {
            val cliente = ClienteEntity(
                nombre = binding.tNombre.text.toString(),
                nif = binding.tNif.text.toString(),
                domicilio = binding.tDomicilio.text.toString(),
                ciudad = binding.tCiudad.text.toString(),
                provincia = binding.tProvincia.text.toString(),
                email = binding.tCorreo.text.toString(),
                telefono = binding.tTelefono.text.toString() )
            Thread {
                DataApplication.database.clienteDao().insertCliente(cliente)
            }.start()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }
}