package com.deanil.proyecto.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.deanil.proyecto.data.entities.ProductoEntity

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos")
    fun getAllProductos(): MutableList<ProductoEntity>

    @Query("SELECT * FROM productos WHERE codigo = :codigo")
    fun getProductoById(codigo: Int): ProductoEntity

    @Query("SELECT * FROM productos WHERE nombre = :nombre LIMIT 1")
    fun getProductoByNombre(nombre: String): ProductoEntity

    @Insert
    fun insertAll(productos: List<ProductoEntity>)

    @Insert
    fun insertProducto(producto: ProductoEntity)

    @Update
    fun updateProducto(producto: ProductoEntity)

    @Delete
    fun deleteProducto(producto: ProductoEntity)
}