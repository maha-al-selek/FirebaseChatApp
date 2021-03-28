package com.theambitiouscoder.firebasechatapp.ui.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theambitiouscoder.firebasechatapp.data.repositories.firebase.FirebaseRepoImpl

class ProfileViewModel : ViewModel() {

    private var authRepository  = FirebaseRepoImpl()
    var uploadImageResponse = MutableLiveData<Boolean>()
    var getUserNameResponse = MutableLiveData<String>()
    var getProfileImageResponse = MutableLiveData<String>()
    var getErrorToastResponse = MutableLiveData<String>()

    init {
        uploadImageResponse = authRepository.uploadImageMutableLiveData
        getUserNameResponse = authRepository.getUserNameMutableLiveData
        getProfileImageResponse = authRepository.getProfileImageMutableLiveData
        getErrorToastResponse = authRepository.getToastErrorMutableLiveData
    }


    fun uploadImage(filePath: Uri, username: String){
        authRepository.uploadImage(filePath, username )
    }

    fun getUserData(){
        authRepository.getUserData()
    }



}