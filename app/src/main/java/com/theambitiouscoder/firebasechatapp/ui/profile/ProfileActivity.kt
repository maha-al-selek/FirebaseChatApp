package com.theambitiouscoder.firebasechatapp.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.theambitiouscoder.firebasechatapp.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_users.imgBack
import kotlinx.android.synthetic.main.activtiy_profile.*
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel

    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST: Int = 2020

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activtiy_profile)

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        profileViewModel.uploadImageResponse.observe(this, Observer{
            if (it){
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Uploaded", Toast.LENGTH_SHORT).show()
                btnSave.visibility = View.GONE
            }else{
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Failed $it", Toast.LENGTH_SHORT)
                        .show()
            }
        })

        profileViewModel.getUserNameResponse.observe(this, Observer{
            etUserName.setText(it)
        })

        profileViewModel.getProfileImageResponse.observe(this, Observer{
            if(it==""){
                userImage.setImageResource(R.drawable.profile_image)
            }
            else{
                Glide.with(this@ProfileActivity).load(it).into(userImage)
            }
        })

        profileViewModel.getErrorToastResponse.observe(this, Observer{
            if(it != null || it != ""){
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        })

        getUserData()

        imgBack.setOnClickListener {
            onBackPressed()
        }

        userImage.setOnClickListener {
            chooseImage()
        }

        btnSave.setOnClickListener {
            uploadImage()
            progressBar.visibility = View.VISIBLE
        }

    }

    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null) {
            filePath = data!!.data
            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                userImage.setImageBitmap(bitmap)
                btnSave.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            profileViewModel.uploadImage(filePath!!,etUserName.text.toString())
        }
    }

    private fun getUserData() {
        profileViewModel.getUserData()
    }

}