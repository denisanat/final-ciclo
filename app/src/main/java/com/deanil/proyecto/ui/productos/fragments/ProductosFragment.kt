package com.deanil.proyecto.ui.productos.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deanil.proyecto.R
import com.deanil.proyecto.data.adapters.ProductosAdapter
import com.deanil.proyecto.data.db.DataApplication
import com.deanil.proyecto.data.entities.ProductoEntity
import com.deanil.proyecto.databinding.FragmentClientesBinding
import com.deanil.proyecto.databinding.FragmentProductosBinding
import com.deanil.proyecto.ui.clientes.fragments.NewClienteFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.LinkedBlockingQueue

class ProductosFragment : Fragment() {

    private lateinit var binding: FragmentProductosBinding

    private lateinit var appbar: BottomAppBar

    private lateinit var productosAdapter: ProductosAdapter
    private var productos = mutableListOf<ProductoEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductosBinding.inflate(inflater, container, false)
        getProductos()
        setupAppbar()
        setupRecycler()

        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd).setOnClickListener {
            cambiarFragment()
        }

        return binding.root
    }

    private fun cambiarFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, NewProductoFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecycler() {
        val recycler = binding.recyclerProductos
        productosAdapter = ProductosAdapter(productos)

        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productosAdapter
        }
    }

    private fun getProductos() {
        val queue = LinkedBlockingQueue<MutableList<ProductoEntity>>()
        Thread {
            val productos = DataApplication.database.productoDao().getAllProductos()
            queue.add(productos)
        }.start()
        productos = queue.take()
    }

    private fun clearItems() {
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
    }

    private fun setupAppbar() {
        appbar = requireActivity().findViewById(R.id.appbar)
        clearItems()
        appbar.menu.getItem(0).setVisible(true)
        appbar.menu.getItem(1).setVisible(true)
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.VISIBLE
    }

}