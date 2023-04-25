package fi.juka.activityrecognizer.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import fi.juka.activityrecognizer.database.ActivityDao
import fi.juka.activityrecognizer.database.TrainingDbHelper

class DialogManager(private val context: Context) {

    private val builder = AlertDialog.Builder(context)
    val activityDao = ActivityDao(TrainingDbHelper(context))


    // Open the first dialog when the activity starts
    fun showActivityDialog(title: String, message: String, onPositiveButtonClick: () -> Unit) {
        builder.setTitle(title).setMessage(message)
        builder.setPositiveButton("Yes") { dialog, which ->
            onPositiveButtonClick()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.setNeutralButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        /*
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_view, null)
        builder.setView(dialogView)
         */

        val dialog = builder.create()
        dialog.show()
    }

    // Open a new dialog for the user to input the name of the activity.
    fun showNameDialog(title: String, onPositiveButtonClick: (String?) -> Unit,
                       onSelectedActivityClick: (Long, String) -> Unit) {

        builder.setNegativeButton(null, null)
        builder.setTitle(title).setMessage("")

        val activities = activityDao.getActivitiesList()

        // Create the ListView and ArrayAdapter for the activities list
        val listView = ListView(context)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, activities)
        listView.adapter = adapter

        val editText = EditText(context)

        // Set the dialog view to be the ListView and EditText
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(listView)
        layout.addView(editText)
        builder.setView(layout)

        builder.setPositiveButton("OK") { dialog, which ->
            val inputText = editText.text.toString()
            if (inputText.isNotEmpty()) {
                onPositiveButtonClick(inputText)
            }
        }

        val dialog = builder.create()
        dialog.show()

        // Send the selected activity to the onSelectedActivityClick callback function
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedActivity = parent.getItemAtPosition(position)
            if (selectedActivity is Pair<*, *>) {
                val activityId = selectedActivity.first as Long
                val activityName = selectedActivity.second as String
                onSelectedActivityClick(activityId, activityName)
            }
            dialog.dismiss()
        }

    }

    fun showSaveDialog(title: String, onPositiveButtonClick: () -> Unit) {
        builder.setTitle(title)
        builder.setPositiveButton("Yes") { dialog, which ->
            onPositiveButtonClick()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }
}