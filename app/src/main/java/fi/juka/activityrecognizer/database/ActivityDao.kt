package fi.juka.activityrecognizer.database

import android.content.ContentValues
import android.provider.BaseColumns
import android.util.Log
import kotlin.math.sqrt

class ActivityDao(private val dbHelper: TrainingDbHelper) {

    private val activityEntry = TrainingContract.ActivityEntry
    private val trainingDataEntry = TrainingContract.TrainingDataEntry

    // Save the new Activity to the new row and return id
    fun saveActivityAndGetId(activityName: String): Long {
        val db = dbHelper.writableDatabase
        val formattedActivityName = activityName.lowercase().replace("\\s".toRegex(), "_")

        val values = ContentValues().apply {
            put(activityEntry.COLUMN_NAME_ACTIVITY, formattedActivityName)
        }
        val id = db.insert(activityEntry.TABLE_NAME, null, values)

        db.close()

        return id
    }

    fun incrementSampleCount(activityId: Long) {
        val db = dbHelper.writableDatabase

        val selectQuery = "SELECT ${activityEntry.COLUMN_NAME_SAMPLES} FROM " +
                "${activityEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        var selectionArgs = arrayOf(activityId.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)
        cursor.moveToFirst()
        val oldSampleCount = cursor.getInt(cursor.getColumnIndexOrThrow("samples"))


        val values = ContentValues().apply {
            put(activityEntry.COLUMN_NAME_SAMPLES, oldSampleCount + 1)
        }

        val selection = "${BaseColumns._ID} = ?"
        selectionArgs = arrayOf(activityId.toString())

        db.update(
            activityEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        db.close()
    }

    fun getActivitiesList(): MutableList<Triple<Long, String, Double>> {

        val activitiesList = mutableListOf<Triple<Long, String, Double>>()

        // Fetch all activities from the database
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM activity", null)

        // Add every activity to the list
        while (cursor.moveToNext()) {
            val activityId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
            val activityName = cursor.getString(cursor.getColumnIndexOrThrow("activity_name"))
            val activityAverageSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow("average_speed"))
            activitiesList.add(Triple(activityId, activityName, activityAverageSpeed))
        }

        cursor.close()
        db.close()

        return activitiesList
    }

    fun getSampleCount(activityId: Long): Int {
        val db = dbHelper.readableDatabase

        val selectQuery = "SELECT ${activityEntry.COLUMN_NAME_SAMPLES} FROM " +
                "${activityEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        var selectionArgs = arrayOf(activityId.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        val samples: Int = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            0
        }

        cursor.close()
        db.close()

        return samples
    }


    fun getAllTrainingDataForActivity(activityId: Long): MutableList<TrainingData> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${trainingDataEntry.TABLE_NAME} WHERE " +
                "${trainingDataEntry.COLUMN_NAME_ACTIVITY_ID} = ?", arrayOf(activityId.toString()))

        val trainingDataList = mutableListOf<TrainingData>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val x_axis = cursor.getFloat(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_X_AXIS))
            val y_axis = cursor.getFloat(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_Y_AXIS))
            val z_axis = cursor.getFloat(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_Z_AXIS))
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_TIMESTAMP))
            val activityId = cursor.getLong(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_ACTIVITY_ID))
            val total_acceleration = cursor.getFloat(cursor.getColumnIndexOrThrow(trainingDataEntry.COLUMN_NAME_TOTAL_ACCELERATION))

            val trainingData = TrainingData(id, x_axis, y_axis, z_axis, total_acceleration, timestamp, activityId)
            trainingDataList.add(trainingData)
        }

        cursor.close()
        db.close()

        for (x in trainingDataList) {
            Log.d("TRAINX", "${x.id} ${x.x_axis} ${x.y_axis} " +
                    "${x.z_axis} ${x.total_acceleration} ${x.timestamp} ${x.activityId}")
        }

        return trainingDataList
    }


    fun updateAverageSpeed(newSpeed: Double, activityId: Long) {

        Log.d("TEST", newSpeed.toString())

        val db = dbHelper.writableDatabase

        val selectQuery = "SELECT ${activityEntry.COLUMN_NAME_AVERAGE_SPEED} FROM " +
                "${activityEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(activityId.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)
        cursor.moveToFirst()
        val oldAverageSpeed = cursor.getLong(cursor.getColumnIndexOrThrow(activityEntry.COLUMN_NAME_AVERAGE_SPEED))

        val updatedSpeed = if (oldAverageSpeed != 0L) {
            (oldAverageSpeed + newSpeed) / 2
        } else {
            newSpeed
        }

        Log.d("Test", updatedSpeed.toString())

        val values = ContentValues().apply {
            put(activityEntry.COLUMN_NAME_AVERAGE_SPEED, updatedSpeed)
        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgsUpdate = arrayOf(activityId.toString())

        db.update(
            activityEntry.TABLE_NAME,
            values,
            selection,
            selectionArgsUpdate
        )

        db.close()
    }

    fun saveData(accelerations: MutableList<Pair<FloatArray, Long>>, activityId: Long) {

        lateinit var values: ContentValues
        val db = dbHelper.writableDatabase

        for (acc in accelerations) {
            val x = acc.first[0].toDouble()
            val y = acc.first[1].toDouble()
            val z = acc.first[2].toDouble()

            val totalAcc = sqrt(x*x + y*y + z*z)

            values = ContentValues().apply {
                put(trainingDataEntry.COLUMN_NAME_TIMESTAMP, acc.second)
                put(trainingDataEntry.COLUMN_NAME_X_AXIS,acc.first[0])
                put(trainingDataEntry.COLUMN_NAME_Y_AXIS,acc.first[1])
                put(trainingDataEntry.COLUMN_NAME_Z_AXIS, acc.first[2])
                put(trainingDataEntry.COLUMN_NAME_TOTAL_ACCELERATION, totalAcc)
                put(trainingDataEntry.COLUMN_NAME_ACTIVITY_ID, activityId)
            }
            if (values.size() > 0) {
                db.insert(trainingDataEntry.TABLE_NAME, null, values)
            }
        }
        db.close()
    }

    fun deleteActivity(activityId: Long) {
        val db = dbHelper.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(activityId.toString())
        db.delete(TrainingContract.ActivityEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
    }

    fun deleteSamplesForActivity(activityId: Long) {
        val db = dbHelper.writableDatabase
        val selection = "${TrainingContract.TrainingDataEntry.COLUMN_NAME_ACTIVITY_ID} = ?"
        val selectionArgs = arrayOf(activityId.toString())
        db.delete(TrainingContract.TrainingDataEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
    }
}