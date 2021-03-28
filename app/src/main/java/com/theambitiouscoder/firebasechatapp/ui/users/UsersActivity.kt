package com.theambitiouscoder.firebasechatapp.ui.users

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.theambitiouscoder.firebasechatapp.R
import com.theambitiouscoder.firebasechatapp.data.backgroundServices.FirebaseService
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.theambitiouscoder.firebasechatapp.ui.profile.ProfileActivity
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