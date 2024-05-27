package com.deanil.proyecto.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(tableName = "facturas")
data class FacturaEntity(

    @PrimaryKey
    @ColumnInfo(name = "numero_factura")
    val numeroFactura: String,

    val estado: String,

    @ColumnInfo(name = "fecha_emision")
    val fechaEmision: String,

    @ColumnInfo(name = "fecha_vencimiento")
    val fechaVencimiento: String,

    @ColumnInfo(name = "metodo_de_pago")
    val metodoDePago: String,

    @ColumnInfo(name = "importe_total")
    val importeTotal: Float,

    @ColumnInfo(name = "importe_total_iva")
    val importeTotalIva: Float,

    @ColumnInfo(name = "importe_total_pagar")
    val importeTotalPagar: Float,

    @ColumnInfo(name = "cliente_id")
    val idCliente: Int,
) {
    enum class MetodosDePago(
        val texto: String
    ) {
        EFECTIVO("Efectivo"),
        TARJETA_BANCARIA("Tarjeta bancaria"),
        TRANSFERENCIA_BANCARIA("Transferencia bancaria"),
        DOMICILIACION_BANCARIA("Domiciliación bancaria"),
        PAGARE("Pagaré"),
        CONFIRMING("Confirming")
    }

    enum class Estados(
        val texto: String
    ) {
        NOPAGADA("No pagada"),
        PAGADA("Pagada"),
        VENCIDA("Vencida")
    }
}


