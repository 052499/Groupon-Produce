package com.example.grouponproduceapp.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.grouponproduceapp.Utils
import com.example.grouponproduceapp.activity.AdminMainActivity
import com.example.grouponproduceapp.activity.UsersMainActivity
import com.example.grouponproduceapp.viewmodels.AuthVM
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private val authVM: AuthVM by viewModels()
    lateinit var binding: FragmentSplashBinding
    lateinit var userId: String
    var userName: String? = ""
    var role: String? = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        statusBarColor()

        val firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                val user = firebaseAuth.currentUser

                authVM.isCurrentUser.collect {
                    if (it) {
                        userId = user!!.uid
//                        fetchUserData()
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    userName = document.getString("name")
                                    role = document.getString("role")
                                    if (role == "admin") {
                                        val intent = Intent(requireActivity(), AdminMainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
                                    }
                                } else {
                                    findNavController().navigate(R.id.loginFragment)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Utils.showToast(requireContext(), "Error fetching data: ${exception.message}")
                            }
                    }
                    else {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                }
            }
        }, 1500)
        return binding.root
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.white)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}