package com.theambitiouscoder.firebasechatapp.ui.users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theambitiouscoder.firebasechatapp.model.User
import com.theambitiouscoder.firebasechatapp.repository.FirebaseRepository
import java.util.ArrayList

class UsersViewModel : ViewModel() {
    private var authRepository  = FirebaseRepository()

    var getProfileImageResponse = MutableLiveData<String>()
    var getErrorToastResponse = MutableLiveData<String>()
    var getUsersListResponse = MutableLiveData<ArrayList<User>>()

    init {
        getProfileImageResponse = authRepository.getProfileImageMutableLiveData
        getErrorToastResponse = authRepository.getToastErrorMutableLiveData
        getUsersListResponse = authRepository.getUsersListMutableLiveData
    }


    fun getProfileImage(){
        authRepository.getUserData()
    }

    fun getUsersList(){
        authRepository.getUsersList()
    }

}