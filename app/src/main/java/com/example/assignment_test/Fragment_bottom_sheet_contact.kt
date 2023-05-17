package com.example.assignment_test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.assignment_test.databinding.FragmentBottomSheetContactBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Fragment_bottom_sheet_contact: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetContactBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentBottomSheetContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Email.setOnClickListener(){
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:abc@gmail.com")
            startActivity(Intent.createChooser(emailIntent, "Send email"))
        }

        binding.Phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+6012312312")
            startActivity(intent)
        }

        binding.Whatsapp.setOnClickListener {
            val whatsappNumber = "60161231313"
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$whatsappNumber")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }


    }

}


