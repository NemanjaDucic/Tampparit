package com.example.tampparit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.tampparit.R
import com.example.tampparit.interfaces.RemoveAdminInterface
import com.example.tampparit.models.AdminModel
import com.example.tampparit.models.DriverModel

class AsignedDriversAdapter  (
    private var drivers: ArrayList<DriverModel>,
) : RecyclerView.Adapter<AsignedDriversAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.textView.text = drivers[position].username
        holder.button.isVisible = false
        holder.buttonEdt.isVisible = false


    }

    override fun getItemCount(): Int {
        return drivers.count()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val button: Button = itemView.findViewById(R.id.button_remove)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val buttonEdt: Button = itemView.findViewById(R.id.button_detail)
    }

    fun setAdminDList(users: ArrayList<DriverModel>) {
        drivers = users
        notifyDataSetChanged()
    }
}