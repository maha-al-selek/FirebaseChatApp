package com.theambitiouscoder.firebasechatapp.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theambitiouscoder.firebasechatapp.model.Chat
import com.theambitiouscoder.firebasechatapp.model.User
import com.theambitiouscoder.firebasechatapp.utils.Constants
import com.theambitiouscoder.firebasechatapp.utils.HelperClass.logErrorMessage
import java.util.*
import kotlin.collections.HashMap


class FirebaseRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseUser: FirebaseUser

    val authenticatedUserMutableLiveData = MutableLiveData<Boolean>()
    val createUserMutableLiveData = MutableLiveData<Boolean>()
    val uploadImageMutableLiveData = MutableLiveData<Boolean>()
    val getProfileImageMutableLiveData = MutableLiveData<String>()
    val getUserNameMutableLiveData = MutableLiveData<String>()
    val getToastErrorMutableLiveData = MutableLiveData<String>()
    val getUsersListMutableLiveData = MutableLiveData<ArrayList<User>>()
    val getMessagesMutableLiveData = MutableLiveData<ArrayList<Chat>>()

    var userList = ArrayList<User>()
    var chatList = ArrayList<Chat>()
    var topic = ""

    //Sign in using Email
    fun login(email: String, password: String){
        firebaseAuth!!.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener{
            if (it.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    authenticatedUserMutableLiveData.value = true
                }
            } else {
                it.exception!!.message?.let { it1 -> logErrorMessage(it1) }
                authenticatedUserMutableLiveData.value = false
            }
        }
    }

    //Sign Up using Email
    fun signUp(userName: String, email: String, password: String){
        firebaseAuth!!.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener{
            if (it.isSuccessful){
                val user: FirebaseUser? = firebaseAuth.currentUser
                val userId:String = user!!.uid

                databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_INSTANCE_URL).getReference("Users").child(userId)

                //Getting user details
                val hashMap:HashMap<String, String> = HashMap()
                hashMap.put("userId", userId)
                hashMap.put("userName", userName)
                hashMap.put("profileImage", "")

                //Sending user details to Firebase Real-Time Database
                databaseReference.setValue(hashMap).addOnCompleteListener{
                    createUserMutableLiveData.value = it.isSuccessful
                }
            }
        }
    }

    //Updating / Changing user name and image
    fun uploadImage(filePath: Uri,userName: String){
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        databaseReference =
                FirebaseDatabase.getInstance(Constants.FIREBASE_INSTANCE_URL).getReference("Users").child(firebaseUser.uid)

        var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())

          //Uploading image
          ref.putFile(filePath!!)
           .addOnSuccessListener {
                //Once image uploaded, get the Image download URL
               ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                   val hashMap: java.util.HashMap<String, String> = java.util.HashMap()
                   hashMap.put("userName",userName)
                   hashMap.put("profileImage",uri.toString())

                   //Updating username and image url
                   databaseReference.updateChildren(hashMap as Map<String, Any>)


                   uploadImageMutableLiveData.value = true
               })
           }
            .addOnFailureListener {
                uploadImageMutableLiveData.value = false
            }
    }

    //Get user name and profile image
    fun getUserData(){
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
                FirebaseDatabase.getInstance(Constants.FIREBASE_INSTANCE_URL).getReference("Users").child(firebaseUser.uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                getToastErrorMutableLiveData.value = error.message
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                getUserNameMutableLiveData.value = user!!.userName
                getProfileImageMutableLiveData.value = user.profileImage
            }
        })

    }

    //Getting all users
    fun getUsersList(){
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userid = firebase.uid

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")
        val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance(Constants.FIREBASE_INSTANCE_URL).getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                getToastErrorMutableLiveData.value = error.message
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    if (!user!!.userId.equals(firebase.uid)) {
                        userList.add(user)
                    }
                }
                getUsersListMutableLiveData.value = userList
            }
        })

    }

    //Inserting message to Database
    fun sendMessage(senderId: String, receiverId: String, message: String){
        var reference: DatabaseReference? = FirebaseDatabase.getInstance(Constants.FIREBASE_INSTANCE_URL).getReference()

        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        reference!!.child("Chat").push().setValue(hashMap)
    }

    //Getting all messages
    fun getMessages(senderId: String, receiverId: String){
        val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance(Constants.FIREBASE_INSTANCE_URL).getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                            chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }
                getMessagesMutableLiveData.value = chatList
            }
        })
    }

}