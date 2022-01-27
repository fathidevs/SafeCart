package com.gmail.safecart.loyaltyCard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.Jumper
import com.gmail.safecart.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyCardsActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var loyaltyCardRv: RecyclerView
    private lateinit var addCardBtn: FloatingActionButton

    private lateinit var myCardsAdapter: MyCardsAdapter
    private lateinit var db: CardsDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cards)
        init()
        toolBar()

        addCardBtn.setOnClickListener {
            Jumper(this).jumpWith2Extras(
                null, null,
                AppsConstants.isEditMode,
                false,
                CardScannerActivity()
            )
        }

        loyaltyCardRv.setHasFixedSize(true)
        loyaltyCardRv.adapter = myCardsAdapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        loyaltyCardRv.layoutManager = layoutManager

    }

    private fun init() {
        toolbar = findViewById(R.id.cardsToolbar)
        loyaltyCardRv = findViewById(R.id.loyaltyCardRv)
        addCardBtn = findViewById(R.id.addCardBtn)

        db = CardsDb(this)
        val cardList = db.read()
        myCardsAdapter = MyCardsAdapter(this, cardList)
    }

    private fun toolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        val leftArrow = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_left)
        leftArrow?.setTint(resources.getColor(R.color.colorOnPrimary, null))
        supportActionBar?.setHomeAsUpIndicator(leftArrow)

    }

    private fun refreshRv(db: CardsDb) {
        myCardsAdapter = MyCardsAdapter(this, db.read())
        loyaltyCardRv.adapter = myCardsAdapter
    }

    override fun onResume() {
        super.onResume()
        val db = CardsDb(this)
        refreshRv(db)
    }
}