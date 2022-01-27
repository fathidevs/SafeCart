package com.gmail.safecart.loyaltyCard

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CardsDb(context: Context):
    SQLiteOpenHelper(context, databaseName, null, databaseVersion) {

    companion object{
        private const val databaseName = "cards.db"
        private const val databaseVersion = 1
        private const val keyId = "_id"
        private const val cardsTable = "cards_table"
        private const val cardNameCol = "card_name"
        private const val cardCodeCol = "card_code"
        private const val cardCodeFormatCol = "card_code_format"
        private const val cardColorCol = "card_color_position"

        // create statement
        private const val createDatabase = "CREATE TABLE $cardsTable ($keyId INTEGER PRIMARY KEY AUTOINCREMENT, $cardNameCol TEXT, $cardCodeCol INTEGER, $cardCodeFormatCol TEXT, $cardColorCol INTEGER)"
        // upgrades
        //private const val newColumnAdded = "ALTER TABLE $cardsTable ADD COLUMN $cardCodeFormatCol TEXT" // V2
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createStatement = createDatabase
        db?.execSQL(createStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldV: Int, p2: Int) {
        //if (oldV < 2){ db?.execSQL(newColumnAdded) }
    }

    // insert
    fun insert(model: CardsModel): Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(cardNameCol, model.cardName)
        cv.put(cardCodeCol, model.cardCode)
        cv.put(cardCodeFormatCol, model.cardCodeFormat)
        cv.put(cardColorCol, model.cardColor) // color position in Array<Int> is saved

        val insert = db.insert(cardsTable, null, cv)
        return insert > -1
    }
    // read
    fun read(): ArrayList<CardsModel>{
        val returnList = ArrayList<CardsModel>()
        val queryString = "SELECT * FROM $cardsTable"
        val db = this.readableDatabase
        val cursor = db.rawQuery(queryString, null)
        if (cursor.moveToFirst()){
            do {
                val id =  cursor.getInt(0)
                val name = cursor.getString(1)
                val code = cursor.getString(2)
                val codeFormat = cursor.getString(3)
                val color = cursor.getInt(4)
                returnList.add(CardsModel(id, name, code, color, codeFormat))
            }while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnList
    }

    fun getCardCodes(): ArrayList<String>{
        val returnList = ArrayList<String>()
        val stringQuery = "SELECT * FROM $cardsTable"
        val db = this.readableDatabase
        val cursor = db.rawQuery(stringQuery, null)

        if (cursor.moveToFirst()){
            do {
                val code = cursor.getString(2)
                returnList.add(code)
            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return returnList
    }
    // delete
    fun delete(model: CardsModel): Boolean {
        val db = this.writableDatabase
        val delete = db.delete(cardsTable, "$keyId=${model.id}", null)
        return delete > 0
    }
    // edit
    fun edit(model: CardsModel): Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.apply {
            put(cardNameCol, model.cardName)
            put(cardCodeCol, model.cardCode)
            put(cardColorCol, model.cardColor)
            put(cardCodeFormatCol, model.cardCodeFormat)
        }

        val update = db.update(cardsTable, cv, "$keyId=${model.id}", arrayOf())

        return update > 0
    }
}