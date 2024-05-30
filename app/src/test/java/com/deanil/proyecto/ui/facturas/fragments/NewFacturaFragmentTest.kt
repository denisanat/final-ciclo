package com.deanil.proyecto.ui.facturas.fragments

import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.mockito.MockitoAnnotations

class NewFacturaFragmentTest {

    private val newFacturaFragment = NewFacturaFragment()

    @Test
    fun soloPasandoleUnNumeroALaFuncionDosNumeros() {
        val numero = 1
        val respuestaEsperada = "01"

        val respuestaReal = newFacturaFragment.dosNumeros(numero)

        assertEquals(respuestaEsperada, respuestaReal)
    }

    @Test
    fun pasandoleMasDeDosDigitos() {
        val numero = 1234
        val respuestaEsperada = "1234"

        val respuestaReal = newFacturaFragment.dosNumeros(numero)

        assertEquals(respuestaEsperada, respuestaReal)
    }

    @Test
    fun pasandoNumeroNegativo() {
        val numero = -3
        val respuestaEsperada = "-3"

        val respuestaReal = newFacturaFragment.dosNumeros(numero)

        assertEquals(respuestaEsperada, respuestaReal)
    }

}