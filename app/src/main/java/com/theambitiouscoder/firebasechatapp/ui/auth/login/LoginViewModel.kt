package com.theambitiouscoder.firebasechatapp.ui.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theambitiouscoder.firebasechatapp.data.enums.Resource
import com.theambitiouscoder.firebasechatapp.data.repositories.firebase.FirebaseRepo
import com.theambitiouscoder.firebasechatapp.data.repositories.firebase.FirebaseRepoImpl

class LoginViewModel : ViewModel() {

    private var firebaseRepo: FirebaseRepo = FirebaseRepoImpl()

    val loginResponse: MutableLiveData<Resource> = firebaseRepo.authenticatedUserMutableLiveData

    fun userLogin(email: String, password: String) {
        firebaseRepo.login(email, password)
    }

}