import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.assignment_test.ActivityChangePassword
import com.example.assignment_test.Fragment_bottom_sheet_email
import com.example.assignment_test.Fragment_bottom_sheet_username
import com.example.assignment_test.R
import com.example.assignment_test.SettingsActivity
import com.example.assignment_test.SignUpActivity
import com.example.assignment_test.databinding.FragmentMineBinding
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask.TaskSnapshot
import java.util.UUID


class MineFragment : Fragment(), Fragment_bottom_sheet_username.OnUserIdEnteredListener,
    Fragment_bottom_sheet_email.OnNameEnteredListener,Dialog_weight_picker.OnWeightSelectedListener,
    Dialog_height_picker.OnHeightSelectedListener{

    private lateinit var databaseRef: DatabaseReference
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var binding: FragmentMineBinding
    var formatDate=SimpleDateFormat("dd MMMM YYYY", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMineBinding.inflate(inflater, container, false)
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("SignupUsers").child(uid)
        readData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonSettings = binding.buttonSettings
        val profilePicture = binding.profileImage
        val usernameLayout=binding.usernameLayout

        buttonSettings.setOnClickListener {
            buttonClicked()
        }

        val fragmentBottomSheetUsername=Fragment_bottom_sheet_username()
        usernameLayout.setOnClickListener{
            fragmentBottomSheetUsername.show(childFragmentManager,"BottomSheetUsername")
        }

        binding.passwordLayout.setOnClickListener {
            startActivity(Intent(activity, ActivityChangePassword::class.java))

        }

        val fragmentBottomSheetEmail= Fragment_bottom_sheet_email()
        binding.emailLayout.setOnClickListener{
            fragmentBottomSheetEmail.show(childFragmentManager,"BottomSheetEmail")
        }


        //weight onclick
        binding.weightLayout.setOnClickListener {
            val dialog = Dialog_weight_picker(requireContext())
            dialog.setOnWeightSelectedListener(this)
            dialog.show(childFragmentManager, "weight_picker_dialog")
        }

        binding.heightLayout.setOnClickListener {
            val dialog = Dialog_height_picker(requireContext())
            dialog.setOnHeightSelectedListener(this)
            dialog.show(childFragmentManager, "height_picker_dialog")
        }

        //gender onclick
        binding.genderLayout.setOnClickListener {
            val listGender = arrayOf("Male","Female")
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Choose your gender")
            builder.setSingleChoiceItems(listGender,-1) {
                dialogInterface:DialogInterface,i:Int ->
                binding.genderValue.text=listGender[i]

                databaseRef.child("gender").setValue(listGender[i])
                    .addOnSuccessListener {
                        Toast.makeText(context, "Change gender Successful", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Error occurred while updating data
                    }

                dialogInterface.dismiss()

            }
            val dialogGender=builder.create()
            dialogGender.show()

        }

        binding.profilePictureChangeButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)

        }

        binding.logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }


    }


    private fun readData() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.value as? HashMap<String, Any>
                if (userData != null) {
                    val username = userData["username"].toString()
                    val gender = userData["gender"].toString()
                    val height = userData["height"].toString()
                    val weight = userData["weight"].toString()
                    val email = userData["email"].toString()
                    val age = userData["age"].toString()
                    val storageRef = FirebaseStorage.getInstance().reference
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val profilePicturesRef = storageRef.child("profile_pictures/$userId.jpg") // Reference to the profile picture file

                    profilePicturesRef.downloadUrl.addOnSuccessListener { uri ->
                        // The download URL of the profile picture is available here
                        // Use an image loading library to load and display the profile picture
                        context?.let { nonNullContext ->
                            Glide.with(nonNullContext)
                                .load(uri)
                                .into(binding.profileImage)
                        }
                    }.addOnFailureListener { exception ->
                        // Handle any errors that occurred during the download
                        // Display a placeholder image or an error message if necessary
                    }

                    // Update your UI or perform any other operations with the retrieved values
                    binding.usernameValue.text = username
                    binding.topnameValue.text = username
                    binding.genderValue.text = gender
                    binding.heightValue.text = height
                    binding.weightValue.text = weight
                    binding.emailValue.text = email
                    binding.ageValue.text = age


                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.data // Get URI of selected image
            binding.profileImage.setImageURI(imageUri)
            uploadProfilePicture(imageUri!!)

        }
    }
    private fun uploadProfilePicture(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val profilePicturesRef = storageReference.child("profile_pictures/${userId}.jpg")

        profilePicturesRef.putFile(imageUri)
            .addOnSuccessListener {
                // Profile picture uploaded successfully
                Toast.makeText(requireContext(), "Profile picture uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the upload
                Toast.makeText(requireContext(), "Failed to upload profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onWeightSelected(weight: Double) {
        binding.weightValue.text = weight.toInt().toString()
    }

    override fun onHeightSelected(height: Double) {
        binding.heightValue.text = height.toInt().toString()
    }
    private fun buttonClicked() {
        Log.i("Button", "Button Clicked")
        startActivity(Intent(activity, SettingsActivity::class.java))
    }


    override fun onInputReceived(username: String) {
        binding.usernameValue.text = username
    }

    override fun onEmailEntered(email: String) {
        binding.emailValue.text = email
    }


}


