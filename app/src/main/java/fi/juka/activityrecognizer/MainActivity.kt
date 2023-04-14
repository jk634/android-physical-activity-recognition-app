package fi.juka.activityrecognizer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fi.juka.accelerometer.Accelerometer

class MainActivity : AppCompatActivity() {

    private lateinit var accelerometer: Accelerometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accelerometer = Accelerometer(this)

    }

    override fun onResume() {
        super.onResume()
        accelerometer.register()
    }
}