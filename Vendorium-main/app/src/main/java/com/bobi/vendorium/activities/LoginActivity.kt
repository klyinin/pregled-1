package com.bobi.vendorium.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bobi.vendorium.R
import com.bobi.vendorium.helpers.ToastHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = firebaseAuth?.currentUser
        if (currentUser != null) {
            Log.d("DEBUG", "User found from session! Logging in")
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val signInButton = findViewById<TextView>(R.id.signInButton)
        val signInGoogleButton = findViewById<ImageView>(R.id.signInGoogleButton)
        val registerHereTextView = findViewById<TextView>(R.id.signUpTextView)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            Log.d("DEBUG", "Signing in with email: $email")

            signIn(email, password)
        }

        //signInGoogleButton.setOnClickListener { createSignInIntent() }
        //signInFacebookButton.setOnClickListener { createSignInIntent() }

        registerHereTextView.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signIn(email: String, password: String) {
        if (email.length < 2 || password.length < 2) {
            //Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
            Log.d("DEBUG", "Incorrect Data! Showing toast...")
            ToastHelper.createShortToast(
                this@LoginActivity,
                "Please correctly insert email and password"
            )
            return
        }

        firebaseAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = firebaseAuth!!.currentUser
                Log.d("DEBUG", "Authentication successful: " + user?.email)
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra("firebaseUser", user)
                startActivity(intent)
                finish()
            } else {
                // If sign in fails, display a message to the user.
                ToastHelper.createShortToast(this, "Incorrect username or password!")
            }
        }
    }
}