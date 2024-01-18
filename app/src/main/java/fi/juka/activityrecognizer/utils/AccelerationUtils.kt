package fi.juka.activityrecognizer.utils

class AccelerationUtils {
    companion object {
        fun calculateAverageSpeed(x: List<Double>, y: List<Double>, z: List<Double>, time: List<Long>): Double {

            var totalDistance = 0.0
            var totalTime = 0.0

            for (i in 1 until x.size) {
                val timeDifference = (time[i] - time[i - 1]) / 1000.0 // time in seconds
                val distanceX = Math.abs(x[i] - x[i - 1])
                val distanceY = Math.abs(y[i] - y[i - 1])
                val distanceZ = Math.abs(z[i] - z[i - 1])

                val distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ)
                totalDistance += distance
                totalTime += timeDifference
            }

            return totalDistance / totalTime
        }

        fun calculateAverageTotalAcceleration(x: List<Double>, y: List<Double>, z: List<Double>): Double {

            var totalAcceleration = 0.0

            for (i in 1 until x.size) {
                val accX = x[i]
                val accY = y[i]
                val accZ = z[i]

                val acceleration = Math.sqrt(accX * accX + accY * accY + accZ * accZ)
                totalAcceleration += acceleration
            }

            return totalAcceleration / x.size
        }
    }
}

