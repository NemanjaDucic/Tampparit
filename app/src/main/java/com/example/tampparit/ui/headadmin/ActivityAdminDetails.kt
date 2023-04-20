package com.example.tampparit.ui.headadmin

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tampparit.adapters.AsignedDriversAdapter
import com.example.tampparit.databinding.ActivityAdminDetailsLayoutBinding
import com.example.tampparit.models.AdminModel
import com.example.tampparit.models.DriverModel
import com.example.tampparit.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityAdminDetails:AppCompatActivity() {
    private var user = AdminModel()

    private  lateinit var viewModel: MainViewModel
    private lateinit var binding :ActivityAdminDetailsLayoutBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter :AsignedDriversAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init(){
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getAdminByID(intent.getStringExtra("id")!!)
        recyclerView = binding.RV
        adapter = AsignedDriversAdapter(arrayListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.getAllDrivers()

        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            user = viewModel.user
            binding.nameTV.text = "Name: " + user.name
            binding.areaTV.text = "Area: " + user.areaName
            binding.cityTV.text = "City: " + user.cityName
            viewModel.diverListLiveData.observe(this@ActivityAdminDetails){
                    value ->
                adapter.setAdminDList(value.filter { it.asignedBy == user.id } as ArrayList<DriverModel>)
            }
        }



    }
}