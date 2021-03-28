package com.theambitiouscoder.firebasechatapp.data.repositories.firebase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.theambitiouscoder.firebasechatapp.data.enums.Resource
import com.theambitiouscoder.firebasechatapp.data.models.Chat
import com.theambitiouscoder.firebasechatapp.data.models.User
import java.util.*

interface FirebaseRepo {

    val authenticatedUserMutableLiveData: MutableLiveData<Resource>
    val createUserMutableLiveData: MutableLiveData<Boolean>
    val uploadImageMutableLiveData: MutableLiveData<Boolean>
    val getProfileImageMutableLiveData: MutableLiveData<String>
    val getUserNameMutableLiveData: MutableLiveData<String>
    val getToastErrorMutableLiveData: MutableLiveData<String>
    val getUsersListMutableLiveData: MutableLiveData<ArrayList<User>>
    val getMessagesMutableLiveData: MutableLiveData<ArrayList<Chat>>

    var userList: ArrayList<User>
    var chatList: ArrayList<Chat>

    fun login(email: String, password: String)

    fun signUp(userName: String, email: String, password: String)

    fun uploadImage(filePath: Uri, userName: String)

    fun getUserData()

    fun getUsersList()

    fun sendMessage(senderId: String, receiverId: String, message: String)

    fun getMessages(senderId: String, receiverId: String)

}