package fi.juka.activityrecognizer.activities

import android.content.ContentValues
import android.content.Intent
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
import fi.juka.activityrecognizer.utils.AccelerationUtils
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
    private lateinit var showSamplesCount: TextView
    private lateinit var startBtn: Button
    private lateinit var retakeBtn: Button
    private val activityDao = ActivityDao(dbHelper)
    private val accelerationData = mutableListOf<Pair<FloatArray, Long>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        this.showTempActivity = findViewById(R.id.tempActivity)

        this.accelerometer = Accelerometer(this)
        this.startBtn = findViewById(R.id.btnStartSample)
        this.retakeBtn = findViewById(R.id.retakeBtn)
        this.showTimer = findViewById(R.id.showTimer)
        this.showSamplesCount = findViewById(R.id.showSamplesCount)

        //dbHelper.deleteAllTables()

        activityChoiseDialogHandler()
        startBtn.setOnClickListener{startClicked()}
        retakeBtn.setOnClickListener{
            retakeBtn.visibility = View.INVISIBLE
            showSamplesCount.visibility = View.INVISIBLE
            startClicked()}
        val n = activityDao.getActivitiesList()
    }

    override fun onAccelerationChanged(acceleration: FloatArray) {
        val filteredAccData = accelerometer.filter(acceleration)
        val data = Pair(filteredAccData, System.currentTimeMillis())
        accelerationData.add(data)
        showTempActivity.text = "${filteredAccData[0]} ${filteredAccData[1]} ${filteredAccData[2]}"
    }

    fun startClicked() {
        startBtn.visibility = View.GONE
        showTempActivity.textSize = 17f
        Thread {
            var x = 0
            accelerometer.register(this)
            while (x < 5) {
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
                saveDataDialogHandler()

            }
        }.start()
    }

    // Makes whole dialog process
    private fun activityChoiseDialogHandler() {
        val dialogManager = DialogManager(this)
        dialogManager.showActivityDialog(
            "Create Training Data",
            "Would you like to start taking the training data?",
            {
                dialogManager.showNameDialog(
                    "Choose an existing activity or create a new one to collect data",
                    { activityName ->
                        if (activityName != null && activityName.isNotEmpty()) {
                            activityId = activityDao.saveActivityAndGetId(activityName)
                            showTempActivity.text = "$activityName"
                            startBtn.visibility = View.VISIBLE
                        }
                    },
                    { activityId, activityName ->
                        if (activityId != null && activityName != null) {
                            this.activityName = activityName
                            this.activityId = activityId
                            showTempActivity.text = "${activityName}"
                            startBtn.visibility = View.VISIBLE
                        } else {
                            this.activityName = null
                            this.activityId = null
                        }
                    }, {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            },
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        )
    }

    fun saveDataDialogHandler() {
        val dialog = DialogManager(this)

        dialog.showSaveDialog("Do you want to save this sample?", {
            activityDao.saveData(accelerationData, activityId!!)
            activityDao.incrementSampleCount(activityId!!)
            activityDao.getAllTrainingDataForActivity(activityId!!)

            val sampleAverageSpeed = AccelerationUtils.calculateAverageSpeed(accelerationData)
            activityDao.updateAverageSpeed(sampleAverageSpeed, activityId!!)

            showSamplesCount.text = "Total samples ${activityDao.getSampleCount(activityId!!)}/10"
            showSamplesCount.visibility = View.VISIBLE
            retakeBtn.visibility = View.VISIBLE
        }, {
            retakeBtn.visibility = View.VISIBLE
        }
        )
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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