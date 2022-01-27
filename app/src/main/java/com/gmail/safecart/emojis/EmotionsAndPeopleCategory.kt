package com.gmail.safecart.emojis

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.gmail.safecart.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.jvm.internal.Intrinsics

class EmotionsAndPeopleCategory() {

    companion object {
        fun epEmojiNameAndCodeList(): ArrayList<EmojiModel> {
            val returnList = ArrayList<EmojiModel>()
            returnList.addAll(
                listOf(
                    EmojiModel("grinning face", 0x1F600),
                    EmojiModel("grinning face", 0x1F600),
                    EmojiModel("grinning face", 0x1F600),
                    EmojiModel("grinning face", 0x1F600),
                    EmojiModel("grinning face", 0x1F600),
                    EmojiModel("grinning face", 0x1F600),
                    EmojiModel("grinning face", 0x1F600),
                )
            )
            return returnList
        }

        private fun epSize(): Int {
            return epEmojiNameAndCodeList().size
        }
    }

}