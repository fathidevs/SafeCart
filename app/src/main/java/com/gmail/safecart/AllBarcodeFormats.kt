package com.gmail.safecart

import com.google.zxing.BarcodeFormat

class AllBarcodeFormats {

    companion object{
        val formats: Collection<BarcodeFormat> = listOf(
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_93,

            BarcodeFormat.UPC_E,
            BarcodeFormat.UPC_EAN_EXTENSION,

            BarcodeFormat.QR_CODE,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.AZTEC,
            BarcodeFormat.CODABAR,
            BarcodeFormat.ITF,
            BarcodeFormat.MAXICODE,
            BarcodeFormat.PDF_417,

            BarcodeFormat.RSS_14,
            BarcodeFormat.RSS_EXPANDED,
            BarcodeFormat.UPC_A,
        )
    }
}