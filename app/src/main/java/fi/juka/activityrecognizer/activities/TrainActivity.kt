package fi.juka.activityrecognizer.activities

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.database.ActivityDao
import fi.juka.activityrecognizer.database.TrainingContract
import fi.juka.activityrecognizer.database.TrainingDbHelper
import fi.juka.activityrecognizer.interfaces.AccelerometerListener
import fi.juka.activityrecognizer.utils.DialogManager
import org.w3c.dom.Text

class TrainActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var db: SQLiteDatabase
    private var activityName: String? = null
    private var activityId: Long? = null
    private val dbHelper by lazy { TrainingDbHelper(this) }
    private lateinit var showTempActivity: TextView
    private lateinit var showTimer: TextView
    private lateinit var startBtn: Button
    private val activityDao = ActivityDao(dbHelper)

    //private lateinit var acceleration: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        this.showTempActivity = findViewById(R.id.tempActivity)

        this.accelerometer = Accelerometer(this)
        this.startBtn = findViewById(R.id.btnStartSample)
        this.showTimer = findViewById(R.id.showTimer)

        //dbHelper.deleteAllTables()

        dialogHandler()
        startBtn.setOnClickListener{startClicked()}
    }

    override fun onAccelerationChanged(acceleration: FloatArray) {

        showTempActivity.text = "${acceleration[0]} ${acceleration[1]} ${acceleration[2]}"
        //this.acceleration = acceleration
        activityDao.saveData(acceleration, activityId!!)

    }

    // Makes whole dialog process
    private fun dialogHandler() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog("Create Training Data", "Would you like to start " +
                "taking training data?"
        ) {
            dialogManager.showNameDialog("Choose an existing activity or create a new one to " +
                    "collect data", {
                activityName ->
                    if (activityName != null && activityName.isNotEmpty()) {
                        activityId = activityDao.saveActivityAndGetId(activityName)
                        showTempActivity.text = "$activityName"

                }
            }, {
                activityId, activityName ->
                    if (activityId != null && activityName != null) {
                        this.activityName = activityName
                        this.activityId = activityId
                        showTempActivity.text = "${activityName}"
                    } else {
                            this.activityName = null
                            this.activityId = null
                    }
                }
            )
        }
    }

    fun startClicked() {
        startBtn.visibility = View.GONE
        showTempActivity.textSize = 20f
        Thread {
            var x = 0
            accelerometer.register(this)
            while (x < 10) {
                runOnUiThread() {
                    showTimer.text = (x + 1).toString()
                }
                Thread.sleep(1000)
                x++
            }
            accelerometer.unregister()
            runOnUiThread() {
                showTimer.textSize = 50F
                showTimer.text = "Completed"
                activityDao.incrementSampleCount(activityId!!)
                activityDao.getAllTrainingDataForActivity(activityId!!)

            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        //accelerometer.register(this)
    }

    override fun onPause() {
        super.onPause()
        //accelerometer.unregister()
    }
}