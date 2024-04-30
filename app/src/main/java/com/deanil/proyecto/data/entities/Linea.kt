package com.deanil.proyecto.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "lineas",
    primaryKeys = ["codigo_producto", "numero_factura"]
)
data class Linea(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_linea")
    val idLinea: Int,

    @ColumnInfo(name = "codigo_producto")
    val codigoProducto: Int,

    @ColumnInfo(name = "numero_factura")
    val numeroFactura: Int,

    @ColumnInfo(name = "cantidad_producto")
    val cantidadProducto: Float,

    @ColumnInfo(name = "descuento_aplicado")
    val descuentoAplicado: Float,
)
