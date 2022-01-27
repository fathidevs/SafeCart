package com.gmail.safecart

import android.content.Context
import android.view.View
import androidx.core.view.GestureDetectorCompat

class TouchEv(context: Context): View(context) {

    fun sliderTouch(view: View, onTouchEvent: GestureDetectorCompat){
        view.setOnTouchListener { p0, p1 ->
            p0?.performClick()
            onTouchEvent.onTouchEvent(p1)
            false
        }
    }
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}