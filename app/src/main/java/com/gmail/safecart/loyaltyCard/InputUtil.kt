package com.gmail.safecart.loyaltyCard

object InputUtil {

    fun validateRoyaltyCardInfoInput(
        code: String,
        name: String,
    ): Boolean {
        var isFound = false
        for (char in IllegalCharacters.illegalCharacters)
            if (char == code) isFound = true
        return !(
                isFound
                || code.length > 80
                || code.isBlank()
                || name.isBlank()
                )
    }
    fun validateManufactureInputInfo(
        name: String,
        code: String
    ): Boolean{
        return !(
                name.isBlank()
                || name.length < 2
                || code.length < 12
                || code.isBlank()
                )
    }
}