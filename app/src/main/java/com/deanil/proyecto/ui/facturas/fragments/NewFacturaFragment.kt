package com.deanil.proyecto.ui.facturas.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.deanil.proyecto.R
import com.deanil.proyecto.data.adapters.LineasAdapter
import com.deanil.proyecto.data.db.DataApplication
import com.deanil.proyecto.data.entities.ClienteEntity
import com.deanil.proyecto.data.entities.FacturaEntity
import com.deanil.proyecto.data.entities.Linea
import com.deanil.proyecto.data.entities.ProductoEntity
import com.deanil.proyecto.databinding.DialogoNuevaLineaBinding
import com.deanil.proyecto.databinding.FragmentNewFacturaBinding
import com.deanil.proyecto.ui.extra.DatePickerFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.LinkedBlockingQueue

class NewFacturaFragment : Fragment() {

    private lateinit var binding: FragmentNewFacturaBinding
    private lateinit var appbar: BottomAppBar

    private var clientes = mutableListOf<ClienteEntity>()
    private lateinit var adapterClientes: ArrayAdapter<String>
    private var cliente: ClienteEntity? = null

    private var lineas = mutableListOf<Linea>()
    private lateinit var lineasAdapter: LineasAdapter
    private var productos = mutableListOf<ProductoEntity>()
    private lateinit var productosAdapter: ArrayAdapter<String>
    private var producto: ProductoEntity? = null

    private lateinit var bindingDialogoNuevaLineaBinding: DialogoNuevaLineaBinding
    private lateinit var dialogo: Dialog

    private var importeTotal = 0f
    private var importeIva = 0f
    private var importeTotalPagar = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewFacturaBinding.inflate(inflater, container, false)
        appbar = requireActivity().findViewById(R.id.appbar)
        lineasAdapter = LineasAdapter(lineas)

        setupAppbar()
        initSpinners()
        initDatePicker()
        initLineasRecycler()

        initDialogo()

        return binding.root
    }

    private fun initDialogo() {
        bindingDialogoNuevaLineaBinding = DialogoNuevaLineaBinding.inflate(layoutInflater)
        dialogo = Dialog(requireContext())
        dialogo.apply {
            setContentView(bindingDialogoNuevaLineaBinding.root)
            setCancelable(false)
            create()
        }
        binding.addLinea.setOnClickListener {
            dialogo.show()
        }
        bindingDialogoNuevaLineaBinding.apply {
            btnCancel.setOnClickListener { dialogo.dismiss() }
            btnSubmit.setOnClickListener {
                validarLinea()
                dialogo.dismiss()
            }
        }
        dialogTextView()
    }

    private fun validarLinea() {
        if (producto != null && bindingDialogoNuevaLineaBinding.cantidadProducto.text.isNotBlank()) {
            val descuento: Float
            if (bindingDialogoNuevaLineaBinding.descuentoProducto.text.isBlank()) descuento = 0f
            else descuento = bindingDialogoNuevaLineaBinding.descuentoProducto.text.toString().toFloat()
            val linea = Linea(
                codigoProducto = producto!!.codigo,
                cantidadProducto = bindingDialogoNuevaLineaBinding.cantidadProducto.text.toString().toFloat(),
                numeroFactura = 0,
                descuentoAplicado = descuento
            )
            lineas.add(linea)
            lineasAdapter.updateList(lineas)
            actualizarPrecios()
        }
    }

    private fun actualizarPrecios() {
        for (linea in lineas) {
            val producto = getProducto(linea.codigoProducto)
            importeTotal = importeTotal + (producto.precioUnitario * linea.cantidadProducto)
            importeIva = importeIva + (producto.precioUnitario * (producto.iva / 100) * linea.cantidadProducto)
        }
        binding.importeTotal.text = "Importe total: ${importeTotal}€"
        binding.importeTotalIva.text = "Importe total iva: $importeIva€"
        importeTotalPagar = importeTotal + importeIva
        binding.importeTotalPagar.text = "Importe total a pagar: $importeTotalPagar€"
    }

    private fun dialogTextView() {
        val nombresProductos = productos.map {it.nombre}.toMutableList()
        productosAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresProductos)
        getProductos()
        bindingDialogoNuevaLineaBinding.nombreProducto.apply {
            setAdapter(productosAdapter)
            threshold = 1
            setOnItemClickListener { parent, view, position, id ->
                val selectedProducto: String = parent.getItemAtPosition(position) as String
                producto = getProducto(selectedProducto)
                bindingDialogoNuevaLineaBinding.ivaProducto.text = producto?.iva.toString()
                bindingDialogoNuevaLineaBinding.precioProducto.text = producto?.precioUnitario.toString()
            }
        }
    }

    private fun initLineasRecycler() {
        val recycler = binding.recyclerLineas

        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lineasAdapter
        }
    }

    private fun initSpinners() {
        val nombresClientes = clientes.map { it.nombre }.toMutableList()
        adapterClientes = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresClientes)
        getClientes()
        binding.cliente.apply {
            setAdapter(adapterClientes)
            threshold = 1
            setOnItemClickListener { parent, view, position, id ->
                val selectedCliente: String = parent.getItemAtPosition(position) as String
                if (getCliente(selectedCliente) != null) cliente = getCliente(selectedCliente)
            }
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val response = DataApplication.database.clienteDao().getClienteByNombre(binding.cliente.text.toString())
                        if (response != null)
                            cliente = response
                        else
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "No existe ese cliente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }
        }
    }

    private fun getCliente(nombre: String): ClienteEntity? {
        val queue = LinkedBlockingQueue<ClienteEntity?>()
        Thread {
            val cliente = DataApplication.database.clienteDao().getClienteByNombre(nombre)
            queue.add(cliente)
        }.start()
        return queue.take()
    }

    private fun getProducto(nombre: String): ProductoEntity? {
        val queue = LinkedBlockingQueue<ProductoEntity?>()
        Thread {
            val producto = DataApplication.database.productoDao().getProductoByNombre(nombre)
            queue.add(producto)
        }.start()
        return queue.take()
    }

    private fun getProducto(id: Int): ProductoEntity {
        val queue = LinkedBlockingQueue<ProductoEntity>()
        Thread {
            val producto = DataApplication.database.productoDao().getProductoById(id)
            queue.add(producto)
        }.start()
        return queue.take()
    }

    private fun getProductos() {
        val queue = LinkedBlockingQueue<MutableList<ProductoEntity>>()
        Thread {
            val productos = DataApplication.database.productoDao().getAllProductos()
            queue.add(productos)
        }.start()
        productos = queue.take()
        productosAdapter.apply {
            addAll(productos.map {it.nombre}.toMutableList())
            notifyDataSetChanged()
        }
    }

    private fun getClientes() {
        val queue = LinkedBlockingQueue<MutableList<ClienteEntity>>()
        Thread {
            val clientes = DataApplication.database.clienteDao().getAllClientes()
            queue.add(clientes)
        }.start()
        clientes = queue.take()
        adapterClientes.apply {
            addAll(clientes.map { it.nombre }.toMutableList())
            notifyDataSetChanged()
        }
    }

    private fun initDatePicker() {
        binding.btnFechaEmision.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnFechaVencimiento.setOnClickListener {
            showDatePickerDialog2()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment {day, month, year -> onDateSelected(day, month, year)}
        datePicker.show(requireActivity().supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.btnFechaEmision.setText("$day/$month/$year")
    }

    private fun showDatePickerDialog2() {
        val datePicker = DatePickerFragment {day, month, year -> onDateSelected2(day, month, year)}
        datePicker.show(requireActivity().supportFragmentManager, "datePicker")
    }

    fun onDateSelected2(day: Int, month: Int, year: Int) {
        binding.btnFechaVencimiento.setText("$day/$month/$year")
    }

    private fun setupAppbar() {
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.GONE
        appbar.menu.add("add").apply {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_24dp)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                crearFactura()
                true
            }
        }
    }

    private fun crearFactura() {
        if (cliente != null) {
            val factura = FacturaEntity(
                numeroFactura = binding.campoNumeroFactura.text.toString(),
                estado = FacturaEntity.Estados.NOPAGADA,
                fechaEmision = stringToDate(binding.btnFechaEmision.text.toString()),
                fechaVencimiento = stringToDate(binding.btnFechaVencimiento.text.toString()),
                idCliente = cliente!!.idCliente,
                metodoDePago = FacturaEntity.MetodosDePago.EFECTIVO,
                importeTotal = importeTotal,
                importeTotalIva = importeIva,
                importeTotalPagar = importeTotalPagar
            )
            Thread {
                DataApplication.database.facturaDao().insertFactura(factura)
            }.start()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }

    private fun stringToDate(fecha: String): Date {
        val fechaSeparada = fecha.split("/")
        return Date(fechaSeparada.get(0).toInt(), fechaSeparada.get(1).toInt(), fechaSeparada.get(2).toInt())
        //val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        //    return try {
        //        format.parse(dateString)
        //    } catch (e: ParseException) {
        //        // Manejar el error apropiadamente
        //        null
        //    }
    }

}