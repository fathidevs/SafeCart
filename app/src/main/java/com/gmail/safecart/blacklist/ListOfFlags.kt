package com.gmail.safecart.blacklist

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import com.jsramraj.flags.Flags

class ListOfFlags(context: Context, private val countryCode: String) {

    val flagInit = Flags.init(context)
    fun getFlag(): BitmapDrawable = Flags.forCountry(countryCode)

}