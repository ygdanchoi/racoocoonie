package com.example.racoocoonie

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.racoocoonie.databinding.ActivityMainBinding


class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, MainService::class.java)
        applicationContext.startForegroundService(intent)
        binding.text.text = "Racoocoonie service started"
        binding.stopButton.visibility = View.VISIBLE

        binding.stopButton.setOnClickListener {
            binding.text.text = "Racoocoonie service stopped"
            applicationContext.stopService(intent)
            it.visibility = View.GONE
        }
    }
}