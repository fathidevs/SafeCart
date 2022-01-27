package com.gmail.safecart.blacklist

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.R

class ManufacturerListAdapter(
    private val context: Context,
    private val items: ArrayList<BlacklistModel>
):RecyclerView.Adapter<ManufacturerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.blacklised_manufacturer_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.manufactNameTv.text = context.resources.getString(R.string.manufactName, item.name)
        holder.manufactCodeTv.text = context.resources.getString(R.string.manufactCode, item.prefix)
        if (position%2 == 0){
            holder.manufactInfoItem.background = ColorDrawable(Color.WHITE)
        }else{
            holder.manufactInfoItem.background = ColorDrawable(context.getColor(R.color.darkWhite))
        }
    }
    override fun getItemCount(): Int = items.size
    fun insertedMnaufactRefresh(model: BlacklistModel){
        items.add(model)
        notifyItemInserted(itemCount)
    }
    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val manufactNameTv: TextView = v.findViewById(R.id.manufactNameTv)
        val manufactCodeTv: TextView = v.findViewById(R.id.manufactCodeTv)
        val manufactInfoItem: LinearLayout = v.findViewById(R.id.manufactInfoItem)
    }
}