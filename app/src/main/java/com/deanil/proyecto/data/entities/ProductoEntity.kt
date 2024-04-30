package com.deanil.proyecto.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(

    @PrimaryKey
    @ColumnInfo(name = "codigo")
    val codigo: Int = 0,

    val nombre: String,

    @ColumnInfo(name = "precio_unitario")
    val precioUnitario: Float,

    val iva: Float
)
