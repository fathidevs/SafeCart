package com.gmail.safecart.lists

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DeletedListDb(context: Context) :
    SQLiteOpenHelper(context, databaseName, null, databaseVersion) {

    companion object {
        private const val databaseName = "deleted_list.db"
        private const val databaseVersion = 1

        private const val tableName = "deleted_list"
        private const val keyId = "_id"
        private const val listIdCol = "listId"
        private const val listNameCol = "listName"
        private const val listPosCol = "listPosition"
        private const val deletedAtCol = "deletedAt"
        private const val removeAtCol = "removeAt"

        private const val createStatement = "CREATE TABLE $tableName " +
                "($keyId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$listIdCol INTEGER, " +
                "$listNameCol TEXT, " +
                "$listPosCol INTEGER, " +
                "$deletedAtCol TEXT, " +
                "$removeAtCol TEXT)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createStatement)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        //if (oldV < 2) { db?.execSQL(newColumnAdded) }
    }
    // read
    fun read(): ArrayList<DeletedListModel>{
        val returnList = ArrayList<DeletedListModel>()
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(0)
                val listId = cursor.getInt(1)
                val name = cursor.getString(2)
                val position = cursor.getInt(3)
                val deletedAt = cursor.getString(4)
                val removeAt = cursor.getString(5)
                returnList.add(DeletedListModel(id,listId,name,position,deletedAt,removeAt))
            }while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return returnList
    }
    // insert
    fun insert(model: DeletedListModel): Boolean {
        val size = deletedListSize()
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(listIdCol, model.deletedListId)
        cv.put(listNameCol, model.deletedListName)
        cv.put(listPosCol, model.deletedListPosition)
        cv.put(deletedAtCol, model.deletedAt)
        cv.put(removeAtCol, model.removeAt)

        val insert = db.insert(tableName, null, cv)

        return insert > size
    }

    fun getDeletedAt(): ArrayList<String> {
        val returnDeletedAt = ArrayList<String>()
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val deletedAt = cursor.getString(4)
                returnDeletedAt.add(deletedAt)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return returnDeletedAt
    }

    private fun deletedListSize(): Long {
        var returnSize: Long = 0
        val db = this.readableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()) {
            do {
                returnSize++
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return if (returnSize == 0L) -1 else returnSize
    }
    fun delete(model: DeletedListModel): Boolean{
        val db = this.writableDatabase
        val delete = db.delete(tableName, "$keyId=${model.id}", null)
        return delete > 0
    }
    fun removeIfDue(currentDateAndTime: String) {
        val dbr = this.readableDatabase
        val dbw = this.writableDatabase
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = dbr.rawQuery(stringQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val listId = cursor.getInt(0)
                val removeAtDate = cursor.getString(5)

                if (currentDateAndTime.toLong() >= removeAtDate.toLong()) {
                    dbw.delete(tableName, "$keyId=$listId", null)
                }
            } while (cursor.moveToNext())
        }
        dbr.close()
        cursor.close()
    }

    fun editRemovingDates(listOfNewRemovingDate: ArrayList<String>) {
        val db = this.writableDatabase
        val cv = ContentValues()
        val stringQuery = "SELECT * FROM $tableName"
        val cursor = db.rawQuery(stringQuery, null)
        var index = 0
        if (cursor.moveToFirst()) {
            do {
                val deletedListId = cursor.getInt(1)
                val newRemoveAtDate = listOfNewRemovingDate[index]
                cv.put(removeAtCol, newRemoveAtDate)
                db.update(tableName, cv, "$listIdCol = ?", arrayOf(deletedListId.toString()))
                index++
            } while (cursor.moveToNext())
        }

        //val edit = db.update(tableName, cv, "$listIdCol = ?", arrayOf(model.deletedListId.toString()))

        db.close()
        cursor.close()
    }
}