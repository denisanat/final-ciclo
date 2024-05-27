package com.deanil.proyecto.ui.albaranes.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deanil.proyecto.R
import com.deanil.proyecto.data.adapters.AlbaranesAdapter
import com.deanil.proyecto.data.adapters.ProductosAdapter
import com.deanil.proyecto.data.db.DataApplication
import com.deanil.proyecto.data.entities.AlbaranEntity
import com.deanil.proyecto.data.interfaces.onAlbaranClickListener
import com.deanil.proyecto.databinding.FragmentAlbaranesBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

class AlbaranesFragment : Fragment(), onAlbaranClickListener {

    private lateinit var binding: FragmentAlbaranesBinding

    private lateinit var albaranesAdapter: AlbaranesAdapter
    private var albaranes = listOf<AlbaranEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbaranesBinding.inflate(inflater, container, false)

        setupRecycler()
        getAlbaranes()
        setupAppbar()

        return binding.root
    }

    override fun onClick(albaran: AlbaranEntity) {
        val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "${albaran.numeroAlbaran}.pdf")
        leerPdf(filePath)
    }

    private fun setupAppbar() {
        val appbar = requireActivity().findViewById<BottomAppBar>(R.id.appbar)
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
        appbar.menu.getItem(0).setVisible(true)
        appbar.menu.getItem(1).setVisible(true)
    }

    private fun getAlbaranes() {
        val queue = LinkedBlockingQueue<List<AlbaranEntity>>()
        Thread {
            val albaranes = DataApplication.database.albaranDao().getAllAlbaranes()
            queue.add(albaranes)
        }.start()
        albaranesAdapter.updateList(queue.take())
    }

    private fun setupRecycler() {
        val recycler = binding.recylerAlbaranes
        albaranesAdapter = AlbaranesAdapter(albaranes, this)

        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = albaranesAdapter
        }
    }

    private fun leerPdf(file: File) {
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}