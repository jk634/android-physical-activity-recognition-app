package fi.juka.activityrecognizer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.interfaces.AccelerometerListener

class TrainActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    //private lateinit var acceleration: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)

    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        //this.acceleration = acceleration
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