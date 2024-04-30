package com.deanil.proyecto.data

import android.app.Application
import androidx.room.Room
import com.deanil.proyecto.data.db.AppDatabase

class DataApplication : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "AppDatabase").build()
    }
}