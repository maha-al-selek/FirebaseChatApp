package com.theambitiouscoder.firebasechatapp.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theambitiouscoder.firebasechatapp.model.Chat
import com.theambitiouscoder.firebasechatapp.repository.FirebaseRepository
import java.util.ArrayList

class ChatViewModel : ViewModel() {
    private var authRepository  = FirebaseRepository()

    var getMessageResponse = MutableLiveData<ArrayList<Chat>>()
    var getProfileImageResponse = MutableLiveData<String>()
    init {
        getMessageResponse = authRepository.getMessagesMutableLiveData
        getProfileImageResponse = authRepository.getProfileImageMutableLiveData
    }

    fun sendMessage(senderId: String, receiverId: String, message: String){
        authRepository.sendMessage(senderId, receiverId, message)
    }

    fun getMessages(senderId: String, receiverId: String){
        authRepository.getMessages(senderId, receiverId)
    }

    fun getProfileImage(){
        authRepository.getUserData()
    }

}