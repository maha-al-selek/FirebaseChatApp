package com.theambitiouscoder.firebasechatapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.theambitiouscoder.firebasechatapp.R
import com.theambitiouscoder.firebasechatapp.data.enums.Resource
import com.theambitiouscoder.firebasechatapp.ui.auth.signup.SignUpActivity
import com.theambitiouscoder.firebasechatapp.ui.users.UsersActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginViewModel.loginResponse.observe(this, Observer {
            Log.d("LoginActivity", "The value of result is $it")

            when (it) {

                Resource.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }

                Resource.COMPLETED -> {
                    progressBar.visibility = View.GONE

                    etEmail.setText("")
                    etPassword.setText("")
                    val intent = Intent(
                        this@LoginActivity,
                        UsersActivity::class.java
                    )
                    startActivity(intent)
                    finish()
                }

                Resource.FAILED -> {
                    progressBar.visibility = View.GONE

                    Toast.makeText(
                        applicationContext,
                        "email or password invalid",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })


        //check if user login then navigate to user screen
        if (firebaseUser != null) {
            val intent = Intent(
                this@LoginActivity,
                UsersActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(
                    applicationContext,
                    "email and password are required",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                loginViewModel.userLogin(email, password)
            }
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(
                this@LoginActivity,
                SignUpActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}