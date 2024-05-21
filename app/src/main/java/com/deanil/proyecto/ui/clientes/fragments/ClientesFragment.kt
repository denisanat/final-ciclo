package com.deanil.proyecto.ui.clientes.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deanil.proyecto.R
import com.deanil.proyecto.data.adapters.ClientesAdapter
import com.deanil.proyecto.data.db.DataApplication
import com.deanil.proyecto.data.entities.ClienteEntity
import com.deanil.proyecto.databinding.FragmentClientesBinding
import com.deanil.proyecto.ui.extra.FragmentWithAppBar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.LinkedBlockingQueue

class ClientesFragment : FragmentWithAppBar() {

    private lateinit var binding: FragmentClientesBinding
    private lateinit var appbar: BottomAppBar
    private lateinit var clientesAdapter: ClientesAdapter
    private var clientes = mutableListOf<ClienteEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientesBinding.inflate(inflater, container, false)
        appbar = requireActivity().findViewById(R.id.appbar)
        clientesAdapter = ClientesAdapter(clientes)

        getClientes()
        setupAppbar()
        setupRecycler()

        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd).setOnClickListener {
            cambiarFragment()
        }

        binding.searchCliente.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString()
                val listaFiltrada = clientes.filter {
                    it.nombre.contains(texto, ignoreCase = true)
                }
                clientesAdapter.updateList(listaFiltrada.toMutableList())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    override fun setAppBarButtons(appbar: BottomAppBar) {
        appbar.menu.getItem(0).setVisible(false)
        appbar.menu.getItem(1).setVisible(false)
    }

    private fun setupRecycler() {
        val recycler = binding.recyclerClientes
        clientesAdapter.updateList(clientes)

        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = clientesAdapter
        }
    }

    private fun getClientes() {
        val queue = LinkedBlockingQueue<MutableList<ClienteEntity>>()
        Thread {
            val clientes = DataApplication.database.clienteDao().getAllClientes()
            queue.add(clientes)
        }.start()
        clientes = queue.take()
    }

    private fun cambiarFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, NewClienteFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun clearItems() {
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
    }

    private fun setupAppbar() {
        clearItems()
        appbar.menu.getItem(0).setVisible(true)
        appbar.menu.getItem(1).setVisible(true)
    }

}