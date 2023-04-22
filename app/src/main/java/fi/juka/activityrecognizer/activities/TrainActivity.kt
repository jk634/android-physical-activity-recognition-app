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
import fi.juka.activityrecognizer.database.TrainingContract
import fi.juka.activityrecognizer.database.TrainingDbHelper
import fi.juka.activityrecognizer.interfaces.AccelerometerListener
import fi.juka.activityrecognizer.utils.DialogManager
import org.w3c.dom.Text

class TrainActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var db: SQLiteDatabase
    private var activityName: String? = null
    private var activityId: Integer? = null
    private val dbHelper = TrainingDbHelper(this)
    private lateinit var showTempActivity: TextView
    private lateinit var showTimer: TextView
    private lateinit var startBtn: Button

    //private lateinit var acceleration: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        this.showTempActivity = findViewById(R.id.tempActivity)

        this.accelerometer = Accelerometer(this)
        this.startBtn = findViewById(R.id.btnStartSample)
        this.showTimer = findViewById(R.id.showTimer)

        dialogHandler()
        //initTrainView()
        startBtn.setOnClickListener{startClicked()}
    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        //this.acceleration = acceleration
        //saveData(acceleration)
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
                        val activityId = saveActivity(activityName)
                        showTempActivity.text = "${activityName} ${activityId}"
                }
            }, {
                activityId, activityName ->
                    if (activityId != null && activityName != null) {
                        this.activityName = activityName
                        this.activityId = activityId
                    } else {
                            this.activityName = null
                            this.activityId = null
                    }
                }
            )
        }
    }

    // Save the new Activity to the new row and return id
    private fun saveActivity(activityName: String): Int {
        this.db = dbHelper.writableDatabase
        val formattedActivityName = activityName.lowercase().replace("\\s".toRegex(), "_")

        val values = ContentValues().apply {
            put(TrainingContract.ActivityEntry.COLUMN_NAME_ACTIVITY, formattedActivityName)
        }
        val id = db.insert(TrainingContract.ActivityEntry.TABLE_NAME, null, values)

        db.close()

        return id.toInt()
    }


    fun saveData(acceleration: FloatArray) {
        /*val values = ContentValues().apply {
            put(TrainingContract.TrainingDataEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis())
            put(TrainingContract.TrainingDataEntry.COLUMN_NAME_X_AXIS,acceleration[0])
            put(TrainingContract.TrainingDataEntry.COLUMN_NAME_Y_AXIS,acceleration[1])
            put(TrainingContract.TrainingDataEntry.COLUMN_NAME_Z_AXIS, acceleration[2])
        }*/
    }

    /*private fun initTrainView() {
        if (activityId != null && activityName != null) {
            showTempActivity.text = "${activityName} ${activityId} number of samples: TODO"

        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    } */

    fun startClicked() {
        startBtn.visibility = View.GONE
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