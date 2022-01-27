package com.gmail.safecart.lists

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ShoppingListsDb(context: Context):
SQLiteOpenHelper(context, databaseName, null, databaseVersion){

    companion object{
        private const val databaseName = "shopping_lists.db"
        private const val databaseVersion = 1

        private const val tableName = "lists_table"
        private const val keyId = "_id"
        private const val listNameCol = "listName"
        private const val createdAtCol = "created_at"

        private const val createStatement = "CREATE TABLE $tableName ($keyId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$listNameCol TEXT," +
                "$createdAtCol TEXT)"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createStatement)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        //if (oldV < 2) { db?.execSQL(newColumnAdded) }
    }

    // insert
    fun insert(model: ShoppingListModel): Boolean{
        val db = readableDatabase
        val cv = ContentValues()

        cv.put(listNameCol, model.listName)
        cv.put(createdAtCol, model.createdAt)

        val insert: Long = db.insert(tableName, null, cv)

        return insert > -1
    }
    // read
    fun read(): ArrayList<ShoppingListModel> {
        val returnList = ArrayList<ShoppingListModel>()
        val db = readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val createdAt = cursor.getString(2)
                returnList.add(ShoppingListModel(id, name, createdAt))
            }while(cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnList
    }
    // edit
    // delete
    fun delete(model: ShoppingListModel): Boolean{
        val db = this.writableDatabase
        val delete = db.delete(tableName, "$keyId=${model.id}", null)
        return delete > 0
    }
}