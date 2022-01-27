package com.gmail.safecart.settings

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.gmail.safecart.AppsConstants

class SettingsDb(context: Context):
    SQLiteOpenHelper(context, databaseName, null, databaseVersion) {

    companion object{
        private const val databaseName = "settings.db"
        private const val databaseVersion = 1
        private const val keyId = "_id"
        private const val settingsTable = "settings_table"

        const val settingsCategoryCol = "settings_category"
        const val settingsTitleCol = "settings_title"
        const val settingsSwitchCol = "settings_switch"
        const val settingsMultiChoiceCol = "choice_value"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createStatement = "CREATE TABLE $settingsTable ($keyId INTEGER PRIMARY KEY AUTOINCREMENT, $settingsCategoryCol TEXT, $settingsTitleCol TEXT, $settingsSwitchCol BOOL, $settingsMultiChoiceCol TEXT)"
        db?.execSQL(createStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldV: Int, newV: Int) {
        //if (oldV < 1){ }
    }
    fun insert(model: SettingsModel): Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(settingsCategoryCol, model.category)
        cv.put(settingsTitleCol, model.title)
        cv.put(settingsSwitchCol, model.switch)
        cv.put(settingsMultiChoiceCol, model.choice)

        val insert = db.insert(settingsTable, null, cv)

        return insert != -1L
    }
    fun getTitles(): ArrayList<String>{
        val db = this.readableDatabase
        val returnTitles = ArrayList<String>()
        val stringQuery = "SELECT * FROM $settingsTable"
        val cursor = db.rawQuery(stringQuery, null)

        if (cursor.moveToFirst()){
            do {

                val title = cursor.getString(2)
                returnTitles.add(title)
            }while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnTitles
    }
    fun getChoices(title: String): String{
        val db = this.readableDatabase
        var returnChoices = AppsConstants.noDataInDb
        val stringQuery = "SELECT * FROM $settingsTable"
        val cursor = db.rawQuery(stringQuery, null)

        if (cursor.moveToFirst()){
            do {
                if (title == cursor.getString(2)) { returnChoices = cursor.getString(4) }
            }while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnChoices
    }
    fun edit(model: SettingsModel): Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(settingsSwitchCol, if(model.switch == true) 1 else 0)
        cv.put(settingsMultiChoiceCol, model.choice)

        val update = db.update(settingsTable, cv, "$settingsTitleCol = ?", arrayOf(model.title))
        db.close()
        return update > 0
    }
    fun findBools(title: String): Int {
        var returnList: Int = -1
        val db = this.readableDatabase
        val queryString = "SELECT * FROM $settingsTable"
        val cursor = db.rawQuery(queryString, null)

        if (cursor.moveToFirst()){
            do {
                if (title == cursor.getString(2)) {
                    val getSwitch = cursor.getInt(3)
                    returnList=getSwitch
                }
            }while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnList
    }
}