package com.example.tampparit.ui.admin

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tampparit.R
import com.example.tampparit.databinding.ActivityRegisterBinding
import com.example.tampparit.helpers.Instances
import com.example.tampparit.models.DriverModel

class ActivityRegister:AppCompatActivity() {
    private lateinit var binding:ActivityRegisterBinding
    private var selectedVehicle = "Snow Mobile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init(){
        binding.typeOneButton.setBackgroundResource(R.drawable.jet_ski_selected)

        binding.buttonRegister.setOnClickListener {
            registerNewVehicle(binding.emailTf.text.toString(),binding.passwordTf.text.toString(),binding.usernameTf.text.toString())
        }
        binding.typeOneButton.setOnClickListener {
            selectedVehicle = "Snow Mobile"
            binding.typeTwoButton.setBackgroundResource(R.drawable.snow_groom)
            binding.typeOneButton.setBackgroundResource(R.drawable.jet_ski_selected)
            Toast.makeText(this, "Chosen Vehicle Is Snowmobile", Toast.LENGTH_SHORT).show()

        }
        binding.typeTwoButton.setOnClickListener {
            selectedVehicle = "Snow Groomer"
            binding.typeTwoButton.setBackgroundResource(R.drawable.snow_groom_selected)
            binding.typeOneButton.setBackgroundResource(R.drawable.jet_ski)
            Toast.makeText(this, "Chosen Vehicle Is Snow Groomer", Toast.LENGTH_SHORT).show()
        }
    }
    private fun registerNewVehicle(email:String,password:String,username:String){
        Instances.authInstance.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                val uid =  it.result.user?.uid
                Instances.databaseInstance.child("drivers").child(uid!!).setValue(DriverModel(username,email,uid,selectedVehicle,

                )).addOnSuccessListener {
                    Toast.makeText(this,"New Driver Registered Successfully",Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
             else {
                 Toast.makeText(this,"registration failed",Toast.LENGTH_SHORT).show()
             }
        }
    }
}