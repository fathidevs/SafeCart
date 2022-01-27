package com.gmail.safecart.premiumAccount

import com.gmail.safecart.R

class SliderContent(private val activity: PremiumAccountActivity) {

    fun titles(): Array<String>{
        return arrayOf("No Ads!", "Support ${activity.resources.getString(R.string.app_name)}", "Infinite love")
    }
    fun images(): Array<Int>{
        return arrayOf(R.drawable.ic_no_ad, R.drawable.ic_support, R.drawable.ic_unli)
    }
    fun descriptions(): Array<String>{
        return arrayOf("Build lists, without interruptions!", "Help in building advanced shopping list app", "Unlimited access to everything!")
    }
    fun size(): Int{
        return images().size
    }
}