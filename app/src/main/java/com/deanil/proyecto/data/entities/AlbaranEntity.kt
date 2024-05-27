package com.deanil.proyecto.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albaranes")
data class AlbaranEntity(

    @PrimaryKey()
    @ColumnInfo(name = "numero_albaran")
    val numeroAlbaran: String,

    @ColumnInfo(name = "fecha_emision")
    val fechaEmision: String,

    @ColumnInfo(name = "numero_factura")
    val numeroFactura: String
)
