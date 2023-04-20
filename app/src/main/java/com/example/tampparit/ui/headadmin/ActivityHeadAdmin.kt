package com.example.tampparit.ui.headadmin

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tampparit.databinding.ActivityHeadAdminLayoutBinding

class ActivityHeadAdmin:AppCompatActivity() {
    private lateinit var binding :ActivityHeadAdminLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeadAdminLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init(){
        binding.buttonCreateAdmin.setOnClickListener {
            val intent = Intent(this,ActivityCreateAdmin::class.java)
            startActivity(intent)
        }
        binding.buttonDelete.setOnClickListener {
            val intent = Intent(this,ActivityAdminList::class.java)
            startActivity(intent)
        }
    }
}