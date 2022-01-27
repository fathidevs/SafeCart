package com.gmail.safecart.items

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView

class MyItemsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var bsb: BottomSheetBehavior<MaterialCardView>
    private var listName: String? = null
    private lateinit var sheet: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_items)
        init()
        toolbar()
        val bsbHeight: Int = (getWindowHeight()*.80).toInt()
        bsb.apply {
            this.isDraggable = false
            this.peekHeight = bsbHeight
        }
    }
    private fun init(){
        toolbar = findViewById(R.id.myItemsToolbar)
        sheet = findViewById(R.id.itemSheetCard)

        bsb = BottomSheetBehavior.from(sheet)

        listName = intent.getStringExtra(AppsConstants.listNameKey)
    }
    private fun toolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = listName

        val leftArrow = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_arrow_left)
        leftArrow?.setTint(resources.getColor(R.color.colorOnPrimary, null))
        supportActionBar?.setHomeAsUpIndicator(leftArrow)
    }
    private fun getWindowHeight(): Int{
        // calculate window height
        val outMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = this.display
            @Suppress("DEPRECATION")
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }
        return outMetrics.heightPixels
    }
}