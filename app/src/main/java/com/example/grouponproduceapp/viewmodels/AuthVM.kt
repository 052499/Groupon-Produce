package com.example.grouponproduceapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.grouponproduceapp.Utils
import kotlinx.coroutines.flow.MutableStateFlow

class AuthVM: ViewModel() {

    private val _isSignedInSuccessfully = MutableStateFlow(false)
    val isSignedInSuccessfully= _isSignedInSuccessfully



    private val _isCurrentUser = MutableStateFlow(false)
    val isCurrentUser= _isCurrentUser

    init {
        Utils.getAuthInstance().currentUser?.let { _isCurrentUser.value = true }
    }

}