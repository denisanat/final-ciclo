package com.deanil.proyecto.ui.home.fragments

import android.content.Context
import android.content.SharedPreferences
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import com.deanil.proyecto.R
import com.deanil.proyecto.databinding.FragmentEmpresaBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class EmpresaFragmentTest {

    private lateinit var scenario: FragmentScenario<EmpresaFragment>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Create a scenario for EmpresaFragment
        scenario = FragmentScenario.launchInContainer(EmpresaFragment::class.java)
    }

}
