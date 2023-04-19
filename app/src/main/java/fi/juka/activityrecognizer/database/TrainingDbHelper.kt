package fi.juka.activityrecognizer.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TrainingDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TrainingContract.ActivityEntry.SQL_CREATE_TABLE)
        db.execSQL(TrainingContract.TrainingDataEntry.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(TrainingContract.TrainingDataEntry.SQL_DROP_TABLE)
        db.execSQL(TrainingContract.ActivityEntry.SQL_DROP_TABLE)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Training.db"
    }
}