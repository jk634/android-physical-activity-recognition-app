package fi.juka.activityrecognizer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.AccelerationDataBuffer
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.interfaces.AccelerometerListener
import fi.juka.activityrecognizer.utils.AccelerationUtils

class RecognizeActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private var recognizedActivity : TextView? = null
    private lateinit var accelerationBuffer: AccelerationDataBuffer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognize)

        this.recognizedActivity = findViewById(R.id.recognizedActivity)

        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)

        this.accelerationBuffer = AccelerationDataBuffer(bufferSize = 20)

    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        accelerationBuffer.addData(
            Triple(acceleration[0].toDouble(), acceleration[1].toDouble(), acceleration[2].toDouble()),
            System.currentTimeMillis()
        )

        val currentData = accelerationBuffer.getData()

        if (currentData.size == 20) {
            val x = currentData.map { it.first.first }
            val y = currentData.map { it.first.second }
            val z = currentData.map { it.first.third }
            val time = currentData.map { it.second }

            val averageSpeed = AccelerationUtils.calculateAverageSpeed(x, y, z, time)
            Log.d("average", "Average $averageSpeed")

            accelerationBuffer.emptyData()
        }

        Log.d("current", currentData.toString())
    }

    override fun onResume() {
        super.onResume()
        accelerometer.register(this)
    }

    override fun onPause() {
        super.onPause()
        accelerometer.unregister()
    }
}