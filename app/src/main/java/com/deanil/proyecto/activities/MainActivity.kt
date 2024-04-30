package com.deanil.proyecto.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.deanil.proyecto.R
import com.deanil.proyecto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClientes.setOnClickListener { ira(ClientesActivity::class.java) }

    }

    fun ira(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}