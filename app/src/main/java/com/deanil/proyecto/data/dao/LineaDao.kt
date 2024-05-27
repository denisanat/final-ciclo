package com.deanil.proyecto.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.deanil.proyecto.data.entities.Linea

@Dao
interface LineaDao {

    @Insert
    fun insertLineas(lineas: MutableList<Linea>)

    @Query("SELECT * FROM lineas WHERE numero_factura = :numero_factura")
    fun getLineasByFactura(numero_factura: String): MutableList<Linea>
}