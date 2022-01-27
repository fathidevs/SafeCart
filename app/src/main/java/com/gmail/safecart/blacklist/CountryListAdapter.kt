package com.gmail.safecart.blacklist

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.R

class CountryListAdapter(
    private val context: Context,
    private val countryNameItems: ArrayList<String>,
    private val countryCodeItems: ArrayList<String>
) :
    RecyclerView.Adapter<CountryListAdapter.ViewHolder>() {

    private lateinit var listOfFlags: ListOfFlags
    private val originalFlagWidth = 32
    private val originalFlagHeight = 22
    private val increasedPercentage = 1.5
    private val modifiedFlagWidth =
        ((originalFlagWidth * increasedPercentage) + originalFlagWidth).toInt()
    private val modifiedFlagHeight =
        ((originalFlagHeight * increasedPercentage) + originalFlagHeight).toInt()
    private var db: BlacklistDb
    private val type = "country"
    private val isNeverBuy = false
    private var premiumAccountCheck = false

    init {
        db = BlacklistDb(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.country_list_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val code = countryCodeItems[position]
        val country = countryNameItems[position]

        listOfFlags = ListOfFlags(context, code)
        listOfFlags.flagInit
        val flag: BitmapDrawable = listOfFlags.getFlag()
        db = BlacklistDb(context)

        val scaledFlag =
            Bitmap.createScaledBitmap(flag.bitmap, modifiedFlagWidth, modifiedFlagHeight, true)
        holder.flagIv.setImageBitmap(scaledFlag)
        holder.countryNameTv.text = country

        holder.countryBlockBtn.setOnClickListener {
            val isExists = db.isCountryExists(country)
            val id = db.getCountryId(country)
            if (isExists) unblock(country, id, position) else block(code, country, position)
        }

        viewBlockedCountry(holder.layoutPosition, country, holder.countryBlockBtn)

        if (position % 2 == 0)
            holder.countryInfoItem.background = ColorDrawable(Color.WHITE)
        else
            holder.countryInfoItem.background = ColorDrawable(context.getColor(R.color.darkWhite))
    }

    private fun viewBlockedCountry(position: Int, country: String, countryBlockBtn: ImageButton) {
        val unTriggeredBlockBtn =
            AppCompatResources.getDrawable(context, R.drawable.ic_block_untriggered_24dp)
        val triggeredBlockBtn =
            AppCompatResources.getDrawable(context, R.drawable.ic_round_block_triggered_24)
        if (db.getCountryPosition(country) == position) {
            countryBlockBtn.setImageDrawable(triggeredBlockBtn)
        } else {
            countryBlockBtn.setImageDrawable(unTriggeredBlockBtn)
        }
    }

    override fun getItemCount(): Int = countryNameItems.size

    private fun unblock(countryName: String, id: Int, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Unblock $countryName")
        builder.setMessage("Stop boycotting products from $countryName?")
        builder.setPositiveButton("Yes") { _, _ ->
            val deleted = db.delete(BlacklistModel(id, "", countryName, null, "", false))
            if (deleted)
                notifyItemChanged(position)
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun block(code: String, country: String, position: Int) {
        val listSize = db.countryListSize()
        if (!premiumAccountCheck) {
            if (listSize < AppsConstants.freeAccountCountryListSize) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Block $country")
                builder.setMessage("Boycott products from $country?")
                builder.setPositiveButton("Yes") { _, _ ->
                    val inserted = db.insert(BlacklistModel(0, type, country, position, code, isNeverBuy))
                    if (inserted)
                        notifyItemChanged(position)
                }.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                val dialog = builder.create()
                dialog.show()
            } else {
                Toast.makeText(context.applicationContext, "Blacklist country limit reached", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val flagIv: ImageView = view.findViewById(R.id.flagIv)
        val countryNameTv: TextView = view.findViewById(R.id.countryNameTv)
        val countryBlockBtn: ImageButton = view.findViewById(R.id.countryBlockBtn)
        val countryInfoItem: LinearLayout = view.findViewById(R.id.countryInfoItem)
    }
}