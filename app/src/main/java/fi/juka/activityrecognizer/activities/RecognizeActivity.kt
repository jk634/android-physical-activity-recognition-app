package fi.juka.activityrecognizer.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.AccelerationDataBuffer
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.comparison.DataCompare
import fi.juka.activityrecognizer.interfaces.AccelerometerListener
import fi.juka.activityrecognizer.utils.AccelerationUtils

class RecognizeActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var comparison: DataCompare
    private var currentActivity : TextView? = null
    private val currentData = mutableListOf<FloatArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognize)

        this.currentActivity = findViewById(R.id.currentActivity)

        this.accelerometer = Accelerometer(this)
        this.comparison = DataCompare(this)
        accelerometer.register(this)

        comparison.preprocessing()
    }

    override fun onAccelerationChanged(acceleration: FloatArray) {

        val accData = accelerometer.filter(acceleration)

        currentData.add(
                floatArrayOf(accData[0], accData[1], accData[2])
        )

        if (currentData.size == 40) {
            val x = currentData.map { it[0].toDouble() }
            val y = currentData.map { it[1].toDouble() }
            val z = currentData.map { it[2].toDouble() }

            val avrgTotalAccReal = AccelerationUtils.calculateAverageTotalAcceleration(x,y,z)

            comparison.compareDataAverages(avrgTotalAccReal) { activityName, activityTotAcc ->

                val activityInfo = if (activityName != null && activityName != "still") {
                    "You are $activityName\n(${String.format("%.3f", activityTotAcc)})\n"
                } else if (activityName == "still") {
                    "still\n"
                } else {
                    ""
                }

                val formattedReal = "%.3f".format(avrgTotalAccReal)
                currentActivity!!.text = "$activityInfo\nReal time total acceleration\n$formattedReal"
            }
            currentData.clear()
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