package com.gmail.safecart.blacklist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.gmail.safecart.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BlacklistActivity : AppCompatActivity(){

    private lateinit var toolbar: Toolbar
    private lateinit var blTabLayout: TabLayout
    private lateinit var fragsPager: ViewPager2
    private lateinit var navMcv: MaterialCardView

    private lateinit var adapter: BlacklistViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blacklist)
        init()
        toolbar()
        //navMcv.setCardBackgroundColor(resources.getColor(R.color.colorPrimary, null))
        fragsPager.adapter = adapter

        TabLayoutMediator(blTabLayout, fragsPager){tab,position->

            when(position){
                0 -> tab.text = "Country"
                1 -> tab.text = "Manufacturer"
            }
        }.attach()

        blTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Toast.makeText(applicationContext, "pos: ${tab?.position!!}", Toast.LENGTH_SHORT)
                    .show()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun init(){
        toolbar = findViewById(R.id.blacklistToolbar)
        blTabLayout = findViewById(R.id.blTabLayout)
        fragsPager = findViewById(R.id.fragsPager)
        navMcv = findViewById(R.id.navMcv)

        adapter = BlacklistViewPagerAdapter(supportFragmentManager, lifecycle)
    }
    private fun toolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val leftArrow = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_arrow_left)
        leftArrow?.setTint(resources.getColor(R.color.colorOnPrimary, null))
        supportActionBar?.setHomeAsUpIndicator(leftArrow)
    }
}

// manufacture: 1st 6 digits
// country: 1st 3 digits