package com.gmail.safecart.loyaltyCard

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class InputUtilTest{

    /**
     * the input is not valid if ...
     * ... contains illegal chars that specified in IllegalCharacters.kt
     * ... length exceeds 80
     * ... one blank field or two
     * ...
     *
     */

    @Test
    fun `if barcode contains illegal characters return false`(){
        val result = InputUtil.validateRoyaltyCardInfoInput(
            IllegalCharacters.illegalCharacters.component1(),
            "shop",
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `if barcode length exceeds 80 characters return false`(){
        val result = InputUtil.validateRoyaltyCardInfoInput(
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "shop",
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `if barcode field is blank return false`(){
        val result = InputUtil.validateRoyaltyCardInfoInput(
            "",
            "shop",
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `if name field is blank return false`(){
        val result = InputUtil.validateRoyaltyCardInfoInput(
            "abc123",
            "",
        )
        assertThat(result).isFalse()
    }
}