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

class Dialog_height_picker(private val context: Context) : DialogFragment() {

    private var listener: OnHeightSelectedListener? = null
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_height_picker, null)
        val heightPicker = view.findViewById<NumberPicker>(R.id.height_picker)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("SignupUsers").child(uid)

        heightPicker.minValue = 100
        heightPicker.maxValue = 300
        heightPicker.value = 160
        heightPicker.wrapSelectorWheel = false

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setTitle("Select Height in cm")
        builder.setPositiveButton("OK") { dialog, which ->
            val height = heightPicker.value.toLong()
            listener?.onHeightSelected(height.toDouble())
            updateDataInDatabase(height)
        }
        builder.setNegativeButton("Cancel", null)

        return builder.create()
    }

    private fun updateDataInDatabase(height: Long) {
        databaseRef.child("height").setValue(height)
            .addOnSuccessListener {
                Toast.makeText(context, "Change Height Successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Error occurred while updating data
            }
    }

    interface OnHeightSelectedListener {
        fun onHeightSelected(height: Double)
    }

    fun setOnHeightSelectedListener(listener: OnHeightSelectedListener) {
        this.listener = listener
    }
}
