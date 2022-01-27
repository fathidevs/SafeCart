package com.gmail.safecart.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.R
import com.gmail.safecart.lists.DeletedListDb
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.collections.ArrayList

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var shoppingListCategory: TextView

    private lateinit var trashBtn: ImageButton
    private lateinit var trashTitleTv: TextView

    private lateinit var scannerCategory: TextView

    private lateinit var beepSw: SwitchCompat
    private lateinit var beepTitleTv: TextView

    private lateinit var autoCheckSw: SwitchCompat
    private lateinit var autoTitleTv: TextView

    private lateinit var pricesCategory: TextView

    private lateinit var multiplySw: SwitchCompat
    private lateinit var multiplyTitleTv: TextView

    private lateinit var generalCategory: TextView

    private lateinit var screenOnSw: SwitchCompat
    private lateinit var screenOnTitleTv: TextView

    private lateinit var versionTv: TextView

    private lateinit var bsd: BottomSheetDialog

    private lateinit var db: SettingsDb
    private lateinit var delDb: DeletedListDb

    private lateinit var versionStatement: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        setContentView(R.layout.activity_settings)
        init()
        toolbar()

        versionTv.text = versionStatement
        // scanner category
        switchThis(beepSw, scannerCategory, beepTitleTv)
        switchThis(autoCheckSw, scannerCategory, autoTitleTv)
        // prices category
        switchThis(multiplySw, pricesCategory, multiplyTitleTv)
        // general category
        switchThis(screenOnSw, generalCategory, screenOnTitleTv)

        trashBtn.setOnClickListener {
            bottomSheetDialog(shoppingListCategory, trashTitleTv, setMenuText(AppsConstants.trash))
        }
    }

    private fun init() {
        toolbar = findViewById(R.id.settingsToolbar)

        shoppingListCategory = findViewById(R.id.shoppingListCategory)

        trashBtn = findViewById(R.id.trashBtn)
        trashTitleTv = findViewById(R.id.trashTitleTv)

        scannerCategory = findViewById(R.id.scannerCategory)

        beepSw = findViewById(R.id.beepSw)
        beepTitleTv = findViewById(R.id.beepTitleTv)

        autoCheckSw = findViewById(R.id.AutoCheckSw)
        autoTitleTv = findViewById(R.id.autoTitleTv)

        pricesCategory = findViewById(R.id.pricesCategoryTv)

        multiplySw = findViewById(R.id.multiplySw)
        multiplyTitleTv = findViewById(R.id.multiplyTitleTv)

        versionTv = findViewById(R.id.versionTv)

        generalCategory = findViewById(R.id.generalCategory)

        screenOnSw = findViewById(R.id.screenOnSw)
        screenOnTitleTv = findViewById(R.id.screenOnTitleTv)

        bsd = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        db = SettingsDb(this)
        delDb = DeletedListDb(this)
        versionStatement =
            "${resources.getString(R.string.version)} " +
                    this.resources.getString(R.string.versionName)
    }

    private fun toolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val leftArrow = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_left)
        leftArrow?.setTint(resources.getColor(R.color.colorOnPrimary, null))
        supportActionBar?.setHomeAsUpIndicator(leftArrow)
    }

    private fun titleExists(tit: String): Boolean {
        if (db.getTitles().size > 0) {
            for (t in db.getTitles()) {
                if (t == tit) {
                    return true
                }
            }
        }
        return false
    }

    private fun switchThis(switch: SwitchCompat, getCategory: TextView, getTitle: TextView) {
        val category = getCategory.text.toString().trim()
        val title = getTitle.text.toString().trim()
        val check = db.findBools(title)
        switch.isChecked = check > 0
        switch.setOnClickListener {
            if (titleExists(title)) {
                val isEdited = db.edit(SettingsModel(0, category, title, switch.isChecked, null))
                if (!isEdited) {
                    Toast.makeText(
                        this,
                        "something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val isInserted =
                    db.insert(SettingsModel(-1, category, title, switch.isChecked, null))
                if (!isInserted) {
                    Toast.makeText(
                        this,
                        "something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun pickThis(pickedDuration: String, getCategory: TextView, getTitle: TextView) {
        val category = getCategory.text.toString().trim()
        val title = getTitle.text.toString().trim()

        if (titleExists(title)) {
            val isEdited = db.edit(SettingsModel(0, category, title, null, pickedDuration))
            if (isEdited) {
                editRemovingDatesInTheDb(pickedDuration)
            } else {
                Toast.makeText(this, "something went wrong please try again", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            // insert
            val isInserted = db.insert(SettingsModel(-1, category, title, null, pickedDuration))
            if (isInserted) {
                editRemovingDatesInTheDb(pickedDuration)
            } else {
                Toast.makeText(this, "something went wrong please try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun editRemovingDatesInTheDb(duration: String) {

        val listOfDurations = resources.getStringArray(R.array.duration)
        val listOfDeletedAtDates = delDb.getDeletedAt()
        val listOfNewRemovingDate = ArrayList<String>()

        val oneDay = "00000001000000"
        val oneWeek = "00000007000000"
        val oneMonth = "00000100000000"
        val sixMonths = "00000600000000"
        val oneYear = "10000000000000"

        when (duration) {
            listOfDurations[0] -> { // day
                for (deletedAt in listOfDeletedAtDates) {
                    val calculatedRemovingDate = deletedAt.toLong() + oneDay.toLong()
                    listOfNewRemovingDate.add(calculatedRemovingDate.toString())
                }
            }
            listOfDurations[1] -> { // week
                for (deletedAt in listOfDeletedAtDates) {
                    val calculatedRemovingDate = deletedAt.toLong() + oneWeek.toLong()
                    listOfNewRemovingDate.add(calculatedRemovingDate.toString())
                }
            }
            listOfDurations[2] -> { // month
                for (deletedAt in listOfDeletedAtDates) {
                    val calculatedRemovingDate = deletedAt.toLong() + oneMonth.toLong()
                    listOfNewRemovingDate.add(calculatedRemovingDate.toString())
                }
            }
            listOfDurations[3] -> { // 6months
                for (deletedAt in listOfDeletedAtDates) {
                    val calculatedRemovingDate = deletedAt.toLong() + sixMonths.toLong()
                    listOfNewRemovingDate.add(calculatedRemovingDate.toString())
                }
            }
            listOfDurations[4] -> { // year
                for (deletedAt in listOfDeletedAtDates) {
                    val calculatedRemovingDate = deletedAt.toLong() + oneYear.toLong()
                    listOfNewRemovingDate.add(calculatedRemovingDate.toString())
                }
            }
            listOfDurations[5] -> { // forever
                for (deletedAt in listOfDeletedAtDates) {
                    listOfNewRemovingDate.add("")
                }
            }
            listOfDurations[6] -> { // never
                for (deletedAt in listOfDeletedAtDates) {
                    listOfNewRemovingDate.add(deletedAt)
                }
            }
        }
        delDb.editRemovingDates(listOfNewRemovingDate)
    }

    private fun bottomSheetDialog(getCategory: TextView, getTitle: TextView, menuText: String) {
        bsd.setContentView(R.layout.trash_duration_picker_layout)
        val card: CardView? = bsd.findViewById(R.id.bsdTrashCard)
        val trashDurationMenu: AutoCompleteTextView? = bsd.findViewById(R.id.trashDurationMenu)
        val durationItems = resources.getStringArray(R.array.duration)
        val adapter = ArrayAdapter(this, R.layout.exposed_dropdown_item, durationItems)

        trashDurationMenu?.setText(menuText.trim())
        trashDurationMenu?.setAdapter(adapter)
        card?.setBackgroundResource(R.drawable.bsd_bg)
        setUpHeight(bsd)

        trashDurationMenu?.setOnItemClickListener { _, _, i, _ ->
            val pickedDuration = durationItems[i]
            pickThis(pickedDuration, getCategory, getTitle)
        }
        bsd.show()
    }

    private fun setMenuText(@Suppress("SameParameterValue") title: String): String {
        val durationItems = resources.getStringArray(R.array.duration)
        var default = durationItems[0]

        if (db.getChoices(title) != "non") {
            default = db.getChoices(title)
        }
        return default
    }

    private fun setUpHeight(bsd: BottomSheetDialog) {
        val layout: CardView? = bsd.findViewById(R.id.bsdTrashCard)
        //val behavior = BottomSheetBehavior.from(layout)
        val params = layout?.layoutParams

        val windowHeight = getWindowHeight()
        if (params != null) {
            params.height = windowHeight
        }
        layout?.layoutParams = params
    }

    private fun getWindowHeight(): Int {
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

    override fun onResume() {
        super.onResume()
        setMenuText(AppsConstants.trash)
    }
}