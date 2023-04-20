package fi.juka.activityrecognizer.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import fi.juka.activityrecognizer.database.TrainingDbHelper

class DialogManager(private val context: Context) {

    private val builder = AlertDialog.Builder(context)


    // Open the first dialog when the activity starts
    fun showDialog(title: String, message: String, onPositiveButtonClick: () -> Unit) {
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
                       onSelectedActivityClick: (Integer, String) -> Unit) {

        builder.setNegativeButton(null, null)
        builder.setTitle(title).setMessage("")

        val activities = getActivitiesListFromDatabase()

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
                val activityId = selectedActivity.first as Integer
                val activityName = selectedActivity.second as String
                onSelectedActivityClick(activityId, activityName)
            }
            dialog.dismiss()
        }

    }

    private fun getActivitiesListFromDatabase(): MutableList<Pair<Int, String>> {

        val activitiesList = mutableListOf<Pair<Int, String>>()

        // Fetch all activities from the database
        val db = TrainingDbHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM activity", null)

        // Add every activity to the list
        while (cursor.moveToNext()) {
            val activityId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
            val activityName = cursor.getString(cursor.getColumnIndexOrThrow("activity_name"))
            activitiesList.add(Pair(activityId, activityName))
        }

        cursor.close()
        db.close()

        return activitiesList
    }

}