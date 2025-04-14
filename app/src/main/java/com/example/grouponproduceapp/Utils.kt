package com.example.grouponproduceapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.grouponproduceapp.databinding.DialogStatusBinding
import com.google.firebase.auth.FirebaseAuth

object Utils {
    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, message: String){
        val progress= DialogStatusBinding.inflate(LayoutInflater.from(context))
        progress.tvProgress.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }
    fun hideDialog(){
        dialog?.dismiss()
    }

    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var firebaseAuthInstance: FirebaseAuth? = null
    fun getAuthInstance() : FirebaseAuth {
        if (firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun getCurrentUserId(): String {
        val user= FirebaseAuth.getInstance().currentUser
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

}