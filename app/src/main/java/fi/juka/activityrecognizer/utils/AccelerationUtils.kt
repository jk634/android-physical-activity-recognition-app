package fi.juka.activityrecognizer.utils

class AccelerationUtils {
    companion object {
        fun calculateAverageSpeed(accelerationData: List<Pair<FloatArray, Long>>): Double {

            var totalSpeed = 0.0
            var totalTime = 0.0

            // Iterate through the acceleration data.
            for (i in 1 until accelerationData.size) {
                // Calculate the time difference between the previous and current samples in seconds.
                val timeDifferenceSeconds = (accelerationData[i].second - accelerationData[i - 1].second) / 1000.0

                // Calculate the acceleration change for each axis.
                val deltaX = accelerationData[i].first[0] - accelerationData[i - 1].first[0]
                val deltaY = accelerationData[i].first[1] - accelerationData[i - 1].first[1]
                val deltaZ = accelerationData[i].first[2] - accelerationData[i - 1].first[2]

                // Calculate the speed for each axis using simple numerical integration.
                val speedX = deltaX * timeDifferenceSeconds
                val speedY = deltaY * timeDifferenceSeconds
                val speedZ = deltaZ * timeDifferenceSeconds

                // Calculate the total speed magnitude.
                val totalSpeedDelta = Math.sqrt(speedX * speedX + speedY * speedY + speedZ * speedZ)

                // Add the speed magnitude to the total speed.
                totalSpeed += totalSpeedDelta

                // Add the time difference to the total time.
                totalTime += timeDifferenceSeconds
            }

            // Calculate the average speed by dividing the total speed by the total time.
            val averageSpeed = totalSpeed / totalTime

            return averageSpeed
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

