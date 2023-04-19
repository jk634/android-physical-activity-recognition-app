package fi.juka.activityrecognizer.database

import android.provider.BaseColumns

object TrainingContract {
    object TrainingDataEntry {
        const val TABLE_NAME = "trainingData"
        const val COLUMN_NAME_X_AXIS = "x_axis"
        const val COLUMN_NAME_Y_AXIS = "y_axis"
        const val COLUMN_NAME_Z_AXIS = "z_axis"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
        const val COLUMN_NAME_ACTIVITY_ID = "activity_id" //

        const val SQL_CREATE_TABLE = "CREATE TABLE ${TrainingDataEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${TrainingDataEntry.COLUMN_NAME_TIMESTAMP} INTEGER, " +
                "${TrainingDataEntry.COLUMN_NAME_X_AXIS} REAL, " +
                "${TrainingDataEntry.COLUMN_NAME_Y_AXIS} REAL, " +
                "${TrainingDataEntry.COLUMN_NAME_Z_AXIS} REAL, " +
                "${TrainingDataEntry.COLUMN_NAME_ACTIVITY_ID} REAL, " +
                "FOREIGN KEY(${TrainingDataEntry.COLUMN_NAME_ACTIVITY_ID}) REFERENCES " +
                "${ActivityEntry.TABLE_NAME}(${BaseColumns._ID}))"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${TrainingDataEntry.TABLE_NAME}"
    }

    object ActivityEntry {
        const val TABLE_NAME = "activity"
        const val COLUMN_NAME_ACTIVITY = "activity_name"

        const val SQL_CREATE_TABLE = "CREATE TABLE ${ActivityEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${ActivityEntry.COLUMN_NAME_ACTIVITY} TEXT)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${ActivityEntry.TABLE_NAME}"
    }
}