package com.theambitiouscoder.firebasechatapp.ui.users

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.theambitiouscoder.firebasechatapp.R
import com.theambitiouscoder.firebasechatapp.repository.FirebaseService
import com.theambitiouscoder.firebasechatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.theambitiouscoder.firebasechatapp.ui.user.profile.ProfileActivity
import com.theambitiouscoder.firebasechatapp.utils.Constants.Companion.FIREBASE_INSTANCE_URL
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {
    private lateinit var userViewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        userViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

        userViewModel.getProfileImageResponse.observe(this, Observer{
            if(it==""){
                imgProfile.setImageResource(R.drawable.profile_image)
            }
            else{
                Glide.with(this@UsersActivity).load(it).into(imgProfile)
            }
        })

        userViewModel.getUsersListResponse.observe(this, Observer{
            if(it != null){
                val userAdapter = UserAdapter(this@UsersActivity, it)

                userRecyclerView.adapter = userAdapter
            }
        })

        FirebaseService.sharedPref = getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }

        userRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgProfile.setOnClickListener {
            val intent = Intent(
                this@UsersActivity,
                ProfileActivity::class.java
            )
            startActivity(intent)
        }

        getProfileImage()
        getUsersList()
    }

    private fun getProfileImage() {
        userViewModel.getProfileImage()
    }

    fun getUsersList() {
        userViewModel.getUsersList()
    }
}