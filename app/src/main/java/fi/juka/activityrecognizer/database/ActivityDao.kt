package fi.juka.activityrecognizer.database

import android.content.ContentValues
import android.util.Log

class ActivityDao(private val dbHelper: TrainingDbHelper) {

    // Save the new Activity to the new row and return id
    fun saveActivityAndGetId(activityName: String): Long {
        val db = dbHelper.writableDatabase
        val formattedActivityName = activityName.lowercase().replace("\\s".toRegex(), "_")

        val values = ContentValues().apply {
            put(TrainingContract.ActivityEntry.COLUMN_NAME_ACTIVITY, formattedActivityName)
        }
        val id = db.insert(TrainingContract.ActivityEntry.TABLE_NAME, null, values)

        db.close()

        return id
    }

    fun getActivitiesList(): MutableList<Pair<Long, String>> {

        val activitiesList = mutableListOf<Pair<Long, String>>()

        // Fetch all activities from the database
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM activity", null)

        // Add every activity to the list
        while (cursor.moveToNext()) {
            val activityId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
            val activityName = cursor.getString(cursor.getColumnIndexOrThrow("activity_name"))
            activitiesList.add(Pair(activityId, activityName))
        }

        cursor.close()
        db.close()

        return activitiesList
    }

    /*fun getAllTrainingDataForActivity(activityId: Long):  MutableList<TrainingData> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM training_data WHERE activity_id = ?", arrayOf(activityId.toString()))

        val trainingDataList = mutableListOf<TrainingData>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
            val x_axis = cursor.getFloat(cursor.getColumnIndexOrThrow("date"))
            val y_axis = cursor.getFloat(cursor.getColumnIndexOrThrow("date"))
            val z_axis = cursor.getFloat(cursor.getColumnIndexOrThrow("date"))
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
            val activityId = cursor.getLong(cursor.getColumnIndexOrThrow("activity_id"))

            val trainingData = TrainingData(id, x_axis, y_axis, z_axis, timestamp, activityId)
            trainingDataList.add(trainingData)
        }

        cursor.close()
        db.close()

        for (x in trainingDataList) {
            Log.d("TRAIN", x.toString())
        }

        return trainingDataList
    }
    */

    fun saveData(acceleration: FloatArray) {
        val values = ContentValues().apply {
            put(TrainingContract.TrainingDataEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis())
            put(TrainingContract.TrainingDataEntry.COLUMN_NAME_X_AXIS,acceleration[0])
            put(TrainingContract.TrainingDataEntry.COLUMN_NAME_Y_AXIS,acceleration[1])
            put(TrainingContract.TrainingDataEntry.COLUMN_NAME_Z_AXIS, acceleration[2])
        }
    }
}