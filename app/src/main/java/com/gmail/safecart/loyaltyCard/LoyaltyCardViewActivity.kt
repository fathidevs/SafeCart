package com.gmail.safecart.loyaltyCard

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.Jumper
import com.gmail.safecart.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

class LoyaltyCardViewActivity : AppCompatActivity() {

    private lateinit var loyaltyCVToolbar: Toolbar
    private lateinit var cardNameViewTv: TextView
    private lateinit var barcodeViewIv: ImageView
    private lateinit var codeViewTv: TextView
    private lateinit var scanningTipBtn: TextView
    private lateinit var cardColorBg: ConstraintLayout

    private var cardId: Int? = null
    private var cardCode: String? = null
    private var cardName: String? = null
    private var codeFormat: String? = null
    private var cardColorPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loyalty_card_view)
        init()
        toolbar()

        cardNameViewTv.text = cardName
        codeViewTv.text = cardCode
        scanningTipBtn.text = spanString(scanningTipBtn.text.toString())
        scanningTipBtn.setOnClickListener {
            Toast.makeText(applicationContext, "show tips", Toast.LENGTH_SHORT).show()
        }
        barcodeGenerator()

        cardColorBg.setBackgroundResource(LoyaltyCardColorPreset.colors[cardColorPosition])
    }

    private fun init() {
        loyaltyCVToolbar = findViewById(R.id.loyaltyCVToolbar)
        cardNameViewTv = findViewById(R.id.cardNameTv)
        codeViewTv = findViewById(R.id.codeViewTv)
        barcodeViewIv = findViewById(R.id.barcodeViewIv)
        scanningTipBtn = findViewById(R.id.scanningTipBtn)
        cardColorBg = findViewById(R.id.cardColorBg)

        cardId = intent.getIntExtra(AppsConstants.loyaltyCardIdKey, -1)
        cardCode = intent.getStringExtra(AppsConstants.cardCodeKey)
        cardName = intent.getStringExtra(AppsConstants.cardNameKey)
        codeFormat = intent.getStringExtra(AppsConstants.codeFormatKey)?.trim()
        cardColorPosition = intent.getIntExtra(AppsConstants.colorPositionKey, 0)
    }

    private fun toolbar() {
        setSupportActionBar(loyaltyCVToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val leftArrow = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_left)
        leftArrow?.setTint(resources.getColor(R.color.colorOnPrimary, null))
        supportActionBar?.setHomeAsUpIndicator(leftArrow)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.loyalty_card_view_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.delLoyaltyCard -> {
                cardDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cardDeleteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("delete ${cardName?.trim()}!")
        builder.setMessage("are you sure you want to delete ${cardName?.trim()}?")
        val dialog = builder.create()

        builder.setPositiveButton("Yes") { _, _ ->
            val deleted = CardsDb(this).delete(
                CardsModel(
                    cardId!!,
                    "",
                    "",
                    -1,
                    ""
                )
            )
            if (deleted) {
                dialog.dismiss()
                Jumper(this).jumpTo(MyCardsActivity())
            } else {
                Jumper(this).jumpTo(MyCardsActivity())
                Toast.makeText(applicationContext, "Failed to delete!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { _, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun barcodeGenerator() {
        try {
            var format: BarcodeFormat? = null
            when (codeFormat) {
                "AZTEC" -> {
                    format = BarcodeFormat.AZTEC
                }
                "CODABAR" -> {
                    format = BarcodeFormat.CODABAR
                }
                "CODE_39" -> {
                    format = BarcodeFormat.CODE_39
                }
                "CODE_93" -> {
                    format = BarcodeFormat.CODE_93
                }
                "CODE_128" -> {
                    format = BarcodeFormat.CODE_128
                }
                "DATA_MATRIX" -> {
                    format = BarcodeFormat.DATA_MATRIX
                }
                "EAN_8" -> {
                    format = BarcodeFormat.EAN_8
                }
                "EAN_13" -> {
                    format = BarcodeFormat.EAN_13
                }
                "ITF" -> {
                    format = BarcodeFormat.ITF
                }
                "MAXICODE" -> {
                    format = BarcodeFormat.MAXICODE
                }
                "PDF_417" -> {
                    format = BarcodeFormat.PDF_417
                }
                "QR_CODE" -> {
                    format = BarcodeFormat.QR_CODE
                }
                "RSS_14" -> {
                    format = BarcodeFormat.RSS_14
                }
                "RSS_EXPANDED" -> {
                    format = BarcodeFormat.RSS_EXPANDED
                }
                "UPC_A" -> {
                    format = BarcodeFormat.UPC_A
                }
                "UPC_E" -> {
                    format = BarcodeFormat.UPC_E
                }
                "UPC_EAN_EXTENSION" -> {
                    format = BarcodeFormat.UPC_EAN_EXTENSION
                }
            }
            if (format != null) {
                val width = (getWindowWidth() / 2) + (getWindowWidth() / 6)
                val height = width / 2
                val hints = Hashtable<EncodeHintType, String>()
                hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix = multiFormatWriter.encode(cardCode, format, width, height, hints)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                barcodeViewIv.setImageBitmap(bitmap)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun spanString(txt: String): SpannableString {
        val span = SpannableString(txt)
        span.setSpan(UnderlineSpan(), 0, txt.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }

    private fun getWindowWidth(): Int {
        // calculate window height
        val outMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = this.display
            @Suppress("DEPRECATION")
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }

        return outMetrics.widthPixels
    }

    private fun writeSystemSettingsDialog(){
        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()
        builder.setTitle("Screen Brightness")
        builder.setMessage("Allow the app to auto-increase brightness on this page, or manually increase brightness for better scanning result")
        builder.setPositiveButton("Allow") { _, _ -> dialog.dismiss(); writeSystemSettings() }
        builder.setNegativeButton("No") { _, _ -> dialog.dismiss() }
        builder.show()
    }
    private fun writeSystemSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:${application.packageName}")
        startActivity(intent)
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun screenBrightness() {
        val brightness = 255
        val cr = contentResolver
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness)
                val lp: WindowManager.LayoutParams = window.attributes
                lp.screenBrightness = brightness / 300F
                window.attributes = lp
            } else {
                writeSystemSettingsDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        screenBrightness()
    }
}