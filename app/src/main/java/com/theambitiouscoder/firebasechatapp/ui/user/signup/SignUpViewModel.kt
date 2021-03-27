package com.theambitiouscoder.firebasechatapp.ui.user.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theambitiouscoder.firebasechatapp.repository.FirebaseRepository

class SignUpViewModel : ViewModel() {
    private var authRepository  = FirebaseRepository()
    var signupResponse = MutableLiveData<Boolean>()

    init {
        signupResponse = authRepository.createUserMutableLiveData
    }


    fun userSignup(userName:String,email:String,password:String){
        authRepository.signUp(userName, email, password)
    }


}
