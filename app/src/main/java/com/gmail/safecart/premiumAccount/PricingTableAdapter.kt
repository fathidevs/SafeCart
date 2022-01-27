package com.gmail.safecart.premiumAccount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.R

class PricingTableAdapter(private val priceInfo: PricingInfo) :
    RecyclerView.Adapter<PricingTableAdapter.ViewHolder>() {

    private lateinit var cardSelector: ArrayList<Boolean>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.active_pricing_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val price = priceInfo.pricePerMonth()[position]
        val duration = priceInfo.subDurations()[position]
        val total = price * duration
        cardSelector = ArrayList(priceInfo.itemCount())

        holder.monthlyCostTv.text = price.toString()
        holder.totalCostTv.text = total.toString()
        holder.priceCard.setOnClickListener {}
    }

    override fun getItemCount(): Int {
        return priceInfo.itemCount()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var priceCard: CardView = itemView.findViewById(R.id.pricingCard)
        var totalCostTv: TextView = itemView.findViewById(R.id.subTotalTv)
        var monthlyCostTv: TextView = itemView.findViewById(R.id.subCostperMonthTv)
    }
}