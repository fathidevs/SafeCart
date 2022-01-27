package com.gmail.safecart.premiumAccount

class PricingInfo {

    fun pricePerMonth(): ArrayList<Double>{
        return arrayListOf(00.0, 11.0, 4.0)
    }
    fun subDurations(): ArrayList<Int>{
        return arrayListOf(1, 6, 12)
    }
    fun itemCount(): Int{
        return pricePerMonth().size
    }
}