package com.deanil.proyecto.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.deanil.proyecto.R
import com.deanil.proyecto.databinding.FragmentHomeBinding
import com.deanil.proyecto.ui.clientes.fragments.ClientesFragment
import com.deanil.proyecto.ui.facturas.fragments.FacturasFragment
import com.deanil.proyecto.ui.home.activities.MainActivity
import com.deanil.proyecto.ui.productos.fragments.ProductosFragment
import com.google.android.material.bottomappbar.BottomAppBar

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setVisibilityAppBarButtons()

        val fragmentManager = requireActivity().supportFragmentManager
        binding.botonCliente.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ClientesFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnProductos.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProductosFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnFacturas.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FacturasFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        requireActivity().findViewById<CoordinatorLayout>(R.id.app_bar).visibility = View.VISIBLE
    }

    fun setVisibilityAppBarButtons() {
        requireActivity().findViewById<CoordinatorLayout>(R.id.app_bar).visibility = View.GONE
    }
}