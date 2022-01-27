package com.gmail.safecart

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.blacklist.BlacklistActivity
import com.gmail.safecart.loyaltyCard.MyCardsActivity
import com.gmail.safecart.settings.SettingsActivity
import com.gmail.safecart.trash.TrashActivity

class MenuCustomAdapter(private val context: Context) :
    RecyclerView.Adapter<MenuCustomAdapter.ViewHolder>() {
    private var menuItemTitles =
        listOf(
            "Sign in / Sign up",
            "Settings",
            "Loyalty Cards",
            "Trash",
            "Recommend ${context.resources.getString(R.string.app_name)} to a friend",
            "Send feedback",
            "Blacklist"
        )
    private val menuItemIcons =
        listOf(
            R.drawable.ic_round_person_24,
            R.drawable.ic_round_settings_24,
            R.drawable.ic_round_credit_card_24,
            R.drawable.ic_round_delete_24,
            R.drawable.ic_round_favorite_24,
            R.drawable.ic_lightbulb_black_24dp,
            R.drawable.ic_block_untriggered_24dp
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemTitle = menuItemTitles[position]
        val itemIcon = menuItemIcons[position]
        holder.menuItemTv.text = itemTitle
        holder.menuItemIv.setImageResource(itemIcon)
        holder.menuItemParentL.setOnClickListener {
            when (position) {
                0 -> signInDialog()
                1 -> openSettings()
                2 -> cards()
                3 -> openTrash()
                4 -> shareApp()
                5 -> sendFeedback()
                6 -> openBlacklsit()
            }
        }
    }

    override fun getItemCount(): Int {
        return menuItemTitles.size
    }

    private fun signInDialog() {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.signin_dialog_layout, null, false)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        if (context is MainActivity)
            context.closeBottomSheetDialog()
    }

    private fun openSettings() {
        Jumper(context).jumpTo(SettingsActivity())
        if (context is MainActivity)
            context.closeBottomSheetDialog()
    }

    private fun cards() {
        Jumper(context).jumpTo(MyCardsActivity())
        if (context is MainActivity)
            context.closeBottomSheetDialog()
    }

    private fun openTrash() {
        Jumper(context).jumpTo(TrashActivity())
        if (context is MainActivity)
            context.closeBottomSheetDialog()
    }

    private fun shareApp() {
        Jumper(context).shareOnSocial()
        if (context is MainActivity)
            context.closeBottomSheetDialog()
    }

    private fun sendFeedback() {
        Jumper(context).sendEmail()
        if (context is MainActivity)
            context.closeBottomSheetDialog()
    }

    private fun openBlacklsit() {
        Jumper(context).jumpTo(BlacklistActivity())
        if (context is MainActivity)
            context.closeBottomSheetDialog()
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var menuItemParentL: LinearLayout = itemView.findViewById(R.id.menuItemParentL)
        var menuItemIv: ImageView = itemView.findViewById(R.id.menuItemIv)
        var menuItemTv: TextView = itemView.findViewById(R.id.menuItemTv)
    }
}