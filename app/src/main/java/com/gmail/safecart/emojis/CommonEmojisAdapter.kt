package com.gmail.safecart.emojis

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.R
import com.google.android.material.card.MaterialCardView

class CommonEmojisAdapter(
    private val context: Context,
    private val listOfCommonEmojis: ArrayList<EmojiModel>
) :
    RecyclerView.Adapter<CommonEmojisAdapter.ViewHolder>() {

    private var selectedItemPosition = -1
    private var lastSelectedItemPosition = -1
    private val lastItemInTheList = listOfCommonEmojis.size - 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.common_emoji_view_item, parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emoji = listOfCommonEmojis[position]

        val emojiStatement = "${String(Character.toChars(emoji.emojiCode))}\t${emoji.emojiName}"
        holder.commonEmojiTv.text = emojiStatement

        holder.commonEmojiHolder.setOnClickListener {
            if (position != lastItemInTheList)
                selector(position)
        }

        if (position == selectedItemPosition)
            holder.selectedCategory()
        else
            holder.unSelectedCategory()
    }

    private fun selector(position: Int) {
        selectedItemPosition = position

        lastSelectedItemPosition = if (lastSelectedItemPosition == -1) {
            selectedItemPosition
        } else {
            notifyItemChanged(lastSelectedItemPosition)
            selectedItemPosition
        }
        notifyItemChanged(selectedItemPosition)
    }

    override fun getItemCount(): Int = listOfCommonEmojis.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val commonEmojiHolder: MaterialCardView = v.findViewById(R.id.commonEmojiHolder)
        val commonEmojiTv: TextView = v.findViewById(R.id.commonEmojiTv)

        fun selectedCategory() {
            commonEmojiHolder.strokeWidth = 5
        }

        fun unSelectedCategory() {
            commonEmojiHolder.strokeWidth = 0
        }
    }
}