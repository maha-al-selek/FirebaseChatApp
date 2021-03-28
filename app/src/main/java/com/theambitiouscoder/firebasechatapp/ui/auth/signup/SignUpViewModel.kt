package com.theambitiouscoder.firebasechatapp.ui.auth.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theambitiouscoder.firebasechatapp.data.repositories.firebase.FirebaseRepo
import com.theambitiouscoder.firebasechatapp.data.repositories.firebase.FirebaseRepoImpl

class SignUpViewModel : ViewModel() {

    private var firebaseRepo: FirebaseRepo = FirebaseRepoImpl()

    val signUpResponse: MutableLiveData<Boolean> = firebaseRepo.createUserMutableLiveData

    fun userSignUp(userName: String, email: String, password: String) {
        firebaseRepo.signUp(userName, email, password)
    }

}
