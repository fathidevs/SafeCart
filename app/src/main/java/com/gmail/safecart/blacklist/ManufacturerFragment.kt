package com.gmail.safecart.blacklist

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.R
import com.gmail.safecart.TouchEv
import com.gmail.safecart.loyaltyCard.InputUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText


class ManufacturerFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var cl: CoordinatorLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private lateinit var sheet: CardView
    private lateinit var addManufactBsb: ImageButton
    private lateinit var addManufactBtn: CardView
    private lateinit var manufactureCodeEt: TextInputEditText
    private lateinit var manufactureNameEt: TextInputEditText
    private lateinit var touchEv: TouchEv

    private lateinit var db: BlacklistDb
    private var isNeverBuy = false
    private var state = true
    private val collapseDelay = 200L
    private lateinit var adapter: ManufacturerListAdapter
    private val getPeekHeight = 250
    private var premiumAccountCheck = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manufacturer, container, false)
        init(view)
        sheet.setBackgroundResource(R.drawable.bsb_dark_bg)
        bottomSheetBehavior.apply {
            this.peekHeight = getPeekHeight
            this.isDraggable = false
        }
        layoutTouch(rv)
        layoutClTouch(sheet)
        addManufactBsb.setOnClickListener { openCloseBsb(addManufactBsb) }
        addManufactBtn.setOnClickListener {
            addManufacturerToLocalDb(
                manufactureNameEt.text.toString(),
                manufactureCodeEt.text.toString()
            )
        }
        getRecyclerViewContent()
        return view
    }

    private fun init(v: View) {
        rv = v.findViewById(R.id.manufacturesRv)
        sheet = v.findViewById(R.id.sheet)
        addManufactBsb = v.findViewById(R.id.addManufactBsb)
        addManufactBtn = v.findViewById(R.id.addManufactBtn)
        manufactureNameEt = v.findViewById(R.id.manufactureNameEt)
        manufactureCodeEt = v.findViewById(R.id.manufactureCodeEt)
        cl = v.findViewById(R.id.coordinatorLayout)

        db = BlacklistDb(requireContext())
        bottomSheetBehavior = BottomSheetBehavior.from(sheet)
        touchEv = TouchEv(rv.context)
        adapter = ManufacturerListAdapter(requireContext(), db.readManufacturer())

    }

    private fun openCloseBsb(view: View) {
        clearEtFocus()
        context.apply { hideKeyboard(view) }
        Handler(Looper.myLooper()!!).postDelayed({
            if (state) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                addManufactBsb.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_arrow_down,
                        null
                    )
                )
                state = false
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                addManufactBsb.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_add,
                        null
                    )
                )
                state = true
            }
        }, collapseDelay)
    }

    private fun closeBsb(view: View) {
        clearEtFocus()
        context.apply { hideKeyboard(view) }
        Handler(Looper.myLooper()!!).postDelayed({
            if (!state) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                addManufactBsb.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_add,
                        null
                    )
                )
                state = true
            }
        }, collapseDelay)
    }

    private fun addManufacturerToLocalDb(name: String, code: String) {
        val isValid = InputUtil.validateManufactureInputInfo(name, code)
        val isExists = db.isManufacturerExists(code)
        val currentSize = adapter.itemCount
        if (!isExists) {
            if (isValid) {
                if (!premiumAccountCheck) {
                    if (currentSize < AppsConstants.freeAccountManufactListSize) {
                        val inserted: Boolean =
                            db.insert(
                                BlacklistModel(
                                    -1,
                                    AppsConstants.typeManufact,
                                    name,
                                    null,
                                    code,
                                    isNeverBuy
                                )
                            )
                        if (inserted) {
                            clearEtText()
                            adapter.insertedMnaufactRefresh(
                                BlacklistModel(
                                    -1,
                                    AppsConstants.typeManufact,
                                    name,
                                    null,
                                    code,
                                    isNeverBuy
                                )
                            )
                            Toast.makeText(
                                context?.applicationContext,
                                "$name blacklisted successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context?.applicationContext,
                                "$name could not be added, please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context?.applicationContext,
                            "Blacklist manufacturer limit reached",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    context?.applicationContext,
                    "check your inputs and try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                context?.applicationContext,
                "this code already exists",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun layoutTouch(view: View) {
        view.setOnTouchListener { p0, _ ->
            p0?.performClick()
            clearEtFocus()
            context.apply { hideKeyboard(view) }
            closeBsb(view)
            false
        }
    }

    private fun layoutClTouch(view: View) {
        view.setOnTouchListener { p0, _ ->
            p0?.performClick()
            clearEtFocus()
            context.apply { hideKeyboard(view) }
            false
        }
    }

    private fun Context?.hideKeyboard(view: View) {
        val inputMethodManager =
            this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clearEtFocus() {
        manufactureNameEt.clearFocus()
        manufactureCodeEt.clearFocus()
    }

    private fun clearEtText() {
        manufactureNameEt.text?.clear()
        manufactureCodeEt.text?.clear()
    }

    private fun getRecyclerViewContent() {
        marginBottomRv(rv)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

    private fun marginBottomRv(rv: RecyclerView) {
        (rv.layoutParams as ConstraintLayout.LayoutParams)
            .apply {
                bottomMargin = getPeekHeight
                rv.layoutParams = this
            }
    }
}