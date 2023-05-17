import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.assignment_test.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DialogAgePicker(private val context: Context) : DialogFragment() {

    private var listener: OnAgeSelectedListener? = null
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_age_picker, null)
        val agePicker = view.findViewById<NumberPicker>(R.id.age_picker)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("SignupUsers").child(uid)

        agePicker.minValue = 0
        agePicker.maxValue = 120
        agePicker.value = 10
        agePicker.wrapSelectorWheel = false

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setTitle("Select Your Age")
        builder.setPositiveButton("OK") { dialog, which ->
            val age = agePicker.value.toLong()
            listener?.onAgeSelected(age.toInt())
            updateDataInDatabase(age)
        }
        builder.setNegativeButton("Cancel", null)

        return builder.create()
    }

    private fun updateDataInDatabase(age: Long) {
        databaseRef.child("age").setValue(age)
            .addOnSuccessListener {
                Toast.makeText(context, "Change Age Successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Error occurred while updating data
            }
    }

    interface OnAgeSelectedListener {
        fun onAgeSelected(age: Int)
    }

    fun setOnAgeSelectedListener(listener: OnAgeSelectedListener) {
        this.listener = listener
    }
}
