package com.theambitiouscoder.firebasechatapp.ui.user.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theambitiouscoder.firebasechatapp.repository.FirebaseRepository


class LoginViewModel : ViewModel() {

    private var authRepository  = FirebaseRepository()
    var loginResponse = MutableLiveData<Boolean>()

    init {
        loginResponse = authRepository.authenticatedUserMutableLiveData
    }


    fun userLogin(email: String, password: String){
        authRepository.login(email, password)
    }


}