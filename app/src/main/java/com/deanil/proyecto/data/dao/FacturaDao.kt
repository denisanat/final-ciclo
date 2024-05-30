package com.deanil.proyecto.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.deanil.proyecto.data.entities.FacturaEntity
import com.deanil.proyecto.data.entities.Linea

@Dao
interface FacturaDao {

    @Query("SELECT * FROM facturas")
    fun getAllFacturas(): MutableList<FacturaEntity>

    @Insert
    fun insertFactura(factura: FacturaEntity)

    @Query("SELECT * FROM facturas WHERE cliente_id = :cliente")
    fun getFacturasByCliente(cliente: Int): List<FacturaEntity>

    @Delete
    fun deleteFactura(factura: FacturaEntity)

    @Query("UPDATE facturas SET estado = 'Pagada' WHERE numero_factura = :numero")
    fun pagarFactura(numero: String)

    @Query("SELECT * FROM facturas WHERE estado = :estado")
    fun getFacturasByEstado(estado: String): MutableList<FacturaEntity>
}