package fi.juka.activityrecognizer.activities

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.database.TrainingContract
import fi.juka.activityrecognizer.database.TrainingDbHelper
import fi.juka.activityrecognizer.interfaces.AccelerometerListener
import fi.juka.activityrecognizer.utils.DialogManager

class TrainActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var db: SQLiteDatabase
    private lateinit var activityName: String
    private lateinit var activityId: Integer
    private val dbHelper = TrainingDbHelper(this)
    private lateinit var showTempActivity: TextView

    var flag = false
    //private lateinit var acceleration: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        this.showTempActivity = findViewById(R.id.tempActivity)

        this.accelerometer = Accelerometer(this)
        accelerometer.register(this)

        dialogHandler()
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
                    this.activityName = activityName
                    this.activityId = activityId
                    showTempActivity.text = "${activityName} ${activityId}"
            }
            )
        }
    }

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

    override fun onResume() {
        super.onResume()
        accelerometer.register(this)
    }

    override fun onPause() {
        super.onPause()
        accelerometer.unregister()
    }
}