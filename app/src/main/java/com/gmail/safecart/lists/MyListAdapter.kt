package com.gmail.safecart.lists

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.Jumper
import com.gmail.safecart.MainActivity
import com.gmail.safecart.R
import com.gmail.safecart.items.MyItemsActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator

class MyListAdapter(private val context: Context, private val items: ArrayList<ShoppingListModel>) :
    RecyclerView.Adapter<MyListAdapter.ViewHolder>() {

    private var selectedItemPosition = -1
    private var lastSelectedItemPosition = -1
    var selectionIsOn = false

    private val selectedCard = ArrayList<MaterialCardView>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.shopping_list_item, parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.listNameTv.text = item.listName

        holder.listItemBtn.setOnClickListener {
            if (!selectionIsOn)
                Jumper(context).jumpToItems(
                    MyItemsActivity(),
                    AppsConstants.listNameKey,
                    item.listName
                )
            else{
                clearSelection(selectedCard)
            }
        }

        holder.listItemBtn.setOnLongClickListener {
            selectItem(position)
            selectionIsOn = true
            if (selectedCard.isEmpty())
                selectedCard.add(holder.listItemBtn)
            else {
                selectedCard.removeAll { true }
                selectedCard.add(holder.listItemBtn)
            }
            return@setOnLongClickListener true
        }
        if (position == selectedItemPosition){
            holder.selected(context)
        }else{
            holder.unselected()
        }
    }

    private fun selectItem(position: Int) {
        selectedItemPosition = position

        lastSelectedItemPosition = if (lastSelectedItemPosition == -1){
            selectedItemPosition
        }else{
            notifyItemChanged(lastSelectedItemPosition)
            selectedItemPosition
        }
        notifyItemChanged(selectedItemPosition)
        /*if (lastSelectedItemPosition == -1){
            lastSelectedItemPosition = selectedItemPosition
        }else{
            notifyItemChanged(lastSelectedItemPosition)
            lastSelectedItemPosition = selectedItemPosition
        }*/
    }
    private fun clearSelection(buttons: ArrayList<MaterialCardView>) {
        for (btn in buttons)
            btn.setCardBackgroundColor(Color.WHITE)
        if (context is MainActivity)
            context.editState = context.editMode(context, false)
        selectionIsOn = false
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val listItemBtn: MaterialCardView = v.findViewById(R.id.listItemBtn)
        val listNameTv: TextView = v.findViewById(R.id.listNameTv)
        val unScannedItemsCounterTv: TextView = v.findViewById(R.id.unscannedItemsCounterTv)

        //val itemProgBar: CircularProgressIndicator = v.findViewById(R.id.itemCountProgressBar)
        val itemProgBar: CircularProgressIndicator = v.findViewById(R.id.itemCountProgressBar)

        fun selected(context: Context){
            listItemBtn.setCardBackgroundColor(context.resources.getColor(R.color.darkWhite, null))
            if (context is MainActivity)
                context.editState = context.editMode(context, true)
        }
        fun unselected() = listItemBtn.setCardBackgroundColor(Color.WHITE)
    }
}