package com.bobi.vendorium.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bobi.vendorium.R
import com.bobi.vendorium.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var dbRef: DatabaseReference

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        mAuth = FirebaseAuth.getInstance()
        val registerButton = findViewById<TextView>(R.id.registerButton)
        val emailEditText = findViewById<TextInputEditText>(R.id.registerEmailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.registerPasswordEditText)
        val repeatPasswordEditText =
            findViewById<TextInputEditText>(R.id.registerRepeatPasswordEditText)
        val backToLoginTextView = findViewById<TextView>(R.id.backToLoginTextView)


        backToLoginTextView.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val repeatPassword = repeatPasswordEditText.text.toString()
            checkInsertsThenRegisterUser(email, password, repeatPassword)
        }
    }

    private fun checkInsertsThenRegisterUser(
        email: String, password: String, repeatPassword: String
    ) {
        if (!isValidEmail(email)) {
            Toast.makeText(
                this@RegisterActivity, "Registration failed. Email incorrect!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (repeatPassword == "" || password == "" || email == "") {
            Toast.makeText(
                this@RegisterActivity,
                "Registration failed. Please insert password!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (repeatPassword != password) {
            Toast.makeText(
                this@RegisterActivity,
                "Registration failed. Passwords do not match!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        registerUser(email, password)
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun registerUser(email: String, password: String) {
        println("Email: $email Pass: $password")
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("DEBUG", "createUserWithEmail:success")
                val user = mAuth!!.currentUser


                //Save user to database
                val newUser = User(
                    userUID = user!!.uid,
                    email = email
                )

            dbRef.child(user!!.uid).setValue(newUser)
                .addOnCompleteListener {
                    registerSuccessful(user)
                    Log.d("DEBUG", "New user saved to database!")
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this@RegisterActivity, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }


            } else {
                // If sign in fails, display a message to the user.
                Log.w("DEBUG", "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    this@RegisterActivity, task.exception?.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun registerSuccessful(user: FirebaseUser?) {
        Log.d("DEBUG", "Signing in successful: " + user!!.displayName)
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("firebaseUser", user)
        startActivity(intent)
        finish()
    }
}