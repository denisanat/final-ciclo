package com.deanil.proyecto.ui.facturas.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deanil.proyecto.R
import com.deanil.proyecto.data.adapters.LineasAdapter
import com.deanil.proyecto.data.db.AppDatabase
import com.deanil.proyecto.data.db.DataApplication
import com.deanil.proyecto.data.entities.AlbaranEntity
import com.deanil.proyecto.data.entities.ClienteEntity
import com.deanil.proyecto.data.entities.DatosEmpresa
import com.deanil.proyecto.data.entities.FacturaEntity
import com.deanil.proyecto.data.entities.Linea
import com.deanil.proyecto.data.entities.ProductoEntity
import com.deanil.proyecto.databinding.FragmentFacturaDetalladaBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.concurrent.LinkedBlockingQueue

class FacturaDetalladaFragment(
    val factura: FacturaEntity
) : Fragment() {

    private lateinit var binding: FragmentFacturaDetalladaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacturaDetalladaBinding.inflate(inflater, container, false)

        setupAppbar()

        binding.nombreFactura.text = factura.numeroFactura
        binding.pdfFactura.setOnClickListener {
            val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "${factura.numeroFactura}.pdf")
            abrirPdf(filePath)
        }
        binding.facturaNombreCliente.text = getCliente().nombre
        binding.facturaNombreEmpresa.text = getEmpresa().nombre
        binding.facturaFechaEmision.text = factura.fechaEmision
        binding.facturaMetodo.text = factura.metodoDePago

        setupLista()

        return binding.root
    }

    private fun setupLista() {
        val lineas = getLineas()
        val productos = getProductos(lineas)
        val lineasAdapter = LineasAdapter(lineas)

        val recycler = binding.listaProductos

        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = lineasAdapter
        }
    }

    private fun abrirPdf(file: File) {
        val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file.name)
        if (filePath.exists()) {
            val uri = FileProvider.getUriForFile(requireContext(), "${requireActivity().applicationContext.packageName}.provider", filePath)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

            val chooser = Intent.createChooser(intent, "Abrir con")
            try {
                startActivity(chooser)
            } catch (e: ActivityNotFoundException) {
                showToast("No hay aplicaciones disponibles para abrir PDF")
            }
        } else {
            showToast("El archivo no existe")
        }
    }

    private fun setupAppbar() {
        val appbar = requireActivity().findViewById<BottomAppBar>(R.id.appbar)
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
        appbar.menu.getItem(0).setVisible(true)
        appbar.menu.getItem(1).setVisible(true)
        requireActivity().findViewById<FloatingActionButton>(R.id.btnAdd)
            .visibility = View.GONE
        appbar.menu.add("pagar").apply {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_albaran_24dp)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                val calendario = Calendar.getInstance()
                val fechaHoy = "${dosNumeros(calendario.get(Calendar.DAY_OF_MONTH))}/${dosNumeros(calendario.get(Calendar.MONTH) + 1)}/${calendario.get(Calendar.YEAR)}"
                Thread {
                    DataApplication.database.facturaDao().pagarFactura(factura.numeroFactura)
                    DataApplication.database.albaranDao().insertAlbaran(
                        AlbaranEntity(
                            numeroAlbaran = factura.numeroFactura + "X",
                            numeroFactura = factura.numeroFactura,
                            fechaEmision = fechaHoy
                        )
                    )
                }.start()
                crearPdf()
                true
            }
        }
        appbar.menu.add("eliminar").apply {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_cancel_24dp)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                Thread {
                    DataApplication.database.facturaDao().deleteFactura(factura)
                }.start()
                requireActivity().supportFragmentManager.popBackStack()
                true
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun dosNumeros(number: Int): String {
        return String.format("%02d", number)
    }

    private fun crearPdf() {
        val ancho = 595
        val alto = 842

        val lineas = getLineas()
        val cliente = getCliente()
        val empresa = getEmpresa()
        val productos = getProductos(lineas)

        val pdfDocument = PdfDocument()
        val page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(ancho, alto, 1).create())

        val canvas = page.canvas
        var fondo: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.modelo_albaran)
        fondo = Bitmap.createScaledBitmap(fondo, ancho, alto, true)
        canvas.drawBitmap(fondo, 0f, 0f, null)

        val paint = Paint()
        val x = 35f
        paint.textSize = 20f

        paint.color = android.graphics.Color.GRAY
        canvas.drawText("#${factura.numeroFactura + "X"}", x, 80f, paint)

        paint.color = android.graphics.Color.BLACK
        canvas.drawText(factura.fechaEmision, x, 155f, paint)
        canvas.drawText(factura.metodoDePago, x, 230f, paint)

        paint.textSize = 17f
        canvas.drawText(cliente.nombre, 226f, 155f, paint)
        canvas.drawText(cliente.nif.uppercase(), 226f, 185f, paint)
        canvas.drawText(cliente.domicilio, 226f, 215f, paint)
        canvas.drawText("${cliente.ciudad}, ${cliente.provincia}", 226f, 245f, paint)
        canvas.drawText(formatTelefono(cliente.telefono), 226f, 275f, paint)

        canvas.drawText(empresa.nombre, 420f, 155f, paint)
        canvas.drawText(empresa.nif.uppercase(), 420f, 185f, paint)
        canvas.drawText(empresa.domicilio, 420f, 215f, paint)
        canvas.drawText("${empresa.ciudad}, ${empresa.provincia}", 420f, 245f, paint)
        canvas.drawText(formatTelefono(empresa.telefono), 420f, 275f, paint)

        paint.textSize = 17f
        var y = 342f
        for (i in 0..< lineas.size) {
            canvas.drawText(getNumberString(lineas[i].cantidadProducto), 50f, y, paint)
            canvas.drawText(productos[i].nombre, 120f, y, paint)
            canvas.drawText("${dosDecimales(productos[i].precioUnitario)} €", 420f, y, paint)
            val total = (productos[i].precioUnitario - (lineas[i].descuentoAplicado/100 * productos[i].precioUnitario)) * lineas[i].cantidadProducto
            canvas.drawText("${dosDecimales(total)} €", 505f, y, paint)
            y += 26.4f
        }

        y = 342f + 26.4f * 10f
        canvas.drawText("${dosDecimales(factura.importeTotal)} €", 502f, y, paint)
        y += 26.4f
        canvas.drawText("${dosDecimales(factura.importeTotalIva)} €", 502f, y, paint)
        y += 26.4f
        canvas.drawText("${dosDecimales(factura.importeTotalPagar)} €", 502f, y, paint)
        y += 26.4f

        pdfDocument.finishPage(page)
        val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "${factura.numeroFactura}X.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            showToast("PDF guardado en ${filePath.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("Error al guardar el PDF: ${e.message}")
        }

        pdfDocument.close()
    }

    private fun getEmpresa(): DatosEmpresa {
        val sharedPreferences = requireActivity().getSharedPreferences("Empresa", Context.MODE_PRIVATE)
        return DatosEmpresa(
            nombre = sharedPreferences.getString("nombre", "").toString(),
            nif = sharedPreferences.getString("nif", "").toString(),
            domicilio = sharedPreferences.getString("domicilio", "").toString(),
            ciudad = sharedPreferences.getString("ciudad", "").toString(),
            provincia = sharedPreferences.getString("provincia", "").toString(),
            email = sharedPreferences.getString("correo", "").toString(),
            telefono = sharedPreferences.getString("telefono", "").toString()
        )
    }

    private fun getLineas(): MutableList<Linea> {
        val queue = LinkedBlockingQueue<MutableList<Linea>>()
        Thread {
            val lineas = DataApplication.database.lineaDao().getLineasByFactura(factura.numeroFactura)
            queue.add(lineas)
        }.start()
        return queue.take()
    }

    private fun getCliente(): ClienteEntity {
        val queue = LinkedBlockingQueue<ClienteEntity>()
        Thread {
            val cliente = DataApplication.database.clienteDao().getClienteById(factura.idCliente)
            queue.add(cliente)
        }.start()
        return queue.take()
    }

    private fun getProductos(lineas: MutableList<Linea>): MutableList<ProductoEntity> {
        val productos: MutableList<ProductoEntity> = mutableListOf()
        for (linea in lineas) {
            val queue = LinkedBlockingQueue<ProductoEntity>()
            Thread {
                val producto = DataApplication.database.productoDao().getProductoById(linea.codigoProducto)
                queue.add(producto)
            }.start()
            productos.add(queue.take())
        }
        return productos
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

}