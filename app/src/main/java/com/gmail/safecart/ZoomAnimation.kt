package com.gmail.safecart

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max

class ZoomAnimation : ViewPager.PageTransformer {

    private val minScale = 0.70F
    private val minAlpha = 0.5F

    override fun transformPage(page: View, position: Float) {

        val pageWidth = page.width
        val pageHeight = page.height

        when {
            (position < -1) -> { page.alpha = 0F }
            (position <= 1) -> {
                val scaleFactor = max(minScale, 1 - abs(position))
                val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                val horizontalMargin = pageWidth * (1 - scaleFactor) / 2

                if (position < 0) {
                    page.translationX = (horizontalMargin - verticalMargin / 2)
                } else {
                    page.translationX = (-horizontalMargin + verticalMargin / 2)
                }

                page.scaleX = scaleFactor
                page.scaleY = scaleFactor

                page.alpha = minAlpha + (scaleFactor - minScale) / (1 - minScale) * (1 - minAlpha)
            }
            else -> {
                page.alpha = 0F
            }
        }
    }
}