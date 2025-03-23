package com.example.grouponproduceapp.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.grouponproduceapp.PreferenceManager
import com.example.grouponproduceapp.Utils
import com.example.grouponproduceapp.Utils.hideDialog
import com.example.grouponproduceapp.Utils.showToast
import com.example.grouponproduceapp.activity.AdminMainActivity
import com.example.grouponproduceapp.activity.UsersMainActivity
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        val prefs = PreferenceManager(requireContext())
        prefs.clearUserType()

        statusBarColor()
        onLoginBtnClicked()
        onRegisterBtnClicked()
        onForgotPasswordClicked() // Added Forgot Password Click Listener
        return binding.root
    }

    private fun onRegisterBtnClicked() {
        binding.btnRegisterOption.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun onLoginBtnClicked() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val passwd = binding.etUserPswdLogin.text.toString().trim()

            val db = FirebaseFirestore.getInstance()
            if (email.isNotEmpty() && passwd.isNotEmpty()) {
                Utils.showDialog(requireContext(), "Signing you in...")
                firebaseAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener {
                    hideDialog()
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val userId = user!!.uid
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    if (document.getString("role") == "admin") {
                                        val intent = Intent(requireActivity(), AdminMainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
                                    }
                                }
                            }
                            .addOnFailureListener { exception ->
                                showToast(requireContext(), "Error fetching data: ${exception.message}")
                            }
                    } else {
                        showToast(requireContext(), "Email or Password is incorrect")
                    }
                }
            } else {
                showToast(requireContext(), "Empty Fields are not allowed")
            }
        }
    }

    private fun onForgotPasswordClicked() {
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()

            if (email.isEmpty()) {
                showToast(requireContext(), "Please enter your registered email first.")
            } else {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast(requireContext(), "Password reset email sent. Check your inbox.")
                        } else {
                            showToast(requireContext(), "Failed to send reset email. Please try again.")
                        }
                    }
            }
        }
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(
                requireContext(),
                R.color.background_page
            )
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
