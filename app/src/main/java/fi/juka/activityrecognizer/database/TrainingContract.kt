package fi.juka.activityrecognizer.database

import android.provider.BaseColumns

object TrainingContract {
    object TrainingDataEntry {
        const val TABLE_NAME = "training_data"
        const val COLUMN_NAME_X_AXIS = "x_axis"
        const val COLUMN_NAME_Y_AXIS = "y_axis"
        const val COLUMN_NAME_Z_AXIS = "z_axis"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
        const val COLUMN_NAME_ACTIVITY_ID = "activity_id"
        const val COLUMN_NAME_TOTAL_ACCELERATION = "total_acceleration"

        const val SQL_CREATE_TABLE = "CREATE TABLE ${TrainingDataEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${TrainingDataEntry.COLUMN_NAME_TIMESTAMP} LONG DEFAULT 0, " +
                "${TrainingDataEntry.COLUMN_NAME_X_AXIS} REAL, " +
                "${TrainingDataEntry.COLUMN_NAME_Y_AXIS} REAL, " +
                "${TrainingDataEntry.COLUMN_NAME_Z_AXIS} REAL, " +
                "${TrainingDataEntry.COLUMN_NAME_TOTAL_ACCELERATION} REAL DEFAULT 0, " +
                "${TrainingDataEntry.COLUMN_NAME_ACTIVITY_ID} REAL, " +
                "FOREIGN KEY(${TrainingDataEntry.COLUMN_NAME_ACTIVITY_ID}) REFERENCES " +
                "${ActivityEntry.TABLE_NAME}(${BaseColumns._ID}))"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${TrainingDataEntry.TABLE_NAME}"
    }

    object ActivityEntry {
        const val TABLE_NAME = "activity"
        const val COLUMN_NAME_ACTIVITY = "activity_name"
        const val COLUMN_NAME_SAMPLES = "samples"
        const val COLUMN_NAME_AVERAGE_SPEED = "average_speed"

        const val SQL_CREATE_TABLE = "CREATE TABLE ${ActivityEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${ActivityEntry.COLUMN_NAME_ACTIVITY} TEXT, " +
                "${ActivityEntry.COLUMN_NAME_SAMPLES} INTEGER DEFAULT 0, " +
                "${ActivityEntry.COLUMN_NAME_AVERAGE_SPEED} DOUBLE DEFAULT 0)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${ActivityEntry.TABLE_NAME}"
    }
}