package fi.juka.activityrecognizer.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import fi.juka.activityrecognizer.interfaces.AccelerometerListener

class Accelerometer(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as
            SensorManager
    private var accelerometer: Sensor? = null
    private var acceleration: FloatArray = floatArrayOf(0f, 0f, 0f)
    private var gravity: FloatArray = floatArrayOf(0f, 0f, 0f)
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

    // Register the accelerometer listener when called
    fun register(listener: AccelerometerListener) {
        this.listener = listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    // Unregister the accelerometer listener when no focus
    fun unregister() {
        sensorManager.unregisterListener(this)
    }

    // Filter accelerometer data with low- and high-pass filters
    fun filter(acceleration: FloatArray): FloatArray {
        val alpha: Float = 0.8f

        // Low-pass, isolates the force of gravity
        gravity[0] = alpha * gravity[0] + (1 - alpha) * acceleration[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * acceleration[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * acceleration[2]

        // High-pass, removes the gravity contribution
        acceleration[0] = acceleration[0] - gravity[0]
        acceleration[1] = acceleration[1] - gravity[1]
        acceleration[2] = acceleration[2] - gravity[2]

        return acceleration
    }

}

