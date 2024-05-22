package com.deanil.proyecto.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.deanil.proyecto.R
import com.deanil.proyecto.databinding.FragmentHomeBinding
import com.deanil.proyecto.ui.albaranes.fragments.AlbaranesFragment
import com.deanil.proyecto.ui.clientes.fragments.ClientesFragment
import com.deanil.proyecto.ui.facturas.fragments.FacturasFragment
import com.deanil.proyecto.ui.home.activities.MainActivity
import com.deanil.proyecto.ui.productos.fragments.ProductosFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var appbar: BottomAppBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        appbar = requireActivity().findViewById(R.id.appbar)

        setupAppbar()

        val fragmentManager = requireActivity().supportFragmentManager
        binding.btnClientes.apply {
            setButtonIcon(R.drawable.clientes)
            setButtonText("Clientes")
            setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ClientesFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        binding.btnProductos.apply {
            setButtonIcon(R.drawable.productos)
            setButtonText("Productos")
            setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ProductosFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        binding.btnFacturas.apply {
            setButtonIcon(R.drawable.facturas)
            setButtonText("Facturas")
            setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, FacturasFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        binding.btnAlbaranes.apply {
            setButtonIcon(R.drawable.albaran)
            setButtonText("Albaranes")
            setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, AlbaranesFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    private fun setupAppbar() {
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
        appbar.menu.getItem(3).setVisible(true)
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.GONE
    }
}