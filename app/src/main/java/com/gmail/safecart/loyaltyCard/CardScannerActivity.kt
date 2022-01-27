package com.gmail.safecart.loyaltyCard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.gmail.safecart.AllBarcodeFormats.Companion.formats
import com.gmail.safecart.AppsConstants
import com.gmail.safecart.Jumper
import com.gmail.safecart.PermissionChecker
import com.gmail.safecart.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.*
import java.util.*


class CardScannerActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var imageView: ImageView
    private lateinit var permLayout: RelativeLayout
    private lateinit var codeTv: TextView
    private lateinit var textInputLayout1: TextInputLayout
    private lateinit var cardNumberEt: TextInputEditText
    private lateinit var cardNameEt: TextInputEditText
    private lateinit var turnOnScanBtn: CardView
    private lateinit var saveCardBtn: CardView
    private lateinit var parentLCard: LinearLayout
    private lateinit var colorSelectRv: RecyclerView
    private lateinit var svCardScanner: ScrollView
    private lateinit var barcodeTypeTv: TextView

    private lateinit var beepManager: BeepManager
    private var lastText: String = ""
    private lateinit var scanContracts: ScanContract
    private lateinit var camera: PermissionChecker

    private lateinit var adapter: CardColorAdapter

    private lateinit var db: CardsDb

    private var cardExists = false
    private val scrollToBottom = 5000
    private val defaultCardColorPosition = 3

    private var formatResult: BarcodeFormat? = null

    private var id: Int? = null
    private var cardCode: String? = null
    private var cardName: String? = null
    private var codeFormat: String? = null
    private var cardColorPosition: Int = -1
    private var isEditMode: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_scanner)
        init()
        toolbar()
        infoOnEditMode()

        parentLCard.setOnClickListener { hideKeyboard() }
        permLayout.setOnClickListener { permissionDialog() }

        initializeScanner()
        hideRevealLayout()

        turnOnScanBtn.setOnClickListener {
            cardNumberEt.text?.clear()
            barcodeView.resume()
        }

        cardNumberEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, delChars: Int, p3: Int) {
                if (p0?.length!! > 0) {
                    hideRevealView(turnOnScanBtn, true)

                    barcodeGeneratorEt(p0.toString(), imageView, getBarcodeType())
                } else {
                    hideRevealView(turnOnScanBtn, false)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length!! == 1) {
                    barcodeView.pause()
                } else if (p0.isEmpty()) {
                    barcodeView.resume()
                    codeTv.text = ""
                    barcodeTypeTv.text = ""
                    imageView.setImageBitmap(null)
                }
            }
        })

        colorSelectRv.setHasFixedSize(true)
        colorSelectRv.layoutManager =
            GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false)
        colorSelectRv.adapter = adapter
        (colorSelectRv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        scrollTo(cardNameEt, svCardScanner)
        scrollTo(cardNumberEt, svCardScanner)
        //cardNumberEt.setOnClickListener { svCardScanner.postDelayed({ svCardScanner.smoothScrollTo(0, scrollToBottom) }, 500) }
        saveCardBtn.setOnClickListener {
            for (cardCode in db.getCardCodes()) {
                cardExists = cardNumberEt.text.toString() == cardCode
            }
            if (cardExists) {
                Toast.makeText(applicationContext, "card already exists!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (adapter.selectedItemP == -1) {
                    adapter.selectedItemP = defaultCardColorPosition
                }
                if (InputUtil.validateRoyaltyCardInfoInput(
                        codeTv.text.toString(),
                        cardNameEt.text.toString(),
                    )
                ) {
                    val name = cardNameEt.text.toString()
                    val code = codeTv.text.toString()
                    if (isEditMode!!) {
                        val edit = db.edit(
                            CardsModel(
                                id!!,
                                name,
                                code,
                                adapter.selectedItemP,
                                getBarcodeType().toString()
                            )
                        )
                        if (edit){
                            MyCardsAdapter(this, CardsDb(this).read())
                                .insertedCardRefresh(
                                    CardsModel(
                                        -1,
                                        name,
                                        code,
                                        adapter.selectedItemP,
                                        getBarcodeType().toString()
                                    )
                                )
                            Toast.makeText(
                                this,
                                "card was edited successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            Jumper(this).jumpTo(MyCardsActivity())
                        }else{
                            Toast.makeText(
                                this,
                                "Field to edit card! please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        val insert = db.insert(
                            CardsModel(
                                -1,
                                name,
                                code,
                                adapter.selectedItemP,
                                getBarcodeType().toString()
                            )
                        )
                        if (insert) {
                            MyCardsAdapter(this, CardsDb(this).read())
                                .insertedCardRefresh(
                                    CardsModel(
                                        -1,
                                        name,
                                        code,
                                        adapter.selectedItemP,
                                        getBarcodeType().toString()
                                    )
                                )
                            Toast.makeText(
                                this,
                                "card was added successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            Jumper(this).jumpTo(MyCardsActivity())
                        } else {
                            Toast.makeText(
                                this,
                                "Field to create card! please reopen the app and try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "check your inputs!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun init() {
        isEditMode = intent.getBooleanExtra(AppsConstants.isEditMode, true)
        id = intent.getIntExtra(AppsConstants.loyaltyCardIdKey, -1)
        cardCode = intent.getStringExtra(AppsConstants.cardCodeKey)
        cardName = intent.getStringExtra(AppsConstants.cardNameKey)
        codeFormat = intent.getStringExtra(AppsConstants.codeFormatKey)
        cardColorPosition = intent.getIntExtra(AppsConstants.colorPositionKey, -1)

        toolbar = findViewById(R.id.cardScannerToolbar)
        barcodeView = findViewById(R.id.barcodeScanner)
        imageView = findViewById(R.id.barcodeIv)
        permLayout = findViewById(R.id.permiLayout)
        codeTv = findViewById(R.id.codeTv)
        textInputLayout1 = findViewById(R.id.textInputLayout1)
        cardNumberEt = findViewById(R.id.cardNumberEt)
        cardNameEt = findViewById(R.id.cardNameEt)
        turnOnScanBtn = findViewById(R.id.turnOnScanBtn)
        saveCardBtn = findViewById(R.id.saveCardBtn)
        parentLCard = findViewById(R.id.parentLCard)
        colorSelectRv = findViewById(R.id.colorSelectRv)
        svCardScanner = findViewById(R.id.svCardScanner)
        barcodeTypeTv = findViewById(R.id.barcodeTypeTv)

        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        beepManager = BeepManager(this)
        scanContracts = ScanContract()
        camera = PermissionChecker(this, Manifest.permission.CAMERA)

        db = CardsDb(this)

        adapter = CardColorAdapter(this)
    }

    private fun toolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val leftArrow = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_arrow_left)
        leftArrow?.setTint(resources.getColor(R.color.colorOnPrimary, theme))
        supportActionBar?.setHomeAsUpIndicator(leftArrow)
        if (isEditMode!!) {
            supportActionBar?.title = resources.getString(R.string.Edit_loyalty_card)
        }
    }

    private fun scrollTo(etView: View, svView: ScrollView) {
        etView.setOnFocusChangeListener { _, hasChanged ->
            if (hasChanged) {
                svView.postDelayed({ svView.smoothScrollTo(0, scrollToBottom) }, 1000)

            }
        }
    }

    private fun permissionDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.permission_dialog_layout, null, false)
        builder.setView(view)
        val msg: TextView = view.findViewById(R.id.permTv)
        val okBtn: Button = view.findViewById(R.id.permOkBtn)
        val cancelBtn: TextView = view.findViewById(R.id.permCancelBtn)

        okBtn.setBackgroundColor(resources.getColor(R.color.colorOnPrimary, null))
        okBtn.setTextColor(resources.getColor(R.color.colorPrimary, null))

        msg.text = getString(R.string.royalcard_permission_message)
        okBtn.text = getString(R.string.cam_permission_grant_btn)
        cancelBtn.text = underline(getString(R.string.cam_permission_deny_btn))

        val dialog = builder.create()
        okBtn.setOnClickListener {
            registeredCamera.launch(Manifest.permission.CAMERA)
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            Toast.makeText(applicationContext, "Permission Denied!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun initializeScanner() {
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(callback)
        barcodeView.setStatusText(getString(R.string.scanner_prompt))
    }

    private fun getBarcodeType(): BarcodeFormat {
        val barcodeType = barcodeTypeTv.text.toString().trim()
        var format: BarcodeFormat? = null
        when (barcodeType) {
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
        return format ?: BarcodeFormat.CODE_39
    }

    private fun hideRevealView(view: View, reveal: Boolean) {
        if (reveal) {
            //val ani
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
            view.visibility = View.VISIBLE
        } else {
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
            view.visibility = View.GONE
        }
    }

    private fun hideRevealLayout() {
        if (camera.grantAccess()) {
            permLayout.visibility = View.INVISIBLE
            barcodeView.visibility = View.VISIBLE
        } else {
            permLayout.visibility = View.VISIBLE
            barcodeView.visibility = View.INVISIBLE
        }
    }

    private val registeredCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (camera.grantAccess()) {
            hideRevealLayout()
        } else {
            Toast.makeText(this@CardScannerActivity, "Permission Denied!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // Prevent duplicate scans
                return
            }
            try {
                lastText = result.text
                barcodeTypeTv.text = result.barcodeFormat.toString()

                try {
                    beepManager.playBeepSound()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@CardScannerActivity, e.message, Toast.LENGTH_LONG).show()
                }

                barcodeGeneratorResult(
                    result.text.toString(),
                    imageView,
                    result.barcodeFormat
                )
                formatResult = result.barcodeFormat
                //cardNumberEt.setText(result.text.toString())
                hideRevealView(turnOnScanBtn, false)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        //override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) { super.possibleResultPoints(resultPoints) }
        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    private fun barcodeGeneratorEt(
        code: String,
        display: ImageView,
        format: BarcodeFormat,
    ) {
        try {
            val width = (getWindowWidth() / 2) + (getWindowWidth() / 6)
            val height = width / 2
            // w 400 h 170
            val hints = Hashtable<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(code, format, width, height, hints)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            display.setImageBitmap(bitmap)
            codeTv.text = code
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun barcodeGeneratorResult(
        code: String,
        display: ImageView,
        format: BarcodeFormat,
    ) {
        try {
            val width = (getWindowWidth() / 2) + (getWindowWidth() / 6)
            val height = width / 2
            // w 400 h 170
            val hints = Hashtable<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(code, format, width, height, hints)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            display.setImageBitmap(bitmap)
            codeTv.text = code

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun infoOnEditMode(){
        if (isEditMode!!){
            cardNumberEt.setText(cardCode)
            cardNameEt.setText(cardName)
        }
    }
    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    private fun underline(tv: String): SpannableString {
        val sp = SpannableString(tv)
        sp.setSpan(UnderlineSpan(), 0, tv.length, 0)
        return sp
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getWindowWidth(): Int {
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

        return outMetrics.widthPixels
    }
}