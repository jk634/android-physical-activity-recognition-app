package fi.juka.activityrecognizer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fi.juka.accelerometer.Accelerometer
import fi.juka.accelerometer.AccelerometerListener

class MainActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accelerometer = Accelerometer(this)

        accelerometer.register(this)

    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        Log.d("TESTI", acceleration[2].toString())
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