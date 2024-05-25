package com.deanil.proyecto.ui.facturas.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.concurrent.LinkedBlockingQueue

class NewFacturaFragment : Fragment() {

    private lateinit var binding: FragmentNewFacturaBinding
    private lateinit var appbar: BottomAppBar

    private var clientes = mutableListOf<ClienteEntity>()
    private lateinit var adapterClientes: ArrayAdapter<String>
    private var cliente: ClienteEntity? = null

    private var lineas = mutableListOf<Linea>()
    private var productosSeleccionados = mutableListOf<ProductoEntity>()
    private lateinit var lineasAdapter: LineasAdapter
    private var productos = mutableListOf<ProductoEntity>()
    private lateinit var productosAdapter: ArrayAdapter<String>
    private var producto: ProductoEntity? = null

    private lateinit var bindingDialogoNuevaLineaBinding: DialogoNuevaLineaBinding
    private lateinit var dialogo: Dialog

    private var importeTotal = 0f
    private var importeIva = 0f
    private var importeTotalPagar = 0f

    private var isFecha = false
    private var isFecha2 = false

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
            productosSeleccionados.add(producto!!)
            actualizarPrecios()
        }
    }

    private fun actualizarPrecios() {
        importeTotal = 0f
        importeIva = 0f
        for (linea in lineas) {
            val producto = getProducto(linea.codigoProducto)
            importeTotal += (producto.precioUnitario * linea.cantidadProducto)
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
        binding.btnFechaEmision.setText("$day/${month+1}/$year")
        isFecha = true
    }

    private fun showDatePickerDialog2() {
        val datePicker = DatePickerFragment {day, month, year -> onDateSelected2(day, month, year)}
        datePicker.show(requireActivity().supportFragmentManager, "datePicker")
    }

    fun onDateSelected2(day: Int, month: Int, year: Int) {
        binding.btnFechaVencimiento.setText("$day/${month+1}/$year")
        isFecha2 = true
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

    private lateinit var factura: FacturaEntity
    private fun crearFactura() {
        if (validarFactura()) {
            factura = FacturaEntity(
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
            /*
            Thread {
                DataApplication.database.facturaDao().insertFactura(factura)
            }.start()
             */
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()

            if (checkPermission())
                Toast.makeText(requireContext(), "Permiso aceptado", Toast.LENGTH_SHORT).show()
            else
                requestPermission()

            crearPdf()
        }
    }

    private fun stringToDate(fecha: String): Date {
        val fechaSeparada = fecha.split("/")
        return Date(fechaSeparada.get(0).toInt(), fechaSeparada.get(1).toInt(), fechaSeparada.get(2).toInt())
    }

    private fun validarFactura(): Boolean {
        if (!(cliente != null &&
            isFecha && isFecha2 &&
            productos.size > 0 &&
            binding.campoNumeroFactura.text.toString().isNotBlank())) {
            Toast.makeText(requireContext(), "No pueden haber campos vacios", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (stringToDate(binding.btnFechaEmision.text.toString())
            .after(stringToDate(binding.btnFechaVencimiento.text.toString()))) {
            Toast.makeText(
                requireContext(),
                "La fecha de vencimiento no puede ser anterior a la de emision",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else
            return true
    }

    // PDF ---
    private fun checkPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(requireActivity().applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(requireActivity().applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            200
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 200)
            if (grantResults.isNotEmpty()) {
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Toast.makeText(requireContext(), "Permiso concedido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
            }
    }

    private fun crearPdf() {
        val ancho = 595
        val alto = 842

        val pdfDocument = PdfDocument()
        val page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(ancho, alto, 1).create())

        val canvas = page.canvas
        var fondo: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.modelo_factura)
        fondo = Bitmap.createScaledBitmap(fondo, ancho, alto, true)
        canvas.drawBitmap(fondo, 0f, 0f, null)

        val paint = Paint()
        var y: Float
        var x = 35f
        paint.textSize = 20f

        paint.color = android.graphics.Color.GRAY
        canvas.drawText("#${binding.campoNumeroFactura.text.toString()}", x, 80f, paint)

        paint.color = android.graphics.Color.BLACK
        canvas.drawText(binding.btnFechaEmision.text.toString(), x, 155f, paint)
        canvas.drawText(factura.metodoDePago.texto, x, 230f, paint)

        paint.textSize = 17f
        canvas.drawText(cliente!!.nombre, 226f, 155f, paint)
        canvas.drawText(cliente!!.nif, 226f, 185f, paint)
        canvas.drawText(cliente!!.domicilio, 226f, 215f, paint)
        canvas.drawText("${cliente!!.ciudad}, ${cliente!!.provincia}", 226f, 245f, paint)
        canvas.drawText(formatTelefono(cliente!!.telefono), 226f, 275f, paint)

        paint.textSize = 17f
        y = 342f
        for (i in 0..< lineas.size) {
            canvas.drawText(getNumberString(lineas[i].cantidadProducto), 50f, y, paint)
            canvas.drawText(productosSeleccionados[i].nombre, 120f, y, paint)
            canvas.drawText("${dosDecimales(productosSeleccionados[i].precioUnitario)} €", 420f, y, paint)
            val total = (productosSeleccionados[i].precioUnitario - (lineas[i].descuentoAplicado/100 * productosSeleccionados[i].precioUnitario)) * lineas[i].cantidadProducto
            canvas.drawText("${dosDecimales(total)} €", 505f, y, paint)
            y += 26.4f
        }

        y = 342f + 26.4f * 10f
        canvas.drawText("${dosDecimales(importeTotal)} €", 502f, y, paint)
        y += 26.4f
        canvas.drawText("${dosDecimales(importeIva)} €", 502f, y, paint)
        y += 26.4f
        canvas.drawText("${dosDecimales(importeTotalPagar)} €", 502f, y, paint)
        y += 26.4f

        pdfDocument.finishPage(page)
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Factura.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            showToast("PDF guardado en ${filePath.absolutePath}")
            Log.e("PDF guardado en ${filePath.absolutePath}", "Archivo")
            openPdf(filePath)
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Error al guardar el PDF: ${e.message}")
        }

        pdfDocument.close()
    }

    private fun formatTelefono(telefono: String): String {
        var numero = ""
        var cont = 0
        for (i in 0..telefono.replace(" ", "").length) {
            cont++
            if (cont == 3) {
                numero += telefono.substring(i - 2, i + 1) + " "
                cont = 0
            }

        }
        return numero
    }

    private fun getNumberString(number: Float): String {
        if (number == number.toInt().toFloat())
            return number.toInt().toString()
        else
            return number.toString()
    }

    fun dosDecimales(number: Float): String {
        return String.format("%.2f", number)
    }

    private fun openPdf(file: File) {
        val uri: Uri = FileProvider.getUriForFile(requireContext(), "${requireActivity().applicationContext.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            showToast("No se puede abrir el PDF: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}