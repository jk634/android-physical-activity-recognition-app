package fi.juka.activityrecognizer.accelerometer

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
    private var listener: AccelerometerListener? = null

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


    override fun onSensorChanged(event: SensorEvent?) {

        // If true, clone it to the array and send it to the accelerometer listener
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            acceleration = event.values.clone()
            listener?.onAccelerationChanged(acceleration)
        }
    }

    // Registers the accelerometer listener when called
    fun register(listener: AccelerometerListener) {
        this.listener = listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // Unregisters the accelerometer listener when no focus
    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}

