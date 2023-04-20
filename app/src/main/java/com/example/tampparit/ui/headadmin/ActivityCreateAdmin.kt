package com.example.tampparit.ui.headadmin

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tampparit.databinding.CreateAdminActivityLayoutBinding
import com.example.tampparit.helpers.Instances
import com.example.tampparit.helpers.RegionHelper
import com.example.tampparit.models.AdminModel
import com.example.tampparit.models.CityModel
import com.google.gson.Gson

class ActivityCreateAdmin:AppCompatActivity() {
    private  lateinit var binding:CreateAdminActivityLayoutBinding
    private var array = ArrayList<CityModel>()
    val namesArray = ArrayList<String>()
    private var code = CityModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateAdminActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()


    }
    private fun init(){
        val assetManager = applicationContext.assets
        val inputStream = assetManager.open("cities.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        val myObject = gson.fromJson(jsonString, Array<CityModel>::class.java)
        for ( i in myObject){
            array.add(i)
        }
        val regionSpinner = binding.regionSpinner
        val citySpinner = binding.citySpinner

        regionSpinner.adapter =
        ArrayAdapter(this, R.layout.simple_list_item_1, RegionHelper.regionList)
        citySpinner.adapter = ArrayAdapter(this,R.layout.simple_list_item_1, array.filter { it.admin_name == regionSpinner.selectedItem.toString() })

        regionSpinner.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filter = array.filter { it.admin_name == regionSpinner.selectedItem.toString() }
                namesArray.clear()
                for (i in filter){
                    namesArray.add(i.city!!)
                }
                citySpinner.adapter = ArrayAdapter(this@ActivityCreateAdmin,R.layout.simple_list_item_1,namesArray )

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("err")
            }
        }
        citySpinner.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val found = array.filter { it.city == citySpinner.selectedItem }
              code = found[0]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("err")
            }
        }
        binding.buttonCreate.setOnClickListener {
        if (binding.nameET.text != null || binding.nameET.text.toString() != "" || binding.emailET.text != null || binding.emailET.text.toString() != "" || binding.passwordET.text != null || binding.passwordET.text.toString() != "" ){
            Instances.authInstance.createUserWithEmailAndPassword(binding.emailET.text.toString(),binding.passwordET.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    val newAdmin = AdminModel(binding.nameET.text.toString(),code.city,code.admin_name,code.lng,code.lat,it.result.user?.uid)
                    Instances.databaseInstance.child("admins").child(it.result.user!!.uid).setValue(newAdmin).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this@ActivityCreateAdmin,"Admin Creation Complete",Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this@ActivityCreateAdmin,"Admin Creation Failed",Toast.LENGTH_SHORT).show()

                        }
                    }

                } else {
                    Toast.makeText(this@ActivityCreateAdmin,"Admin Creation Failed",Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this@ActivityCreateAdmin,"Please Fill In All The Fields ",Toast.LENGTH_SHORT).show()

        }
        }
    }
}