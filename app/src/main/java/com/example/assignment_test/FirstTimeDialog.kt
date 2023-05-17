import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.assignment_test.R

class FirstTimeDialog(context: Context) : Dialog(context, R.style.CustomDialogStyle) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.planting_tree_dialog, null)
        setContentView(view)

        val dialogTitle: TextView = view.findViewById(R.id.dialogTitle)
        val dialogMessage: TextView = view.findViewById(R.id.dialogMessage)
        val closeButton: Button = view.findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            dismiss()
        }
    }
}
