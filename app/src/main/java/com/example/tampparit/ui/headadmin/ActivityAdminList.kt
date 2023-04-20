package com.example.tampparit.ui.headadmin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tampparit.adapters.AdminListAdapter
import com.example.tampparit.databinding.ActivityDeleteAdminLayoutBinding
import com.example.tampparit.interfaces.RemoveAdminInterface
import com.example.tampparit.viewmodel.MainViewModel

class ActivityAdminList:AppCompatActivity(),RemoveAdminInterface {
    private lateinit var binding :ActivityDeleteAdminLayoutBinding
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter:AdminListAdapter
    private lateinit var viewModel : MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAdminLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init(){
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        recyclerView = binding.adminListRv
        adapter = AdminListAdapter(arrayListOf(),this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.getAdmins()
        viewModel.adminListLiveData.observe(this){
            value ->
            adapter.setAdminList(value)
        }
    }

    override fun selectedAdmin(admin: String) {
       viewModel.deleteAdmin(admin)
    }

    override fun detailsAdmin(admin: String) {
        val intent = Intent(this,ActivityAdminDetails::class.java)
        intent.putExtra("id",admin)
        startActivity(intent)
    }
}