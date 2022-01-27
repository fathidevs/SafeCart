package com.gmail.safecart

import android.view.GestureDetector
import android.view.MotionEvent
import com.gmail.safecart.premiumAccount.PremiumAccountActivity

class GestureD(private val activity: PremiumAccountActivity) : GestureDetector.SimpleOnGestureListener() {

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (velocityX <= 0 || velocityX >= 0 || velocityY <= 0 || velocityY >= 0 ) {
            activity.isSliderBusy = true
        }
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onShowPress(e: MotionEvent?) {

        activity.isSliderBusy = true
        super.onShowPress(e)
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (distanceX <= 0 || distanceX >= 0 || distanceY <= 0 || distanceY >= 0 ) {
            activity.isSliderBusy = true
        }
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    override fun onDown(e: MotionEvent?): Boolean {
        activity.isSliderBusy = true
        return super.onDown(e)
    }

    override fun onLongPress(e: MotionEvent?) {
        activity.isSliderBusy = true
        super.onLongPress(e)
    }
}