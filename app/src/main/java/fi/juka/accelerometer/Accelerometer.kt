package fi.juka.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log

class Accelerometer(context: Context) {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as
            SensorManager
    private var accelerometer: Sensor? = null

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
}

