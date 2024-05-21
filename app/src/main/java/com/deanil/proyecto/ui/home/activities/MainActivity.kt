package com.deanil.proyecto.ui.home.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deanil.proyecto.R
import com.deanil.proyecto.data.db.AppDatabase
import com.deanil.proyecto.databinding.ActivityMainBinding
import com.deanil.proyecto.ui.clientes.fragments.ClientesFragment
import com.deanil.proyecto.ui.home.fragments.HomeFragment
import com.google.android.material.bottomappbar.BottomAppBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var appbar: BottomAppBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "AppDatabase")
            .fallbackToDestructiveMigration()
            .build()

        appbarFunction()

        val fragment = HomeFragment()
        val contenedor = binding.fragmentContainer

        supportFragmentManager.beginTransaction()
            .replace(contenedor.id, fragment)
            .commit()
    }

    fun appbarFunction() {
        appbar = binding.appBar.appbar
        appbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_back -> {
                    supportFragmentManager.popBackStack()
                    true
                }

                R.id.action_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, HomeFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}