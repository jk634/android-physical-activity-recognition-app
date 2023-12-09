package fi.juka.activityrecognizer.comparison

import android.content.Context
import android.util.Log
import fi.juka.activityrecognizer.database.ActivityDao
import fi.juka.activityrecognizer.database.TrainingDbHelper
import fi.juka.activityrecognizer.utils.AccelerationUtils

class DataComparer(private val context: Context) {
    private val activityDao = ActivityDao(TrainingDbHelper(context))
    val activityAverageTotalAccList  = mutableListOf<Triple<Long, String, Double>>() // id and total acceleration average for every activity
    //private var averageTotalAcc = 0.0

    fun preprocessing() {

        var activities = activityDao.getActivitiesList()
        Log.d("ACTIVITIES", activities.toString())

        for (activity in activities) {
            val activityId = activity.first
            val activityName = activity.second

            val samples = activityDao.getAllTrainingDataForActivity(activityId)

            if (samples.isNotEmpty()) {

                var averageTotalAcc = AccelerationUtils.calculateAverageTotalAcceleration(
                    samples.map { it.x_axis.toDouble() },
                    samples.map { it.y_axis.toDouble() },
                    samples.map { it.z_axis.toDouble() })

                activityAverageTotalAccList.add(Triple(activityId,activityName, averageTotalAcc))
            }

                /*
                val averageSpeed = AccelerationUtils.calculateAverageSpeed(
                    samples.map { it.x_axis.toDouble() },
                    samples.map { it.y_axis.toDouble() },
                    samples.map { it.z_axis.toDouble() },
                    samples.map { it.timestamp }
                 )
                Log.d("preproc", averageSpeed.toString())
                */
            }

        Log.d("preprocess", activityAverageTotalAccList.toString())

        }


    fun compareDataAverages() {
    }
}