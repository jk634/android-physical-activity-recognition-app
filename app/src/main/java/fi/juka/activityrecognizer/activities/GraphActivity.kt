package fi.juka.activityrecognizer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.interfaces.AccelerometerListener
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.graphs.AccelerometerGraphView

class GraphActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var accelerationGraphView: AccelerometerGraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)
        this.accelerationGraphView = AccelerometerGraphView(this)
    }

    // Filter data and refresh the accelerationGraphView with new values
    override fun onAccelerationChanged(acceleration: FloatArray) {
        accelerometer.filter(acceleration)
        accelerationGraphView.update(acceleration[0], acceleration[1], acceleration[2])
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