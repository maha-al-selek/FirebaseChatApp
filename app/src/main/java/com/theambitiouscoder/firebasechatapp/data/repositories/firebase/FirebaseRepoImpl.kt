package com.theambitiouscoder.firebasechatapp.data.repositories.firebase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theambitiouscoder.firebasechatapp.data.enums.Resource
import com.theambitiouscoder.firebasechatapp.data.models.Chat
import com.theambitiouscoder.firebasechatapp.data.models.User
import com.theambitiouscoder.firebasechatapp.ui.utils.Logger
import java.util.*
import kotlin.collections.HashMap

class FirebaseRepoImpl : FirebaseRepo {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseUser: FirebaseUser

    override val authenticatedUserMutableLiveData = MutableLiveData<Resource>()
    override val createUserMutableLiveData = MutableLiveData<Boolean>()
    override val uploadImageMutableLiveData = MutableLiveData<Boolean>()
    override val getProfileImageMutableLiveData = MutableLiveData<String>()
    override val getUserNameMutableLiveData = MutableLiveData<String>()
    override val getToastErrorMutableLiveData = MutableLiveData<String>()
    override val getUsersListMutableLiveData = MutableLiveData<ArrayList<User>>()
    override val getMessagesMutableLiveData = MutableLiveData<ArrayList<Chat>>()

    override var userList = ArrayList<User>()
    override var chatList = ArrayList<Chat>()

    //Sign in using Email
    override fun login(email: String, password: String) {
        authenticatedUserMutableLiveData.value = Resource.LOADING
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        authenticatedUserMutableLiveData.value = Resource.COMPLETED
                    }
                } else {
                    Logger.log("Login: Exception - ${it.exception}")
                    authenticatedUserMutableLiveData.value = Resource.FAILED
                }
            }
    }

    //Sign Up using Email
    override fun signUp(userName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    val userId: String = user!!.uid

                    databaseReference =
                        FirebaseDatabase.getInstance()
                            .getReference("Users").child(userId)

                    //Getting user details
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["userName"] = userName
                    hashMap["profileImage"] = ""

                    //Sending user details to Firebase Real-Time Database
                    databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                        createUserMutableLiveData.value = task.isSuccessful
                    }
                } else {
                    Logger.log("SignUp: ${it.exception}")
                }
            }
    }

    //Updating / Changing user name and image
    override fun uploadImage(filePath: Uri, userName: String) {
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.uid)

        val ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())

        //Uploading image
        ref.putFile(filePath)
            .addOnSuccessListener {
                //Once image uploaded, get the Image download URL
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val hashMap: java.util.HashMap<String, String> = java.util.HashMap()
                    hashMap["userName"] = userName
                    hashMap["profileImage"] = uri.toString()

                    //Updating username and image url
                    databaseReference.updateChildren(hashMap as Map<String, Any>)


                    uploadImageMutableLiveData.value = true
                }
            }
            .addOnFailureListener {
                uploadImageMutableLiveData.value = false
            }
    }

    //Get user name and profile image
    override fun getUserData() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.uid)

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
    override fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val userId = firebase.uid

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userId")
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                getToastErrorMutableLiveData.value = error.message
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user: User? = dataSnapShot.getValue(User::class.java)
                    if (!user!!.userId.equals(firebase.uid, false)) {
                        userList.add(user)
                    }
                }
                getUsersListMutableLiveData.value = userList
            }
        })

    }

    //Inserting message to Database
    override fun sendMessage(senderId: String, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        reference.child("Chat").push().setValue(hashMap)
    }

    //Getting all messages
    override fun getMessages(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    dataSnapShot.getValue(Chat::class.java)?.let { chat ->
                        if ((chat.senderId.equals(senderId, false) &&
                                    chat.receiverId.equals(receiverId, false)) ||
                            (chat.senderId.equals(receiverId, false) &&
                                    chat.receiverId.equals(senderId, false))
                        ) {
                            chatList.add(chat)
                        }
                    }
                }
                getMessagesMutableLiveData.value = chatList
            }
        })
    }

}