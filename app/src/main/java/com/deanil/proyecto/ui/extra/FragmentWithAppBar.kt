package com.deanil.proyecto.ui.extra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.deanil.proyecto.R
import com.google.android.material.bottomappbar.BottomAppBar

abstract class FragmentWithAppBar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clearItems()
        setAppBarButtons(requireActivity().findViewById(R.id.appbar))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun setAppBarButtons(appbar: BottomAppBar)

    private fun clearItems() {
        val appbar = requireActivity().findViewById<BottomAppBar>(R.id.appbar)
        for (i in 0..<appbar.menu.size())
            appbar.menu.getItem(i).setVisible(false)
    }
}