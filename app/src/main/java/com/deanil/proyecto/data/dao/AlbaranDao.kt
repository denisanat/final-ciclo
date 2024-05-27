package com.deanil.proyecto.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.deanil.proyecto.data.entities.AlbaranEntity

@Dao
interface AlbaranDao {

    @Query("SELECT * FROM albaranes")
    fun getAllAlbaranes(): List<AlbaranEntity>

    @Query("SELECT * FROM albaranes WHERE numero_albaran = :id")
    fun getAlbaran(id: Int): AlbaranEntity

    @Insert
    fun insertAllAlbaranes(albaranes: List<AlbaranEntity>)

    @Upsert
    fun insertAlbaran(albaran: AlbaranEntity)

    @Update
    fun updateAlbaran(albaran: AlbaranEntity)

    @Delete
    fun deleteAlbaran(albaran: AlbaranEntity)
}