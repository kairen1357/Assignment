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

class DialogWeightPicker(private val context: Context) : DialogFragment() {

    private var listener: OnWeightSelectedListener? = null
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_weight_picker, null)
        val weightPicker = view.findViewById<NumberPicker>(R.id.weight_picker)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("SignupUsers").child(uid)

        weightPicker.minValue = 100
        weightPicker.maxValue = 300
        weightPicker.value = 160
        weightPicker.wrapSelectorWheel = false

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setTitle("Select Weight in kg")
        builder.setPositiveButton("OK") { dialog, which ->
            val weight = weightPicker.value.toLong()
            listener?.onWeightSelected(weight.toDouble())
            updateDataInDatabase(weight)
        }
        builder.setNegativeButton("Cancel", null)

        return builder.create()
    }

    private fun updateDataInDatabase(weight: Long) {
        databaseRef.child("weight").setValue(weight)
            .addOnSuccessListener {
                Toast.makeText(context, "Change weight Successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Error occurred while updating data
            }
    }

    interface OnWeightSelectedListener {
        fun onWeightSelected(weight: Double)
    }

    fun setOnWeightSelectedListener(listener: OnWeightSelectedListener) {
        this.listener = listener
    }
}
