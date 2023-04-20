package com.example.tampparit.ui

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.tampparit.databinding.ActivityDetailsLayoutBinding
import com.example.tampparit.models.AnnotationModel

class ActivityDetails:AppCompatActivity() {
    private lateinit var binding:ActivityDetailsLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun init(){
        val bundle = intent.extras!!.getBundle("constant")
        bundle.let {
            val mode = it!!.get("cons") as AnnotationModel

        binding.descTv.text =  mode.comment
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }
}