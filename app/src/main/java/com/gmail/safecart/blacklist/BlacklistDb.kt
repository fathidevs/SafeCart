package com.gmail.safecart.blacklist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.gmail.safecart.AppsConstants

class BlacklistDb(context: Context) :
    SQLiteOpenHelper(context, databaseName, null, databaseVersion) {
    companion object {
        private const val databaseName = "blacklist.db"
        private const val databaseVersion = 1

        private const val tableName = "blacklist_table"
        private const val keyId = "_id"
        private const val typeCol = "type"
        private const val nameCol = "name"
        private const val prefixCol = "prefix"
        private const val statusCol = "status"

        private const val countryPositionCol = "country_position"
        private const val createStatement = "CREATE TABLE $tableName " +
                "($keyId INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $typeCol TEXT," +
                " $nameCol TEXT," +
                " $countryPositionCol INTEGER," +
                " $prefixCol TEXT," +
                " $statusCol BOOL)"
        //private const val newColumnAdded = "ALTER TABLE $tableName ADD COLUMN $countryPositionCol INTEGER" // V2
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldV: Int, newV: Int) {
        //if (oldV < 2) { db?.execSQL(newColumnAdded) }
    }

    // insert
    fun insert(model: BlacklistModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(typeCol, model.type)
        cv.put(nameCol, model.name)
        cv.put(countryPositionCol, model.cPosition)
        cv.put(prefixCol, model.prefix)
        cv.put(statusCol, model.status)

        val insert = db.insert(tableName, null, cv)

        return insert > -1
    }

    fun readManufacturer(): ArrayList<BlacklistModel> {
        val returnList = ArrayList<BlacklistModel>()
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(1) == AppsConstants.blackListTypeManufacturer) {
                    val id = cursor.getInt(0)
                    val type = cursor.getString(1)
                    val name = cursor.getString(2)
                    val cPosition = cursor.getInt(3)
                    val prefix = cursor.getString(4)
                    val status = cursor.getInt(5) > 0
                    returnList.add(BlacklistModel(id, type, name, cPosition, prefix, status))
                }
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnList
    }

    fun getCountryPosition(country: String): Int {
        var returnPosition: Int = -1
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()) {
            do {
                if (country == cursor.getString(2))
                    returnPosition = cursor.getInt(3)
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return if (returnPosition > -1) returnPosition else -1
    }
    fun getCountryId(country: String): Int {
        var returnPosition: Int = -1
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()) {
            do {
                if (country == cursor.getString(2))
                    returnPosition = cursor.getInt(0)
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return if (returnPosition > -1) returnPosition else -1
    }

    fun isCountryExists(country: String): Boolean {
        var returnPosition = false
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()) {
            do {
                if (country == cursor.getString(2))
                    returnPosition = true
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnPosition
    }

    fun isManufacturerExists(manufactCode: String): Boolean {
        var returnManufactCode = "null"
        val db = this.readableDatabase
        val stringQuery = " SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val code = cursor.getString(4)
                if (code == manufactCode) {
                    returnManufactCode = code
                }
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnManufactCode != "null" && returnManufactCode == manufactCode
    }

    fun countryListSize(): Int {
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        var cntr = 0
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(1) == AppsConstants.blackListTypeCountry)
                    cntr++
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return if (cntr > -1) cntr else -1
    }

    fun delete(model: BlacklistModel): Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(keyId, model.id)
        val delete = db.delete(tableName, "$keyId=${model.id}", null)
        db.close()
        return delete > 0
    }

    fun getStatus(code: String): Boolean{
        var status = -1
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName WHERE $prefixCol=$code"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()){
            do {
                if (cursor.getString(4) == code){
                    status = cursor.getInt(5)
                }
            }while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return status > 0
    }
    fun isCodeExists(code: String): Boolean{
        var exists = false
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName WHERE $prefixCol=$code"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()){
            do {
                if (cursor.getString(4) == code){
                    exists = true
                }
            }while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return exists
    }

    fun getBlacklistedInfo(code: String, cursorPosition: Int): String{
        var info = "unavailable"
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName WHERE $prefixCol=$code"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()){
            do {
                if (cursor.getString(4) == code){
                    info = cursor.getString(cursorPosition)
                }
            }while(cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return info
    }
}