package com.gmail.safecart.loyaltyCard

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.R
import com.google.android.material.card.MaterialCardView

class CardColorAdapter(private val context: Context) :
    RecyclerView.Adapter<CardColorAdapter.ViewHolder>() {

    var selectedItemP = -1
    private var lastItemSelectedP = -1
    private val colors = LoyaltyCardColorPreset.colors

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.color_selector_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.colorIv.setImageDrawable(
            ColorDrawable(
                context.resources.getColor(
                    colors[position],
                    null
                )
            )
        )
        holder.colorCard.setOnClickListener {
            itemSelector(position)
        }
        if (position == selectedItemP) {
            holder.selectedCard()
        } else {
            holder.unselectedCard()
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    private fun itemSelector(position: Int) {
        selectedItemP = position

        lastItemSelectedP = if (lastItemSelectedP == -1) {
            selectedItemP
        } else {
            notifyItemChanged(lastItemSelectedP)
            selectedItemP
        }
        notifyItemChanged(selectedItemP)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var colorIv: ImageView = view.findViewById(R.id.colorIv)
        var colorCard: MaterialCardView = view.findViewById(R.id.colorCard)
        private var strokeCard: MaterialCardView = view.findViewById(R.id.strokeCard)

        fun selectedCard() {
            strokeCard.visibility = View.VISIBLE
        }

        fun unselectedCard() {
            strokeCard.visibility = View.INVISIBLE
        }
    }
}