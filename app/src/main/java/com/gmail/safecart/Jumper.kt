package com.gmail.safecart

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity


class Jumper(private val context: Context) : Intent() {

    fun jumpTo(newActivity: Activity) {
        val intent = Intent(context, newActivity::class.java)
        startActivity(context, intent, null)
    }

    fun shareOnSocial() {
        val shareMsg = AppsConstants.appShareMessageP1 +
                " ${context.resources.getString(R.string.app_name)}" +
                " ${AppsConstants.appShareMessageP2}"
        val intent = Intent().apply {
            this.action = ACTION_SEND
            this.putExtra(EXTRA_TEXT, shareMsg)
            this.type = "text/plain"
        }
        val shareIntent = createChooser(intent, "Share ${R.string.app_name}")
        startActivity(context, shareIntent, null)
    }

    fun sendEmail() {
        val intent = Intent().apply {
            this.action = ACTION_SEND
            this.type = "text/html"
            this.putExtra(EXTRA_EMAIL, "a@gmail.com")
            this.putExtra(EXTRA_SUBJECT, "safecart suggetin")
        }
        startActivity(context, intent, null)
    }

    fun jumpWith2Extras(
        cardIdKey: String?,
        cardIdValue: Int?,

        editKey: String,
        editValue: Boolean,

        newActivity: Activity
    ) {
        val intent = Intent(context, newActivity::class.java)
            .putExtra(cardIdKey, cardIdValue)
            .putExtra(editKey, editValue)
        startActivity(context, intent, null)
    }

    fun jumpWith5Extras(
        cardIdKey: String?,
        cardIdValue: Int?,

        cardCodeKey: String,
        cardCodeValue: String,

        cardNameKey: String,
        cardNameValue: String,

        codeFormatKey: String,
        codeFormatValue: String,

        colorPositionKey: String,
        colorPositionValue: Int,

        newActivity: Activity
    ) {
        val intent = Intent(context, newActivity::class.java)
            .putExtra(cardIdKey, cardIdValue)
            .putExtra(cardCodeKey, cardCodeValue)
            .putExtra(cardNameKey, cardNameValue)
            .putExtra(codeFormatKey, codeFormatValue)
            .putExtra(colorPositionKey, colorPositionValue)
        startActivity(context, intent, null)
    }
    fun jumpWith6Extras(
        cardIdKey: String?,
        cardIdValue: Int?,

        cardCodeKey: String,
        cardCodeValue: String,

        cardNameKey: String,
        cardNameValue: String,

        codeFormatKey: String,
        codeFormatValue: String,

        colorPositionKey: String,
        colorPositionValue: Int,

        editKey: String,
        editValue: Boolean,

        newActivity: Activity
    ) {
        val intent = Intent(context, newActivity::class.java)
            .putExtra(editKey, editValue)
            .putExtra(cardIdKey, cardIdValue)
            .putExtra(cardCodeKey, cardCodeValue)
            .putExtra(cardNameKey, cardNameValue)
            .putExtra(codeFormatKey, codeFormatValue)
            .putExtra(colorPositionKey, colorPositionValue)
        startActivity(context, intent, null)
    }

    fun jumpToItems(newActivity: Activity,
    listNameKey: String,
    listNameValue: String){
        val intent = Intent(context, newActivity::class.java)
            .putExtra(listNameKey, listNameValue)
            startActivity(context, intent, null)
    }
}