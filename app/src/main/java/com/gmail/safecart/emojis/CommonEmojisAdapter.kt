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
    private val listOfCommonEmojis: ArrayList<EmojiModel>):
    RecyclerView.Adapter<CommonEmojisAdapter.ViewHolder>() {

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
        val emojiStatement = "${String(Character.toChars(emoji.emojiCode))} car"
        holder.commonEmojiTv.text = emojiStatement

        holder.commonEmojiHolder.setOnClickListener {
            Toast.makeText(context.applicationContext, "pos = $position", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun getItemCount(): Int = listOfCommonEmojis.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val commonEmojiHolder: MaterialCardView = v.findViewById(R.id.commonEmojiHolder)
        val commonEmojiTv: TextView = v.findViewById(R.id.commonEmojiTv)
    }
}