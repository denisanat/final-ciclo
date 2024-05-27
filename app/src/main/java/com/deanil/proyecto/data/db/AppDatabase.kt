package com.deanil.proyecto.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.deanil.proyecto.data.dao.AlbaranDao
import com.deanil.proyecto.data.dao.ClienteDao
import com.deanil.proyecto.data.dao.FacturaDao
import com.deanil.proyecto.data.dao.LineaDao
import com.deanil.proyecto.data.dao.ProductoDao
import com.deanil.proyecto.data.entities.AlbaranEntity
import com.deanil.proyecto.data.entities.ClienteEntity
import com.deanil.proyecto.data.entities.FacturaEntity
import com.deanil.proyecto.data.entities.Linea
import com.deanil.proyecto.data.entities.ProductoEntity

@Database(
    entities = [
        ClienteEntity::class,
        ProductoEntity::class,
        FacturaEntity::class,
        AlbaranEntity::class,
        Linea::class
               ],
    version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun productoDao(): ProductoDao
    abstract fun facturaDao(): FacturaDao
    abstract fun albaranDao(): AlbaranDao
    abstract fun lineaDao(): LineaDao
}