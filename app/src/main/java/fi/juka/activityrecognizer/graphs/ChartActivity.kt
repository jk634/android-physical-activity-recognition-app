package fi.juka.activityrecognizer.graphs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.accelerometer.AccelerometerListener
import fi.juka.activityrecognizer.R

class ChartActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var accelerationChartView: AccelerometerChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)
        this.accelerationChartView = AccelerometerChartView(this)
    }

    // Filter data and refresh the accelerationChartView with new values
    override fun onAccelerationChanged(acceleration: FloatArray) {
        accelerometer.filter(acceleration)
        accelerationChartView.update(acceleration[0], acceleration[1], acceleration[2])
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