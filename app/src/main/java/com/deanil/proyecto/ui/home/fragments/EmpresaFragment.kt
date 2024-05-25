package com.deanil.proyecto.ui.home.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.deanil.proyecto.R
import com.deanil.proyecto.databinding.FragmentEmpresaBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmpresaFragment : Fragment() {

    private lateinit var binding: FragmentEmpresaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmpresaBinding.inflate(layoutInflater, container, false)

        setupAppbar()

        return binding.root
    }

    private fun setupAppbar() {
        val appbar = requireActivity().findViewById<BottomAppBar>(R.id.appbar)
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.GONE
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
        appbar.menu.getItem(0).setVisible(true)
        appbar.menu.getItem(1).setVisible(true)
        appbar.menu.add("add").apply {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_24dp)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                aceptar()
                true
            }
        }
    }

    private fun aceptar() {
        val sharedPreferences = requireActivity().getSharedPreferences("Empresa", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("nombre", binding.tNombre.text.toString())
        editor.putString("nif", binding.tNif.text.toString())
        editor.putString("domicilio", binding.tDomicilio.text.toString())
        editor.putString("ciudad", binding.tCiudad.text.toString())
        editor.putString("provincia", binding.tProvincia.text.toString())
        editor.putString("correo", binding.tCorreo.text.toString())
        editor.putString("telefono", binding.tTelefono.text.toString())

        editor.apply()

        requireActivity().supportFragmentManager.popBackStack()
    }

}