package com.example.tampparit.ui.driver

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.tampparit.databinding.ActivityDriverDetailsLayoutBinding
import com.example.tampparit.helpers.Instances
import com.example.tampparit.models.AnnotationModel

class ActivityDriverDetails:AppCompatActivity() {
    private lateinit var binding: ActivityDriverDetailsLayoutBinding
    var id:String = ""
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityDriverDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun init(){
        val bundle = intent.extras!!.getBundle("constant")
        bundle.let {
            val mode = it!!.get("cons") as AnnotationModel
            id = mode.uid!!
            binding.descTv.setText(mode.comment!!)
        }

        binding.backButton.setOnClickListener {
          Instances.databaseInstance.child("annotations").child(id).child("comment").setValue(
              binding.descTv.text.toString()
          ).addOnSuccessListener {
             val intent = Intent(this@ActivityDriverDetails,ActivityDriver::class.java)
              startActivity(intent)
          }
        }
        binding.deleteButton.setOnClickListener {
            Instances.databaseInstance.child("annotations").child(id).removeValue().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this@ActivityDriverDetails,"Annotation Deleted Successfully",Toast.LENGTH_SHORT).show()
                    onBackPressed()

                } else {
                    Toast.makeText(this@ActivityDriverDetails,"Failed To Delete Annotation and Comment",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}