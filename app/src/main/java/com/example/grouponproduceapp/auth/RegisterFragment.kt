package com.example.grouponproduceapp.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.grouponproduceapp.PreferenceManager
import com.example.grouponproduceapp.Utils
import com.example.grouponproduceapp.activity.AdminMainActivity
import com.example.grouponproduceapp.activity.UsersMainActivity
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    lateinit var email: String
    lateinit var password: String
    lateinit var name: String
    lateinit var uType: String
    lateinit var address: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        statusBarColor()

        val db = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()

//        val userTypes = UserType.values().map { it.name }.toList() // Get the enum values as a list of strings
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userTypes)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerUserType.adapter = adapter

        val userTypes = UserType.values().map { it.name }.toList() // Get the enum values as a list of strings
        val adapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, userTypes) // Use simple_dropdown_item_1line for dropdown items
        binding.actvUserType.setAdapter(adapter)


        binding.actvUserType.setOnClickListener {
//            Log.d("AutoComplete", "Clicked on AutoCompleteTextView")
            // Optionally force the dropdown to show by calling showDropDown() programmatically
            binding.actvUserType.showDropDown()
        }



        binding.btnRegister.setOnClickListener {
            email = binding.etEmailRegister.text.toString()
            password = binding.etUserPswdRegister.text.toString()
            name = binding.etUsername.text.toString()
//            uType = binding.spinnerUserType.selectedItem.toString()  // Get selected user type
            uType = binding.actvUserType.text.toString()  // Get selected user type
            address = binding.etUserAddress.text.toString()

            if (email.isNotEmpty() and !password.isEmpty() and name.isNotEmpty()) {
                if (uType == "ADMIN" || uType == "USER"){
                    firebaseAuth.createUserWithEmailAndPassword(email!!, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val userCreated = firebaseAuth.currentUser
                                val userMap = hashMapOf(
                                    "uid" to userCreated?.uid,
                                    "name" to name,
                                    "email" to email,
                                    "role" to uType,
                                    "address" to address
                                )
                                userCreated?.let {
                                    db.collection("users").document(it.uid).set(userMap)
                                        .addOnSuccessListener {

                                            val prefs = PreferenceManager(requireContext())
                                            prefs.saveUserType(uType)

                                            if (uType == "ADMIN") {
                                                startActivity(
                                                    Intent(
                                                        requireActivity(),
                                                        AdminMainActivity::class.java
                                                    )
                                                )
                                                requireActivity().finish()
                                                Utils.hideDialog()
                                            } else if (uType == "USER") {
                                                startActivity(
                                                    Intent(
                                                        requireActivity(),
                                                        UsersMainActivity::class.java
                                                    )
                                                )
                                                requireActivity().finish()
                                            }

                                        }
                                        .addOnFailureListener { e ->
                                            Log.d("----------------", e.message.toString())
                                        }
                                }
                            } else {
                                Utils.showToast(
                                    requireContext(),
                                    "Registration failed: ${it.exception?.message}"
                                )
                                Utils.hideDialog()
                            }
                        }
                }
                else {
                    Utils.showToast(requireContext(), "Select Usertype ADMIN or USER")
                }
            } else {
                Utils.showToast(requireContext(), "Empty fields are not allowed")
            }
        }
        return binding.root
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

enum class UserType {
    SELECT_USER_TYPE, ADMIN, USER
}