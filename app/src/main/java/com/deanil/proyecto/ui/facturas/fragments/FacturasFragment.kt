package com.deanil.proyecto.ui.facturas.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deanil.proyecto.R
import com.deanil.proyecto.data.adapters.FacturasAdapter
import com.deanil.proyecto.data.db.DataApplication
import com.deanil.proyecto.data.entities.FacturaEntity
import com.deanil.proyecto.databinding.FragmentFacturasBinding
import com.deanil.proyecto.ui.clientes.fragments.NewClienteFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.LinkedBlockingQueue


class FacturasFragment : Fragment() {

    private lateinit var binding: FragmentFacturasBinding
    private lateinit var appbar: BottomAppBar

    private lateinit var facturasAdapter: FacturasAdapter

    private var facturas = mutableListOf<FacturaEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacturasBinding.inflate(inflater, container, false)
        facturasAdapter = FacturasAdapter(facturas)
        appbar = requireActivity().findViewById(R.id.appbar)

        getFacturas()
        setup()

        return binding.root
    }

    private fun getFacturas() {
        val queue = LinkedBlockingQueue<MutableList<FacturaEntity>>()
        Thread {
            val facturas = DataApplication.database.facturaDao().getAllFacturas()
            queue.add(facturas)
        }.start()
        facturas = queue.take()
    }

    private fun setup() {
        setupBtn()
        setupRecycler()
        setappbar()
    }

    private fun setappbar() {
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
        appbar.menu.getItem(0).setVisible(true)
        appbar.menu.getItem(1).setVisible(true)
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.VISIBLE
    }

    private fun setupRecycler() {
        binding.recyclerFacturas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = facturasAdapter
        }

        facturasAdapter.updateList(facturas)
    }

    private fun setupBtn() {
        binding.search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString()
                val listaFiltrada = facturas.filter {
                    it.numeroFactura.contains(texto, ignoreCase = true)
                }
                facturasAdapter.updateList(listaFiltrada.toMutableList())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .setOnClickListener {
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, NewFacturaFragment())
                    .addToBackStack(null)
                    .commit()
            }
    }

}