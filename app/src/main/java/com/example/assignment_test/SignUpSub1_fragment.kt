package com.example.assignment_test

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class SignUpSub1_fragment : Fragment(R.layout.signup_sub1_fragment) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.signup_sub1_fragment, container, false)
        //val intent = Intent(activity,LoginActivity::class.java)
        //startActivity(intent)
        val text1 : TextView = view.findViewById(R.id.signUpFragment_text1)
        val animator = ObjectAnimator.ofFloat(text1, "translationX", 0f, 100f)
        animator.duration = 2000
        animator.start()

        /*val text2 : TextView = view.findViewById(R.id.signUpFragment_text2)
        val animator1 = ObjectAnimator.ofFloat(text2, "translationX", 0f, 100f)
        animator1.duration = 2000
        animator1.start()*/


        val str = resources.getString(R.string.GoalsandFocus)
        val text2: TextView = view.findViewById(R.id.signUpFragment_text2)

        val handler1 = Handler(Looper.getMainLooper())
        var index = 0

        val runnable = object : Runnable {
            override fun run() {
                if (index < str.length) {
                    text2.append(str[index].toString())
                    index++
                    handler1.postDelayed(this, 200) // delay of 500ms between each character
                }
            }
        }
        handler1.post(runnable)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            parentFragmentManager.beginTransaction().remove(this).commit()

            val intent = Intent(activity,InputNameActivity::class.java)
            startActivity(intent)

        }, 3000)

        return view
    }
}

