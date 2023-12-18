package com.bobi.vendorium.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bobi.vendorium.R
import com.bobi.vendorium.activities.MainActivity
import com.bobi.vendorium.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var backToProfileTextView: TextView
    private lateinit var nameEditText: TextInputEditText
    private lateinit var surnameEditText: TextInputEditText
    private lateinit var addressEditText: TextInputEditText
    private lateinit var contactEditText: TextInputEditText
    private lateinit var emailTextView: TextView
    private lateinit var saveProfileButton: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fetchedUser: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");

        //Fetching user & his data
        val user: FirebaseUser? = firebaseAuth.currentUser

        backToProfileTextView = view.findViewById(R.id.backToProfileButton);
        emailTextView = view.findViewById(R.id.emailTextView);
        nameEditText = view.findViewById(R.id.nameEditText);
        surnameEditText = view.findViewById(R.id.surnameEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        contactEditText = view.findViewById(R.id.contactEditText);
        saveProfileButton = view.findViewById(R.id.saveProfileButton);

        fetchUserData(user!!.uid)
        backToProfileTextView.setOnClickListener { openProfileFragment() }
        saveProfileButton.setOnClickListener { saveProfile() }
    }

    private fun openProfileFragment() {
        (activity as? MainActivity)?.setUserFragment();
    }

    private fun fetchUserData(userUid: String) {

        Log.d("DEBUG", "Fetching user data ...")
        dbRef.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fetchedUser = dataSnapshot.getValue(User::class.java)!!
                fetchedUser.let {
                    Log.d("DEBUG", "User name: ${fetchedUser.email}")
                    displayUserData(fetchedUser);
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ERROR", "Fetch user error ${databaseError.message}")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun displayUserData(fetchedUser: User) {
        Log.d("DEBUG", "Displaying user data ...")

        emailTextView.text = fetchedUser.email;

        if (fetchedUser.name != "") {
            nameEditText.setText(fetchedUser.name);
        }
        if (fetchedUser.surname != "") {
            surnameEditText.setText(fetchedUser.surname);
        }
        if (fetchedUser.address != "") {
            addressEditText.setText(fetchedUser.address);
        }
        if (fetchedUser.contactNumber != "") {
            contactEditText.setText(fetchedUser.contactNumber);
        }
    }

    private fun saveProfile() {
        val updatedUser = HashMap<String, Any>()
        updatedUser["name"] = nameEditText.text.toString();
        updatedUser["surname"] = surnameEditText.text.toString();
        updatedUser["address"] = addressEditText.text.toString();
        updatedUser["contactNumber"] = contactEditText.text.toString();

        dbRef.child(fetchedUser.userUID).updateChildren(updatedUser)
            .addOnSuccessListener {
                Log.d("DEBUG", "Edited user data: $updatedUser");
                openProfileFragment();
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR", "Update item error: ${exception.message}");
            }
    }
}