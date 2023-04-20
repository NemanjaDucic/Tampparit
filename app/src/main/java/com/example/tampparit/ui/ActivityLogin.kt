package com.example.tampparit.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tampparit.databinding.ActivityLoginBinding
import com.example.tampparit.helpers.Instances
import com.example.tampparit.ui.driver.ActivityDriver

class ActivityLogin:AppCompatActivity() {
    private lateinit var  binding :ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init(){
        binding.buttonLogin.setOnClickListener {
            login(binding.usernameTf.text.toString(),binding.passTf.text.toString())
        }
    }
    private fun login(email:String,password:String){
        Instances.authInstance.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(this@ActivityLogin, ActivityDriver::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@ActivityLogin, "Wrong Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}