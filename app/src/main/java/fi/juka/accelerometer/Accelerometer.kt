package fi.juka.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class Accelerometer(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as
            SensorManager
    private var accelerometer: Sensor? = null
    private var acceleration: FloatArray = floatArrayOf(0f, 0f, 0f)

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


    override fun onSensorChanged(event: SensorEvent?) {

        // Checks if the incoming data is from the accelerometer. If so, clone it to the array.
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            acceleration = event.values.clone()
        }

        Log.d("TEST", event!!.values[0].toString())
    }

    // Registers accelerometer listener when called
    fun register() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}

