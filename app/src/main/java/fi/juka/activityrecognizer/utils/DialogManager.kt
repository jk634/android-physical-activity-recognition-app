package fi.juka.activityrecognizer.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.EditText

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
        val dialog = builder.create()
        dialog.show()
    }

    // Open a new dialog for the user to input the name of the activity.
    fun showNameDialog(title: String, onPositiveButtonClick: (String?) -> Unit) {
        builder.setNegativeButton(null, null)
        builder.setTitle(title).setMessage("")
        val editText = EditText(context)
        builder.setView(editText)

        builder.setPositiveButton("Yes") { dialog, which ->
            val inputText = editText.text.toString()
            Log.d("INPUT", inputText)
            onPositiveButtonClick(inputText)
        }

        val dialog = builder.create()
        dialog.show()
    }

}