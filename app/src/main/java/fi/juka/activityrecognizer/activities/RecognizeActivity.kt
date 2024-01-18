package fi.juka.activityrecognizer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.AccelerationDataBuffer
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.comparison.DataComparer
import fi.juka.activityrecognizer.interfaces.AccelerometerListener
import fi.juka.activityrecognizer.utils.AccelerationUtils

class RecognizeActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var comparision: DataComparer
    private var currentActivity : TextView? = null
    private lateinit var accelerationBuffer: AccelerationDataBuffer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognize)

        this.currentActivity = findViewById(R.id.currentActivity)

        this.accelerometer = Accelerometer(this)
        this.comparision = DataComparer(this)
        accelerometer.register(this)

        this.accelerationBuffer = AccelerationDataBuffer(bufferSize = 20)

        comparision.preprocessing()
    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        var accelerationData = accelerometer.filter(acceleration)

        accelerationBuffer.addData(
            Triple(accelerationData[0].toDouble(), accelerationData[1].toDouble(), accelerationData[2].toDouble()),
            System.currentTimeMillis()
        )

        val currentData = accelerationBuffer.getData()

        if (currentData.size == 20) {
            val x = currentData.map { it.first.first }
            val y = currentData.map { it.first.second }
            val z = currentData.map { it.first.third }
            val time = currentData.map { it.second }

            val averageTotalAcceleration = AccelerationUtils.calculateAverageTotalAcceleration(x,y,z)

            comparision.compareDataAverages(averageTotalAcceleration) { activityName ->
                if (activityName != null && activityName.isNotEmpty()) {
                    currentActivity!!.text = "$activityName"
                }
            }
            accelerationBuffer.emptyData()
        }
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