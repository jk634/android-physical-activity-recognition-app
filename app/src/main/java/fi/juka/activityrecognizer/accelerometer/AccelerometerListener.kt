package fi.juka.activityrecognizer.accelerometer

interface AccelerometerListener {
    fun onAccelerationChanged(acceleration: FloatArray)
}