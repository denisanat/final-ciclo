package com.deanil.proyecto.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deanil.proyecto.data.entities.ClienteEntity

@Dao
interface ClienteDao {

    @Query("SELECT * FROM clientes")
    fun getAllClientes(): List<ClienteEntity>

    @Query("SELECT * FROM clientes WHERE id_cliente = :id")
    fun getClienteById(id: Int): ClienteEntity

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(clientes: List<ClienteEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertCliente(cliente: ClienteEntity)

    @Update
    fun updateCliente(cliente: ClienteEntity)

    @Delete
    fun deleteCliente(cliente: ClienteEntity)

}