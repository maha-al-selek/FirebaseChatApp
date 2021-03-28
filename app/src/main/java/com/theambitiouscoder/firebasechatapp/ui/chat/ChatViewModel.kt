package com.theambitiouscoder.firebasechatapp.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theambitiouscoder.firebasechatapp.data.models.Chat
import com.theambitiouscoder.firebasechatapp.data.models.PushNotification
import com.theambitiouscoder.firebasechatapp.data.repositories.firebase.FirebaseRepo
import com.theambitiouscoder.firebasechatapp.data.repositories.firebase.FirebaseRepoImpl
import com.theambitiouscoder.firebasechatapp.data.repositories.notifications.NotificationsRepo
import com.theambitiouscoder.firebasechatapp.data.repositories.notifications.NotificationsRepoImpl
import java.util.*

class ChatViewModel : ViewModel() {

    private var firebaseRepo: FirebaseRepo = FirebaseRepoImpl()
    private var notificationsRepo: NotificationsRepo = NotificationsRepoImpl()

    var getMessageResponse = MutableLiveData<ArrayList<Chat>>()
    var getProfileImageResponse = MutableLiveData<String>()

    init {
        getMessageResponse = firebaseRepo.getMessagesMutableLiveData
        getProfileImageResponse = firebaseRepo.getProfileImageMutableLiveData
    }

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        firebaseRepo.sendMessage(senderId, receiverId, message)
    }

    fun getMessages(senderId: String, receiverId: String) {
        firebaseRepo.getMessages(senderId, receiverId)
    }

    fun getProfileImage() {
        firebaseRepo.getUserData()
    }

    suspend fun postNotification(notification: PushNotification) =
        notificationsRepo.postNotification(notification)

}