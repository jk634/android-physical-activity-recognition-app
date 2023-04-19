package fi.juka.activityrecognizer.activities

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fi.juka.activityrecognizer.R
import fi.juka.activityrecognizer.accelerometer.Accelerometer
import fi.juka.activityrecognizer.database.TrainingContract
import fi.juka.activityrecognizer.database.TrainingDbHelper
import fi.juka.activityrecognizer.interfaces.AccelerometerListener
import fi.juka.activityrecognizer.utils.DialogManager

class TrainActivity : AppCompatActivity(), AccelerometerListener {

    private lateinit var accelerometer: Accelerometer
    private lateinit var db: SQLiteDatabase
    private lateinit var builder: DialogManager
    private lateinit var activityName: String
    private val dbHelper = TrainingDbHelper(this)

    var flag = false
    //private lateinit var acceleration: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)


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
        dialogManager.showDialog("Do training data", "Do you want to start taking training data?"
        ) {
            dialogManager.showNameDialog("Give Activity name you are going to do") {
                name ->
                if (name != null) {
                    saveActivity(name)
                }
            }
        }
    }

    private fun saveActivity(name: String) {
        this.db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TrainingContract.ActivityEntry.COLUMN_NAME_ACTIVITY, name)
        }
        db.insert(TrainingContract.ActivityEntry.TABLE_NAME, null, values)
        db.close()
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