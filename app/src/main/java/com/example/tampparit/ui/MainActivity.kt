package com.example.tampparit.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tampparit.databinding.ActivityMainBinding
import com.example.tampparit.ui.admin.AdminActivity
import com.example.tampparit.ui.headadmin.ActivityCreateAdmin
import com.example.tampparit.ui.headadmin.ActivityHeadAdmin

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init(){
        binding.buttonVisitor.setOnClickListener {
            val intent = Intent(this, ActivityMap::class.java)
            startActivity(intent)
        }
        binding.buttonAdmin.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }
        binding.buttonDriver.setOnClickListener {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
        }
        binding.welcomeTV.setOnClickListener {
            val intent = Intent(this, ActivityHeadAdmin::class.java)
            startActivity(intent)
        }
    }
}