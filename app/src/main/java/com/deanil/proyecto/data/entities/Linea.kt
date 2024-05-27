package com.deanil.proyecto.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lineas")
data class Linea(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_linea")
    val idLinea: Int = 0,

    @ColumnInfo(name = "codigo_producto")
    var codigoProducto: Int,

    @ColumnInfo(name = "numero_factura")
    val numeroFactura: Int,

    @ColumnInfo(name = "cantidad_producto")
    val cantidadProducto: Float = 1f,

    @ColumnInfo(name = "descuento_aplicado")
    val descuentoAplicado: Float = 0f,
)
