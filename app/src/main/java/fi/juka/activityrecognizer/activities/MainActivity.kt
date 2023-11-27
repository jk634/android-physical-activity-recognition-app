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
    private lateinit var btnTrain: Button
    private lateinit var btnChart: Button
    private lateinit var btnRecognize: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)

        this.btnTrain = findViewById(R.id.btnTrain)
        this.btnChart = findViewById(R.id.btnChart)
        this.btnRecognize = findViewById(R.id.btnRecognize)

        btnTrain.setOnClickListener{trainClicked()}
        btnChart.setOnClickListener{chartClicked()}
        btnRecognize.setOnClickListener{recognizeClicked()}
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

    private fun trainClicked() {
        val intent = Intent(this, TrainActivity::class.java)
        startActivity(intent)
    }

    private fun chartClicked() {
        val intent = Intent(this, ChartActivity::class.java)
        startActivity(intent)
    }

    private fun recognizeClicked() {
        val intent = Intent(this, RecognizeActivity::class.java)
        startActivity(intent)
    }
}
