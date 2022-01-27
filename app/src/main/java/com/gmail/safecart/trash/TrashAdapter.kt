package com.gmail.safecart.trash

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.R
import com.gmail.safecart.lists.DeletedListDb
import com.gmail.safecart.lists.DeletedListModel
import com.gmail.safecart.lists.ShoppingListModel
import com.gmail.safecart.lists.ShoppingListsDb
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrashAdapter(private val context: Context, private val items: ArrayList<DeletedListModel>) :
    RecyclerView.Adapter<TrashAdapter.ViewHolder>() {

    private lateinit var delDb: DeletedListDb
    private lateinit var listDb: ShoppingListsDb

    init {
        if (context is TrashActivity) {
            delDb = DeletedListDb(context)
            listDb = ShoppingListsDb(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trash_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val id = item.id
        val listId = item.deletedListId
        val name = item.deletedListName
        val pos = item.deletedListPosition
        val delAt = item.deletedAt
        val removeAt = item.removeAt

        holder.listName.text = name
        holder.delBtn.setOnClickListener {
            removeListSnackbar(position, holder.itemView)
        }
        holder.removeListBtn.setOnClickListener {
            Toast.makeText(
                context.applicationContext,
                "click ${item.deletedListName}",
                Toast.LENGTH_SHORT
            ).show()
        }
        holder.restoreBtn.setOnClickListener {
            restoreList(id, listId, name, pos, delAt, removeAt, position)
        }
    }
    private fun removeListSnackbar(position: Int, itemView: View)
    {
        val db = DeletedListDb(context)
        val id = items[position].id
        val listId = items[position].deletedListId
        val listName = items[position].deletedListName
        val listPosition = items[position].deletedListPosition
        val deletedAt = items[position].deletedAt
        val removedAt = items[position].removeAt

        Snackbar.make(itemView, "Removing $listName", Snackbar.LENGTH_LONG)
            .setAction("UNDO!") {
                if (context is TrashActivity) {
                    context.recoverTempRemoved(position)
                }
            }.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    //super.onDismissed(transientBottomBar, event)
                    if (event == DISMISS_EVENT_TIMEOUT
                        || event == DISMISS_EVENT_MANUAL
                        || event == DISMISS_EVENT_SWIPE
                        || event == DISMISS_EVENT_CONSECUTIVE
                    ) {
                        removeList(db, id, listId, listName, listPosition, deletedAt, removedAt)
                    }
                }

                override fun onShown(sb: Snackbar?) {
                    super.onShown(sb)
                    if (context is TrashActivity) {
                        context.tempRemove(position)
                    }
                }
            }).show()
    }
    private fun removeList(
        db: DeletedListDb,
        id: Int,
        listId: Int,
        listName: String,
        listPosition: Int,
        deletedAt: String,
        removedAt: String
    ) {
        val deleted = db.delete(
            DeletedListModel(
                id,
                listId,
                listName,
                listPosition,
                deletedAt,
                removedAt
            )
        )
        if (deleted) {
            Toast.makeText(
                context.applicationContext,
                "$listName removed successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun restoreList(
        id: Int,
        listId: Int,
        listName: String,
        listPos: Int,
        delAt: String,
        removeAt: String,
        position: Int
    ) {
        val activity = context as TrashActivity
        activity.apply{tempRemove(position)}
        val inserted = listDb.insert(ShoppingListModel(listId, listName, getCurrentDateAndTime()))
        if (inserted) {
            val deleted =
                delDb.delete(DeletedListModel(id, listId, listName, listPos, delAt, removeAt))
            if (deleted) {
                Toast.makeText(
                    context.applicationContext,
                    "$listName restored successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else{
            Toast.makeText(
                context.applicationContext,
                "Failed to restore, please try again",
                Toast.LENGTH_SHORT
            ).show()
            activity.apply{recoverTempRemoved(position)}
        }
    }

    private fun getCurrentDateAndTime(): String {
        val simpleDateFormat = SimpleDateFormat(AppsConstants.pattern, Locale.getDefault())
        val date = Calendar.getInstance().time
        return simpleDateFormat.format(date)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val listName: TextView = v.findViewById(R.id.delListName)
        val delBtn: ImageButton = v.findViewById(R.id.removeDelList)
        val restoreBtn: ImageButton = v.findViewById(R.id.restoreDelList)
        val removeListBtn: CardView = v.findViewById(R.id.removedListItem)
    }
}