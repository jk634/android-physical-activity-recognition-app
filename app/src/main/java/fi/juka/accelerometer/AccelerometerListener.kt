package fi.juka.accelerometer

interface AccelerometerListener {
    fun onAccelerationChanged(acceleration: FloatArray)
}