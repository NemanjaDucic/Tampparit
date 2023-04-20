package com.example.tampparit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tampparit.R
import com.example.tampparit.interfaces.RemoveAdminInterface
import com.example.tampparit.models.AdminModel

class AdminListAdapter (
    private var adminNamesArray: ArrayList<AdminModel>,
    private val listener:RemoveAdminInterface
) : RecyclerView.Adapter<AdminListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.textView.text = adminNamesArray[position].name.toString()
        holder.button.setOnClickListener {
            listener.selectedAdmin(adminNamesArray[position].id.toString())
        }
        holder.buttonEdt.setOnClickListener {
            listener.detailsAdmin(adminNamesArray[position].id.toString())
        }
    }

    override fun getItemCount(): Int {
        return adminNamesArray.count()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val button: Button = itemView.findViewById(R.id.button_remove)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val buttonEdt:Button = itemView.findViewById(R.id.button_detail)
    }

    fun setAdminList(users: ArrayList<AdminModel>) {
        adminNamesArray = users
        notifyDataSetChanged()
    }
}