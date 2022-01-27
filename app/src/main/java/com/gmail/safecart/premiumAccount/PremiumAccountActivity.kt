package com.gmail.safecart.premiumAccount

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.viewpager.widget.ViewPager
import com.gmail.safecart.GestureD
import com.gmail.safecart.R
import com.gmail.safecart.TouchEv
import com.gmail.safecart.ZoomAnimation
import com.google.android.material.tabs.TabLayout
import java.util.*

class PremiumAccountActivity : AppCompatActivity() {
    private lateinit var premiumToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var pViewPager: ViewPager
    private lateinit var tabs: TabLayout

    private lateinit var monthlyCard: CardView
    private lateinit var monthlyTv: TextView
    private lateinit var mTotalCost: TextView
    private lateinit var mCurrencyTv: TextView
    private lateinit var mMonthlyCostTv: TextView

    private lateinit var yearlyCard: CardView
    private lateinit var yearlyTv: TextView
    private lateinit var yTotalCost: TextView
    private lateinit var yCurrencyTv: TextView
    private lateinit var yMonthlyCostTv: TextView

    private lateinit var adapter: ViewPagerAdapter
    private lateinit var priceAdapter: PricingTableAdapter

    private lateinit var priceInfo: PricingInfo

    private val sliderDelay = 5000L
    private val sliderPause = 3000L
    private var stopLoop = false
    private lateinit var slider: SliderContent
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var touchEv: TouchEv
    var isSliderBusy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_account)
        init()
        toolbar()

        pViewPager.adapter = adapter
        pViewPager.setPageTransformer(true, ZoomAnimation())
        pViewPager.pageMargin = resources.displayMetrics.widthPixels / -4
        pViewPager.offscreenPageLimit = 2
        tabs.setupWithViewPager(pViewPager, true)

        touchEv.sliderTouch(pViewPager, gestureDetector)

        viewPagerAutoScroll()
        sliderCheck()

        pricingTable()
    }

    private fun init() {
        premiumToolbar = findViewById(R.id.premiumToolbar)
        pViewPager = findViewById(R.id.premiumViewPager)
        tabs = findViewById(R.id.tabLayout)

        monthlyCard = findViewById(R.id.monthlyCard)
        monthlyTv = findViewById(R.id.monthlyTv)
        mTotalCost = findViewById(R.id.mTotalTv)
        mCurrencyTv = findViewById(R.id.mCurrencyTv)
        mMonthlyCostTv = findViewById(R.id.mMonthlyCostTv)

        yearlyCard = findViewById(R.id.yearlyCard)
        yearlyTv = findViewById(R.id.yearlyTv)
        yTotalCost = findViewById(R.id.yTotalTv)
        yCurrencyTv = findViewById(R.id.yCurrencyTv)
        yMonthlyCostTv = findViewById(R.id.yMonthlyCostTv)

        touchEv = TouchEv(pViewPager.context)
        slider = SliderContent(this)
        adapter = ViewPagerAdapter(this, slider.titles(), slider.descriptions(), slider.images())
        pViewPager.isContextClickable = true
        gestureDetector = GestureDetectorCompat(pViewPager.context, GestureD(this))
        priceInfo = PricingInfo()
        priceAdapter = PricingTableAdapter(priceInfo)
    }
    private fun toolbar() {
        setSupportActionBar(premiumToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        val leftArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_left,null)
        leftArrow?.setTint(resources.getColor(R.color.colorOnPrimary, theme))
        supportActionBar?.setHomeAsUpIndicator(leftArrow)
    }

    private fun sliderCheck() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isSliderBusy) {
                isSliderBusy = false
                viewPagerAutoScroll()
            }
        }, sliderPause)
    }
    private fun viewPagerAutoScroll() {
        var v = pViewPager.currentItem
        var revers = false
        val handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            Log.e("sli", "$isSliderBusy")

            if (!isSliderBusy) {
                run {
                    pViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                        }

                        override fun onPageSelected(position: Int) {
                            v = position
                        }

                        override fun onPageScrollStateChanged(state: Int) {}
                    })
                    if (v >= slider.size() - 1) {
                        revers = true
                    } else if (v == 0) {
                        revers = false
                    }

                    if (revers)
                        v--
                    else
                        v++
                    pViewPager.setCurrentItem(v, true)
                }
            }
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (stopLoop)
                    cancel()
                if (isSliderBusy) {
                    cancel()
                    sliderCheck()
                }
                handler.post(update)
            }
        }, sliderDelay, sliderDelay)
    }
    private fun pricingTable() {
        val selectedColor = resources.getColor(R.color.colorOnPrimary, theme)
        val selectedColorText = resources.getColor(R.color.colorOnPrimary, theme)
        monthlyCard.setCardBackgroundColor(selectedColor)
        monthlyTv.setTextColor(selectedColorText)
        mTotalCost.setTextColor(selectedColorText)
        mCurrencyTv.setTextColor(selectedColorText)
        mMonthlyCostTv.setTextColor(selectedColorText)

        monthlyCard.setOnClickListener {
            selectedCard(
                selected = monthlyCard,
                selectedTv1 = monthlyTv,
                selectedTv2 = mTotalCost,
                selectedTv3 = mCurrencyTv,
                selectedTv4 = mMonthlyCostTv,
                unselected = yearlyCard,
                unselectedTv1 = yearlyTv,
                unselectedTv2 = yTotalCost,
                unselectedTv3 = yCurrencyTv,
                unselectedTv4 = yMonthlyCostTv
            )
        }

        yearlyCard.setOnClickListener {
            selectedCard(
                selected = yearlyCard,
                selectedTv1 = yearlyTv,
                selectedTv2 = yTotalCost,
                selectedTv3 = yCurrencyTv,
                selectedTv4 = yMonthlyCostTv,
                unselected = monthlyCard,
                unselectedTv1 = monthlyTv,
                unselectedTv2 = mTotalCost,
                unselectedTv3 = mCurrencyTv,
                unselectedTv4 = mMonthlyCostTv
            )
        }
    }

    private fun selectedCard(
        selected: CardView,
        selectedTv1: TextView,
        selectedTv2: TextView,
        selectedTv3: TextView,
        selectedTv4: TextView,
        unselected: CardView,
        unselectedTv1: TextView,
        unselectedTv2: TextView,
        unselectedTv3: TextView,
        unselectedTv4: TextView
    ) {
        val selectedColor = resources.getColor(R.color.colorOnPrimary, theme)
        val selectedColorText = resources.getColor(R.color.colorOnPrimary, theme)
        val unselectedColor = resources.getColor(R.color.colorPrimary, theme)
        val unselectedColorText = resources.getColor(R.color.colorPrimaryVariant, theme)

        // selected
        selected.setCardBackgroundColor(selectedColor)
        selectedTv1.setTextColor(selectedColorText)
        selectedTv2.setTextColor(selectedColorText)
        selectedTv3.setTextColor(selectedColorText)
        selectedTv4.setTextColor(selectedColorText)
        // unselected
        unselected.setCardBackgroundColor(unselectedColor)
        unselectedTv1.setTextColor(unselectedColorText)
        unselectedTv2.setTextColor(unselectedColorText)
        unselectedTv3.setTextColor(unselectedColorText)
        unselectedTv4.setTextColor(unselectedColorText)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopLoop = true
    }
}