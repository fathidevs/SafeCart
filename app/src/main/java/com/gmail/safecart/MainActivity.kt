// created 11.20.2021
// finished
package com.gmail.safecart

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.blacklist.BlacklistDb
import com.gmail.safecart.blacklist.BlacklistModel
import com.gmail.safecart.blacklist.ManufacturerListAdapter
import com.gmail.safecart.emojis.CommonEmojisAdapter
import com.gmail.safecart.emojis.EmotionsAndPeopleCategory
import com.gmail.safecart.emojis.InitialCategoryPicks
import com.gmail.safecart.lists.*
import com.gmail.safecart.loyaltyCard.CardScannerActivity
import com.gmail.safecart.loyaltyCard.InputUtil
import com.gmail.safecart.premiumAccount.PremiumAccountActivity
import com.gmail.safecart.productScanner.ProductScanner
import com.gmail.safecart.scanResultLib.Find
import com.gmail.safecart.settings.SettingsDb
import com.gmail.safecart.settings.SettingsModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.vanniktech.emoji.*
import com.vanniktech.emoji.twitter.TwitterEmojiProvider
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btmNavView: BottomNavigationView
    private lateinit var mainToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var addCard: ImageButton
    private lateinit var premiumBtn: ImageButton
    private lateinit var addList: FloatingActionButton
    private lateinit var rv: RecyclerView

    private lateinit var bsd: BottomSheetDialog
    private lateinit var adapter: MenuCustomAdapter

    private lateinit var scanContracts: ScanContract

    private lateinit var settingsDb: SettingsDb
    private lateinit var blacklistDb: BlacklistDb
    private lateinit var shoppingListsDb: ShoppingListsDb
    private lateinit var deletedListDb: DeletedListDb

    private lateinit var camera: PermissionChecker

    private val uriString = "http://www.google.com/search?q="

    private lateinit var listAdapter: MyListAdapter

    private var swipeEnabled = true

    var editState = false

    private var isInSelectionMode: Boolean = false

    private var premiumAccountCheck = false
    private var isNeverBuy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        setContentView(R.layout.activity_main)
        init()

        btmNavView.background = null
        btmNavView.menu.getItem(1).isEnabled = false

        btmNavView.setOnLongClickListener(View.OnLongClickListener {
            Toast.makeText(this, "long click", Toast.LENGTH_SHORT).show()
            return@OnLongClickListener true
        })

        // toolbar buttons
        addCard.setOnClickListener { addCard() }
        premiumBtn.setOnClickListener { premiumAccountDialog() }

        // bottom nav buttons
        addList.setOnClickListener {
            addListDialog()

        }
        btmNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mainMenu -> {
                    mainMenu()
                    true
                }
                R.id.scanner -> {
                    if (camera.grantAccess()) {
                        registeredCamera.launch(Manifest.permission.CAMERA)
                    } else {
                        permissionDialog()
                    }
                    true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }

        myListContent()
        recyclerViewSwipe()
    }

    private fun init() {
        camera = PermissionChecker(this, Manifest.permission.CAMERA)

        btmNavView = findViewById(R.id.btm_nav_view)
        mainToolbar = findViewById(R.id.mainToolbar)
        addCard = findViewById(R.id.addCard)
        premiumBtn = findViewById(R.id.premiumBtn)
        addList = findViewById(R.id.addListBtn)
        rv = findViewById(R.id.myListRv)

        bsd = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)

        adapter = MenuCustomAdapter(this)

        scanContracts = ScanContract()

        settingsDb = SettingsDb(this)
        blacklistDb = BlacklistDb(this)
        shoppingListsDb = ShoppingListsDb(this)
        deletedListDb = DeletedListDb(this)

        listAdapter = MyListAdapter(this, shoppingListsDb.read())
        isInSelectionMode = listAdapter.selectionIsOn
    }

    private fun myListContent() {
        val bnvMinHeight = BottomNavigationView(this).minimumHeight
        val rvTopPadding = 0
        rv.clipToPadding = false
        rv.setPadding(0, 0, 0, bnvMinHeight + rvTopPadding)

        rv.setHasFixedSize(true)
        val lm = LinearLayoutManager(this)
        lm.reverseLayout = true
        lm.stackFromEnd = true
        rv.layoutManager = lm
        rv.adapter = listAdapter

    }

    private fun recyclerViewSwipe() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun isItemViewSwipeEnabled(): Boolean = swipeEnabled

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val listsDb = ShoppingListsDb(this@MainActivity)
                val read = listsDb.read()
                val position = viewHolder.adapterPosition
                val id = read[position].id
                val listName = read[position].listName
                val createdAt = read[position].createdAt

                val snackbar =
                    Snackbar.make(viewHolder.itemView, "$listName deleted!", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO!") {
                            recoverTempRemoved(position)
                        }.addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                //super.onDismissed(transientBottomBar, event)
                                if (event == DISMISS_EVENT_TIMEOUT
                                    || event == DISMISS_EVENT_CONSECUTIVE
                                    || event == DISMISS_EVENT_SWIPE
                                    || event == DISMISS_EVENT_MANUAL
                                ) {

                                    val insertedAtDeletedLists = deletedListDb.insert(
                                        DeletedListModel(
                                            id,
                                            id,
                                            listName,
                                            position,
                                            deletedAt(),
                                            removeAt()
                                        )
                                    )
                                    if (insertedAtDeletedLists) {
                                        val deleted = listsDb.delete(
                                            ShoppingListModel(
                                                id,
                                                listName,
                                                createdAt
                                            )
                                        )
                                        if (deleted) {
                                            refreshRv(position, false)
                                            swipeEnabled = true
                                        } else {
                                            recoverTempRemoved(position)
                                            Toast.makeText(
                                                applicationContext,
                                                "could not delete $listName, try again",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }

                            override fun onShown(sb: Snackbar?) {
                                super.onShown(sb)
                                tempRemove(position)
                                swipeEnabled = false
                            }
                        })
                snackbar.show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                try {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        viewHolder.itemView.translationX = dX

                        val paint = Paint()
                        val marginLBtn = dpToPixel(50F, null)
                        val marginRBtn = dpToPixel(-50F, null)

                        val itemHeight = viewHolder.itemView.height

                        val itemLeftPoint = viewHolder.itemView.left
                        val itemRightPoint = viewHolder.itemView.right
                        val itemTopPoint = viewHolder.itemView.top
                        val itemBottomPoint = viewHolder.itemView.bottom

                        val drawable = AppCompatResources
                            .getDrawable(
                                this@MainActivity,
                                R.drawable.ic_round_delete_24
                            )

                        val bitmap = drawableToBitmap(drawable!!, itemHeight)

                        val fl = (itemLeftPoint).toFloat() + marginLBtn
                        val ri = (itemRightPoint - bitmap.width).toFloat() + marginRBtn

                        paint.color = ContextCompat.getColor(applicationContext, R.color.redCard)

                        c.drawRoundRect(
                            itemLeftPoint.toFloat(),
                            itemTopPoint.toFloat(),
                            itemRightPoint.toFloat(),
                            itemBottomPoint.toFloat(),
                            dpToPixel(20F, null),
                            dpToPixel(20F, null),
                            paint
                        )

                        if (dX > 0)
                            c.drawBitmap(
                                bitmap,
                                fl,
                                ((itemTopPoint.toFloat() + itemBottomPoint) - (itemHeight / 2)) / 2,
                                null
                            )
                        if (dX < 0)
                            c.drawBitmap(
                                bitmap,
                                ri,
                                ((itemTopPoint.toFloat() + itemBottomPoint) - (itemHeight / 2)) / 2,
                                null
                            )


                    } else {
                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rv)
    }

    private fun tempRemove(position: Int) {
        val read = shoppingListsDb.read()
        read.removeAt(position)
        rv.adapter?.notifyItemRemoved(position)
        rv.adapter = MyListAdapter(this@MainActivity, read)
    }

    private fun recoverTempRemoved(position: Int) {
        val read = shoppingListsDb.read()
        rv.adapter = MyListAdapter(this@MainActivity, read)
        rv.adapter?.notifyItemChanged(position)
    }

    private fun refreshRv(position: Int?, inserted: Boolean) {
        rv.itemAnimator = null
        rv.adapter = MyListAdapter(
            this@MainActivity,
            shoppingListsDb.read()
        )
        if (inserted) {
            rv.adapter?.notifyItemInserted(position!!)
        } else {
            rv.adapter?.notifyItemRemoved(position!!)
        }
    }

    fun drawableToBitmap(drawable: Drawable, itemHeight: Int): Bitmap {
        drawable.setTint(resources.getColor(R.color.colorOnPrimary, null))
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            itemHeight / 2,
            itemHeight / 2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    @Suppress("SameParameterValue")
    private fun dpToPixel(px: Float, context: Context?): Float {
        return if (context != null) {
            val res = context.resources
            val mtrix = res.displayMetrics
            px / (mtrix.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        } else {
            val mtrix = Resources.getSystem().displayMetrics
            px * (mtrix.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }

    private fun addListDialog() {
        val bsd = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        bsd.setContentView(R.layout.add_list_bsd_layout)

        val card: CardView? = bsd.findViewById(R.id.addListBsd)
        card?.setBackgroundResource(R.drawable.bsd_bg)
        val closeBtn: ImageButton? = bsd.findViewById(R.id.closeAddListBtn)
        val listNameEt: TextInputEditText? = bsd.findViewById(R.id.listNameEt)
        val createBtn: FloatingActionButton? = bsd.findViewById(R.id.createListBtn)
        val commonEmojisRv: RecyclerView? = bsd.findViewById(R.id.commonEmojisRv)
        val showCategory: TextView? = bsd.findViewById(R.id.showCategoryBtn)
        val categoryArrow: ImageView? = bsd.findViewById(R.id.categoryArrow)

        card?.setOnClickListener {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
        closeBtn?.setOnClickListener { bsd.dismiss() }
        createBtn?.setOnClickListener { addListToDatabase(listNameEt, bsd) }
        showCategory?.setOnClickListener { showHideCategory(commonEmojisRv, showCategory, categoryArrow) }
        viewCommonEmojis(commonEmojisRv)
        bsd.show()
    }

    private fun showHideCategory(rv: RecyclerView?, tv: TextView?, categoryArrow: ImageView?) {
        val arrDown = AppCompatResources.getDrawable(this, R.drawable.ic_round_arrow_drop_down_24)
        val arrUp = AppCompatResources.getDrawable(this, R.drawable.ic_round_arrow_drop_up_24)

        if (rv?.visibility == View.GONE){
            rv.visibility = View.VISIBLE
            tv?.text = resources.getString(R.string.hide_category)
            categoryArrow?.setImageDrawable(arrUp)
        }else{
            rv?.visibility = View.GONE
            tv?.text = resources.getString(R.string.show_category)
            categoryArrow?.setImageDrawable(arrDown)
        }
    }

    private fun viewCommonEmojis(commonEmojisRv: RecyclerView?) {
        EmojiManager.install(TwitterEmojiProvider())
        val commonEmojiList = InitialCategoryPicks.epEmojiNameAndCodeList()
        commonEmojisRv?.setHasFixedSize(true)
        commonEmojisRv?.layoutManager = GridLayoutManager(
            this,
            3,
            LinearLayoutManager.VERTICAL,
            false)

        val adapter = CommonEmojisAdapter(this, commonEmojiList)
        commonEmojisRv?.adapter = adapter
    }



    private fun addListToDatabase(listName: TextInputEditText?, bsd: BottomSheetDialog) {
        if (!listName?.text.isNullOrBlank()) {
            val inserted =
                shoppingListsDb.insert(ShoppingListModel(-1, listName?.text.toString(), getDate()))
            if (inserted) {
                refreshRv(listAdapter.itemCount, inserted)
                bsd.dismiss()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Failed to create ${listName?.text.toString()}! please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val inserted = shoppingListsDb.insert(
                ShoppingListModel(
                    -1,
                    resources.getString(R.string.new_list),
                    getDate()
                )
            )
            if (inserted) {
                refreshRv(listAdapter.itemCount, inserted)
                bsd.dismiss()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Failed to create list! please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun editMode(context: Context, isEditMode: Boolean): Boolean {
        val editDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_round_edit_24)
        val addDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_add)
        if (isEditMode)
            addList.setImageDrawable(editDrawable)
        else
            addList.setImageDrawable(addDrawable)
        return isEditMode
    }

    private fun premiumAccountDialog() = Jumper(this).jumpTo(PremiumAccountActivity())

    private fun mainMenu() {
        bsd.setContentView(R.layout.main_menu_layout)

        val card: CardView? = bsd.findViewById(R.id.bsdCard)
        card?.setBackgroundResource(R.drawable.bsd_bg)

        val menuRv: RecyclerView? = bsd.findViewById(R.id.mainMenuRv)
        menuRv?.setHasFixedSize(true)
        menuRv?.layoutManager = LinearLayoutManager(this)
        menuRv?.adapter = adapter

        bsd.show()
    }

    fun closeBottomSheetDialog() = bsd.dismiss()

    private fun addCard() = Jumper(this).jumpTo(CardScannerActivity())

    private val registeredCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (camera.grantAccess()) {
            ProductScanner(
                this,
                productScannerResultLauncher,
                settingsDb.findBools(resources.getString(R.string.enable_disable_beep)) > 0
            ).barcodeScanner()
        } else {
            Toast.makeText(this@MainActivity, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    private val productScannerResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val intentResult = scanContracts.parseResult(it.resultCode, it.data)
        if (intentResult.contents != null) {
            val isExists = blacklistDb.isCodeExists(intentResult.contents)
            if (isExists) {
                val isNeverBuy = blacklistDb.getStatus(intentResult.contents)
                warningDialog(intentResult.contents, isNeverBuy)

            } else {
                productInfoDialog(intentResult)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun productInfoDialog(intentResult: ScanIntentResult) {
        val builder = AlertDialog.Builder(this)
        val v = LayoutInflater.from(this).inflate(R.layout.product_info_dialog, null, false)
        builder.setView(v)
        val closeBtn: ImageButton = v.findViewById(R.id.closeInfoDialogBtn)
        val codeTv: TextView = v.findViewById(R.id.resultCodeTv)
        val typeTv: TextView = v.findViewById(R.id.resultTypeTv)
        val countryTv: TextView = v.findViewById(R.id.resultCountryTv)
        val rescan: ImageButton = v.findViewById(R.id.scanAgainBtn)
        val revealBoycottBtn: Button = v.findViewById(R.id.revealBoycottBtn)
        val boycottBtn: FloatingActionButton = v.findViewById(R.id.boycottBtn)
        val infoBtn: Button = v.findViewById(R.id.infoBtn)
        val nameEtl: LinearLayout = v.findViewById(R.id.manufactNameDialog)
        val nameEt: TextInputEditText = v.findViewById(R.id.dialogManufactNameEt)
        val dialog = builder.create()
        val country = Find.country(intentResult.contents.toString().substring(0, 3))

        codeTv.text = "Code: ${intentResult.contents}"
        typeTv.text = "Barcode type: ${intentResult.formatName}"
        countryTv.text = "Country: $country"

        if (country.contains(AppsConstants.noCountry)) {
            boycottBtn.isEnabled = false
        }

        closeBtn.setOnClickListener { dialog.dismiss() }
        rescan.setOnClickListener { dialog.dismiss(); registeredCamera.launch(Manifest.permission.CAMERA) }
        revealBoycottBtn.setOnClickListener {
            if (nameEtl.visibility == View.VISIBLE) {
                nameEtl.visibility = View.GONE
            } else {
                nameEtl.visibility = View.VISIBLE
            }
        }
        boycottBtn.setOnClickListener { boycottManufacturer(intentResult.contents, nameEt) }
        infoBtn.setOnClickListener { googleSearchIntent1(intentResult.contents) }

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                resources.getColor(
                    android.R.color.transparent,
                    null
                )
            )
        )
        dialog.show()
    }

    @Suppress("unused")
    private fun googleSearchIntent(code: String?) {
        val webSearchIntent = Intent(Intent.ACTION_WEB_SEARCH)
        webSearchIntent.putExtra(SearchManager.QUERY, code)
        startActivity(webSearchIntent)
    }

    private fun googleSearchIntent1(code: String?) {
        val escapedQuery = URLEncoder.encode(code, "UTF-8")
        val uri = Uri.parse(uriString + escapedQuery)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun boycottManufacturer(code: String, nameEt: TextInputEditText) {
        if (!nameEt.text.isNullOrEmpty()) {
            val name = nameEt.text.toString().trim()
            addManufacturerToLocalDb(name, code)
        } else {
            Toast.makeText(applicationContext, "Name cannot be empty!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addManufacturerToLocalDb(name: String, code: String) {
        val isValid = InputUtil.validateManufactureInputInfo(name, code)
        val isExists = BlacklistDb(this).isManufacturerExists(code)
        val currentSize =
            ManufacturerListAdapter(this, BlacklistDb(this).readManufacturer()).itemCount
        if (!isExists) {
            if (isValid) {
                if (!premiumAccountCheck) {
                    if (currentSize < AppsConstants.freeAccountManufactListSize) {
                        val inserted: Boolean =
                            BlacklistDb(this).insert(
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
                            Toast.makeText(
                                applicationContext,
                                "$name blacklisted successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "$name could not be added, please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Blacklist manufacturer limit reached",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "check your inputs and try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "this code already exists",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun warningDialog(code: String, isNeverBuy: Boolean) {
        val isNeverBuyStrokeColor = resources.getColor(R.color.redCard, null)
        val isNotifyMeStrokeColor = resources.getColor(R.color.colorPrimary, null)
        val idPosition = 0
        val typePosition = 1
        val namePosition = 2
        val builder = AlertDialog.Builder(this)
        val v = LayoutInflater.from(this).inflate(R.layout.warning_dialog_layout, null, false)
        builder.setView(v)
        val warningCard: MaterialCardView = v.findViewById(R.id.warningCard)
        val title: TextView = v.findViewById(R.id.blockedTitleTv)
        val closeDialogBtn: ImageButton = v.findViewById(R.id.blacklistedCloseInfoDialogBtn)
        val codeTv: TextView = v.findViewById(R.id.blacklistedCodeTv)
        val typeTv: TextView = v.findViewById(R.id.blacklistedTypeTv)
        val nameTv: TextView = v.findViewById(R.id.blacklistedNameTv)
        val statusTv: TextView = v.findViewById(R.id.blacklistedStatusTv)
        val rescan: ImageButton = v.findViewById(R.id.blacklistedScanAgainBtn)
        val stopBoycottBtn: Button = v.findViewById(R.id.stopBoycottBtn)
        val infoBtn: Button = v.findViewById(R.id.blacklistedInfoBtn)
        val dialog = builder.create()

        if (isNeverBuy) {
            warningCard.strokeColor = isNeverBuyStrokeColor
            title.setTextColor(isNeverBuyStrokeColor)
        } else {
            warningCard.strokeColor = isNotifyMeStrokeColor
            title.setTextColor(isNotifyMeStrokeColor)
        }

        closeDialogBtn.setOnClickListener { dialog.dismiss() }
        codeTv.text = "Code: $code"
        typeTv.text = "Type: ${blacklistDb.getBlacklistedInfo(code, typePosition)}"
        nameTv.text = "Name: ${blacklistDb.getBlacklistedInfo(code, namePosition)}"
        statusTv.text = "Status: ${if (isNeverBuy) "Never Buy" else "Notify me"}"
        rescan.setOnClickListener { dialog.dismiss(); registeredCamera.launch(Manifest.permission.CAMERA) }
        stopBoycottBtn.setOnClickListener {
            dialog.dismiss()
            val builder1 = AlertDialog.Builder(this)
            builder1.setTitle("Unblock ${blacklistDb.getBlacklistedInfo(code, namePosition)}")
                .setMessage(
                    "Do you like to stop boycotting products from " +
                            "${blacklistDb.getBlacklistedInfo(code, namePosition)}?"
                ).setPositiveButton("Yes") { _, _ ->
                    blacklistDb.delete(
                        BlacklistModel(
                            blacklistDb.getBlacklistedInfo(
                                code,
                                idPosition
                            ).toInt(), "", "", null, "", false
                        )
                    )
                }.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                .show()
        }
        infoBtn.setOnClickListener { googleSearchIntent1(code) }
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                resources.getColor(
                    android.R.color.transparent,
                    null
                )
            )
        )
        dialog.show()

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

        msg.text = getString(R.string.cam_permission_message)
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

    private fun underline(tv: String): SpannableString {
        val sp = SpannableString(tv)
        sp.setSpan(UnderlineSpan(), 0, tv.length, 0)
        return sp
    }

    private fun getDate(): String {
        val simpleDateFormat = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
        val date = Calendar.getInstance().time
        return simpleDateFormat.format(date)
    }

    private fun deletedAt(): String {
        val simpleDateFormat = SimpleDateFormat(AppsConstants.pattern, Locale.getDefault())
        val date = Calendar.getInstance().time
        return simpleDateFormat.format(date)
    }

    private fun removeAt(): String {
        val deletedAt = deletedAt()

        val simpleDateFormat = SimpleDateFormat(AppsConstants.pattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormat.parse(deletedAt)!!
        val permanentRemoveDuration = settingsDb.getChoices(AppsConstants.trash)
        val duration = resources.getStringArray(R.array.duration)

        return when (permanentRemoveDuration) {
            duration[0] -> { // day
                calendar.add(Calendar.DATE, 1)
                simpleDateFormat.format(calendar.time)
            }
            duration[1] -> { // week
                calendar.add(Calendar.DATE, 7)
                simpleDateFormat.format(calendar.time)
            }
            duration[2] -> { // month
                calendar.add(Calendar.MONTH, 1)
                simpleDateFormat.format(calendar.time)
            }
            duration[3] -> { // 6 months
                calendar.add(Calendar.MONTH, 6)
                simpleDateFormat.format(calendar.time)
            }
            duration[4] -> { // year
                calendar.add(Calendar.YEAR, 1)
                simpleDateFormat.format(calendar.time)
            }
            duration[5] -> { // forever
                ""
            }
            duration[6] -> { // never
                deletedAt
            }
            else -> "-1"
        }
    }

    private fun getCurrentDateAndTime(): String {
        val simpleDateFormat = SimpleDateFormat(AppsConstants.pattern, Locale.getDefault())
        val date = Calendar.getInstance().time
        return simpleDateFormat.format(date)
    }

    private fun resumeRvState() {
        rv.itemAnimator = null
        rv.adapter = MyListAdapter(
            this@MainActivity,
            shoppingListsDb.read()
        )
    }

    override fun onResume() {
        super.onResume()
        resumeRvState()
        editMode(this, false)
        deletedListDb.removeIfDue(getCurrentDateAndTime())
        if (SettingsDb(this).getChoices(AppsConstants.trash) == AppsConstants.noDataInDb) {
            SettingsDb(this).insert(SettingsModel(-1, "Shopping list", "Trash", null, "day"))
        }
    }
}