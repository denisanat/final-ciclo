package com.deanil.proyecto.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class ClienteEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_cliente")
    val idCliente: Int = 0,

    val nombre: String,

    val nif: String,

    val domicilio: String,

    val ciudad: String,

    val provincia: String,

    val telefono: String,

    val email: String
)
