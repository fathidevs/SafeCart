package com.gmail.safecart.loyaltyCard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.Jumper
import com.gmail.safecart.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyCardsAdapter(private val context: Context, private val items: ArrayList<CardsModel>) :
    RecyclerView.Adapter<MyCardsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.loyalty_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.cardName.text = item.cardName
        holder.myCardV.setCardBackgroundColor(
            context.resources.getColor(
                getCardColor(item.cardColor),
                null
            )
        )
        holder.myCardV.setOnClickListener {

            Jumper(context).jumpWith5Extras(
                AppsConstants.loyaltyCardIdKey,
                item.id,

                AppsConstants.cardCodeKey,
                item.cardCode,

                AppsConstants.cardNameKey,
                item.cardName,

                AppsConstants.codeFormatKey,
                item.cardCodeFormat,

                AppsConstants.colorPositionKey,
                item.cardColor,

                LoyaltyCardViewActivity()
            )
        }
        holder.editCardBtn.setOnClickListener {

            Jumper(context).jumpWith6Extras(
                AppsConstants.loyaltyCardIdKey,
                item.id,

                AppsConstants.cardCodeKey,
                item.cardCode,

                AppsConstants.cardNameKey,
                item.cardName,

                AppsConstants.codeFormatKey,
                item.cardCodeFormat,

                AppsConstants.colorPositionKey,
                item.cardColor,

                AppsConstants.isEditMode,
                true,
                CardScannerActivity()
            )
        }
    }

    private fun getCardColor(position: Int): Int {
        return LoyaltyCardColorPreset.colors[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun insertedCardRefresh(model: CardsModel) {
        items.add(model)
        notifyItemInserted(itemCount)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardName: TextView = view.findViewById(R.id.cardName)
        val myCardV: CardView = view.findViewById(R.id.myCardV)
        val editCardBtn: FloatingActionButton = view.findViewById(R.id.editCardBtn)
    }
}
