package com.example.grouponproduceapp.userFragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.activity.UsersMainActivity
import com.example.grouponproduceapp.databinding.FragmentProfileAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileAdminBinding
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileAdminBinding.inflate(inflater, container, false)

        statusBarColor()

        (activity as? UsersMainActivity)?.hideCheckoutButton()
        fetchUserData()

        binding.btnSave.setOnClickListener {
            saveUpdatedProfileData()
        }

        return binding.root
    }

    private fun fetchUserData() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot: DocumentSnapshot = task.result!!

                    if (documentSnapshot.exists()) {
                        // Populate the fields with the fetched data

                        binding.etName.setText(documentSnapshot.getString("name"))
                        binding.tvEmail.setText(documentSnapshot.getString("email"))
                        binding.etAddress.setText(documentSnapshot.getString("address"))
                    } else {
                        Toast.makeText(context, "No user data found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "No authenticated user found", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to save updated user data to Firestore
    private fun saveUpdatedProfileData() {
        val updatedName = binding.etName.text.toString()
        val updatedAddress = binding.etAddress.text.toString()

        // Validate that the fields are not empty
        if (updatedName.isEmpty() || updatedAddress.isEmpty()) {
            Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the current user and their UID
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = db.collection("users").document(userId)

            // Create a map of the updated data
            val updatedData = hashMapOf(
                "name" to updatedName,
                "address" to updatedAddress,
            )

            // Update the user's data in Firestore
            userDocRef.update(updatedData.toMap())
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.homeFragment)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No authenticated user found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(
                requireContext(),
                R.color.background_page )
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
