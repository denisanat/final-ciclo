package com.deanil.proyecto.ui.home.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.deanil.proyecto.R
import com.deanil.proyecto.databinding.FragmentHomeBinding
import com.deanil.proyecto.ui.albaranes.fragments.AlbaranesFragment
import com.deanil.proyecto.ui.clientes.fragments.ClientesFragment
import com.deanil.proyecto.ui.facturas.fragments.FacturasFragment
import com.deanil.proyecto.ui.home.activities.MainActivity
import com.deanil.proyecto.ui.productos.fragments.ProductosFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var appbar: BottomAppBar
    private var isEmpresa = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        appbar = requireActivity().findViewById(R.id.appbar)

        setupAppbar()
        initLoadAds()

        val fragmentManager = requireActivity().supportFragmentManager

        val sharedPreferences = requireActivity().getSharedPreferences("Empresa", Context.MODE_PRIVATE)
        val empNombre = sharedPreferences.getString("nombre", null)
        if (empNombre != null)
            isEmpresa = true

        binding.btnEmpresa.apply {
            if (isEmpresa)
                text = empNombre
            setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, EmpresaFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.btnClientes.apply {
            setButtonIcon(R.drawable.clientes)
            setButtonText("Clientes")
            setOnClickListener {
                if (isEmpresa) {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ClientesFragment())
                        .addToBackStack(null)
                        .commit()
                } else
                    noEmpresa()
            }
        }
        binding.btnProductos.apply {
            setButtonIcon(R.drawable.productos)
            setButtonText("Productos")
            setOnClickListener {
                if (isEmpresa) {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProductosFragment())
                        .addToBackStack(null)
                        .commit()
                } else
                    noEmpresa()
            }
        }
        binding.btnFacturas.apply {
            setButtonIcon(R.drawable.facturas)
            setButtonText("Facturas")
            setOnClickListener {
                if (isEmpresa) {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, FacturasFragment())
                        .addToBackStack(null)
                        .commit()
                } else
                    noEmpresa()
            }
        }
        binding.btnAlbaranes.apply {
            setButtonIcon(R.drawable.albaran)
            setButtonText("Albaranes")
            setOnClickListener {
                if (isEmpresa) {
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AlbaranesFragment())
                        .addToBackStack(null)
                        .commit()
                } else
                    noEmpresa()
            }
        }

        return binding.root
    }

    private fun initLoadAds() {
        val adRequest = AdRequest.Builder().build()
        binding.banner.loadAd(adRequest)
    }

    private fun noEmpresa() {
        Toast.makeText(requireContext(), "Debe introducir los datos de su empresa", Toast.LENGTH_SHORT).show()
    }

    private fun setupAppbar() {
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
        appbar.menu.getItem(3).setVisible(true)
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.GONE
    }
}