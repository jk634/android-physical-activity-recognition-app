package fi.juka.activityrecognizer.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.interfaces.AccelerometerListener


class MainActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var acceleration: FloatArray
    private lateinit var btnChart: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)

        this.btnChart = findViewById(R.id.btnChart)
        btnChart.setOnClickListener{chartClicked()}
    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        this.acceleration = acceleration
    }

    override fun onResume() {
        super.onResume()
        accelerometer.register(this)
    }

    override fun onPause() {
        super.onPause()
        accelerometer.unregister()
    }

    private fun chartClicked() {
        val intent = Intent(this, ChartActivity::class.java)
        startActivity(intent)
    }
}
