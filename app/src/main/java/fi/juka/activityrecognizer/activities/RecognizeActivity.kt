package fi.juka.activityrecognizer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.interfaces.AccelerometerListener

class RecognizeActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognize)

        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)

    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        Log.d("test", "${acceleration[0]} ${acceleration[1]} ${acceleration[2]}")
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