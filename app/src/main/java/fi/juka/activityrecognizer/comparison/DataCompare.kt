package fi.juka.activityrecognizer.comparison

import android.content.Context
import android.util.Log
import fi.juka.activityrecognizer.database.ActivityDao
import fi.juka.activityrecognizer.database.TrainingDbHelper
import fi.juka.activityrecognizer.utils.AccelerationUtils

class DataCompare(private val context: Context) {

    private val activityDao = ActivityDao(TrainingDbHelper(context))
    // id name and total acceleration average for every activity
    private val activityAverageTotalAccList = mutableListOf<Triple<Long, String, Double>>()
    private val activityAverageSpeedList = mutableListOf<Triple<Long, String, Double>>()
    private var thresholdAcceleration = 0.0
    private var thresholdSpeed = 0.0
    private val stillThreshold = 0.1

    fun preprocessing() {

        val activities = activityDao.getActivitiesList()

        for (activity in activities) {
            val activityId = activity.first
            val activityName = activity.second
            val activityAverageSpeed = activity.third

            val samples = activityDao.getAllTrainingDataForActivity(activityId)

            if (samples.isNotEmpty()) {

                // for total acceleration comparisons
                val x = samples.map { it.x_axis.toDouble() }
                val y = samples.map { it.y_axis.toDouble() }
                val z = samples.map { it.z_axis.toDouble() }

                val averageTotalAcc = AccelerationUtils.calculateAverageTotalAcceleration(x,y,z)
                activityAverageTotalAccList.add(Triple(activityId,activityName, averageTotalAcc))
                activityAverageSpeedList.add(Triple(activityId,activityName, activityAverageSpeed))
            }

            // Creates thresholds
            if (activityAverageTotalAccList.isNotEmpty()) {
                val maxAverage = activityAverageTotalAccList.maxByOrNull { it.third }!!.third
                val minAverage = activityAverageTotalAccList.minByOrNull { it.third }!!.third

                thresholdAcceleration = (maxAverage - minAverage) / activityAverageTotalAccList.size
            }

            if (activityAverageSpeedList.isNotEmpty()) {
                val maxAverage = activityAverageSpeedList.maxByOrNull { it.third }!!.third
                val minAverage = activityAverageSpeedList.minByOrNull { it.third }!!.third

                thresholdSpeed = (maxAverage - minAverage) / activityAverageSpeedList.size
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
            it.third - thresholdAcceleration <= realTimeTotAvrgAcc && realTimeTotAvrgAcc <= it.third + thresholdAcceleration
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

    fun compareSpeedAverages(realTimeSpeed: Double, onActivityRecognized: (String) -> Unit) {

        // if staying still
        if (realTimeSpeed < stillThreshold) {
            onActivityRecognized("still")
            return
        }

        val candidates = activityAverageSpeedList.filter {
            it.third - thresholdSpeed <= realTimeSpeed && realTimeSpeed <= it.third + thresholdSpeed
        }

        when {
            candidates.size == 1 -> onActivityRecognized(candidates.first().second)
            candidates.size > 1 -> {
                val closestActivity = candidates.minByOrNull { Math.abs(it.third - realTimeSpeed) }
                closestActivity?.let {
                    onActivityRecognized(it.second)
                }
            }
            else -> onActivityRecognized("")
        }
    }

}