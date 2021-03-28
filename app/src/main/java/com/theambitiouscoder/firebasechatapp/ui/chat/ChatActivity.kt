package com.theambitiouscoder.firebasechatapp.ui.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.theambitiouscoder.firebasechatapp.R
import com.theambitiouscoder.firebasechatapp.data.models.PushNotification
import com.theambitiouscoder.firebasechatapp.ui.utils.Logger
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.imgBack
import kotlinx.android.synthetic.main.activity_chat.imgProfile
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    private lateinit var chatViewModel: ChatViewModel

    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var topic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        chatViewModel.getMessageResponse.observe(this, Observer {
            if (it != null) {
                val chatAdapter = ChatAdapter(this@ChatActivity, it)

                chatRecyclerView.adapter = chatAdapter
            }
        })

        chatViewModel.getProfileImageResponse.observe(this, Observer {
            if (it == "") {
                imgProfile.setImageResource(R.drawable.profile_image)
            } else {
                Glide.with(this@ChatActivity).load(it).into(imgProfile)
            }
        })

        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val intent = intent
        val userId = intent.getStringExtra("userId")
        var userName = intent.getStringExtra("userName")

        imgBack.setOnClickListener {
            onBackPressed()
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userId!!)

        chatViewModel.getProfileImage()

        btnSendMessage.setOnClickListener {
            var message: String = etMessage.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
                etMessage.setText("")
            } else {
                sendMessage(firebaseUser!!.uid, userId, message)
                etMessage.setText("")
                /*  topic = "/topics/$userId"
                  PushNotification(NotificationData( userName!!,message),
                  topic).also {
                      sendNotification(it)
                  } */

            }
        }
        readMessage(firebaseUser!!.uid, userId)
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        chatViewModel.sendMessage(senderId, receiverId, message)
    }

    fun readMessage(senderId: String, receiverId: String) {
        chatViewModel.getMessages(senderId, receiverId)
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = chatViewModel.postNotification(notification)
                if (response.isSuccessful) {
                    Logger.log("Response: ${Gson().toJson(response)}")
                } else {
                    Logger.log("Error Body: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Logger.log("Exception: $e")
            }
        }

}