package com.deanil.proyecto.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.deanil.proyecto.data.entities.Linea

@Dao
interface LineaDao {

    @Insert
    fun insertLinea(linea: Linea)
}