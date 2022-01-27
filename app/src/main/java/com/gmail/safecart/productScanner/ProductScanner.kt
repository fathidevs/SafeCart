package com.gmail.safecart.productScanner

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.gmail.safecart.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ProductScanner(
    private val context: Context,
    private val activityResultLauncher: ActivityResultLauncher<Intent>,
    private val b: Boolean
) {
    private val scanOptions: ScanOptions = ScanOptions()
    private val scanContracts: ScanContract = ScanContract()

    fun barcodeScanner(){
        scanOptions.setDesiredBarcodeFormats(ScanOptions.PRODUCT_CODE_TYPES)
        scanOptions.setPrompt(context.getString(R.string.scanner_prompt))
        scanOptions.setBeepEnabled(b)
        scanOptions.setOrientationLocked(true)
        scanOptions.setBarcodeImageEnabled(true)
        scanOptions.captureActivity = Capture::class.java
        activityResultLauncher.launch(scanContracts.createIntent(context, scanOptions))

    }

}