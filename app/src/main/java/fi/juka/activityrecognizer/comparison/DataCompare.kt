package fi.juka.activityrecognizer.comparison

import android.content.Context
import android.util.Log
import fi.juka.activityrecognizer.database.ActivityDao
import fi.juka.activityrecognizer.database.TrainingDbHelper
import fi.juka.activityrecognizer.utils.AccelerationUtils

class DataCompare(private val context: Context) {

    private val activityDao = ActivityDao(TrainingDbHelper(context))
    // id name and total acceleration average for every activity
    private val activityAverageTotalAccList  = mutableListOf<Triple<Long, String, Double>>()
    private var threshold = 0.0
    private val stillThreshold = 0.2

    fun preprocessing() {

        val activities = activityDao.getActivitiesList()

        for (activity in activities) {
            val activityId = activity.first
            val activityName = activity.second

            val samples = activityDao.getAllTrainingDataForActivity(activityId)

            if (samples.isNotEmpty()) {

                val averageTotalAcc = AccelerationUtils.calculateAverageTotalAcceleration(
                    samples.map { it.x_axis.toDouble() },
                    samples.map { it.y_axis.toDouble() },
                    samples.map { it.z_axis.toDouble() })

                activityAverageTotalAccList.add(Triple(activityId,activityName, averageTotalAcc))
            }

            if (activityAverageTotalAccList.isNotEmpty()) {
                val maxAverage = activityAverageTotalAccList.maxByOrNull { it.third }!!.third
                val minAverage = activityAverageTotalAccList.minByOrNull { it.third }!!.third

                threshold = (maxAverage - minAverage) / activityAverageTotalAccList.size
            }
        }
    }

    fun compareTotalAccelerationAverages(realTimeTotAvrgAcc: Double, onActivityRecognized: (String) -> Unit) {

        // if staying still
        if (realTimeTotAvrgAcc < stillThreshold) {
            onActivityRecognized("still")
            return
        }

        val candidates = activityAverageTotalAccList.filter {
            it.third - threshold <= realTimeTotAvrgAcc && realTimeTotAvrgAcc <= it.third + threshold
        }

        when {
            candidates.size == 1 -> onActivityRecognized(candidates.first().second)
            candidates.size > 1 -> {
                val closestActivity = candidates.minByOrNull { Math.abs(it.third - realTimeTotAvrgAcc) }
                closestActivity?.let {
                    onActivityRecognized(it.second)
                }
            }
            else -> onActivityRecognized("")
        }
    }
}