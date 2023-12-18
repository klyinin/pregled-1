package com.bobi.vendorium.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bobi.vendorium.R
import com.bobi.vendorium.activities.MainActivity
import com.bobi.vendorium.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserFragment : Fragment(R.layout.fragment_user){

    private lateinit var signOutTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var fullNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var contactTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var myListingsButton: Button
    private lateinit var languageImageButton: ImageButton

    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        //Fetching user & his data
        val user: FirebaseUser? = firebaseAuth?.currentUser

        signOutTextView = view.findViewById(R.id.signOutButton);
        addressTextView = view.findViewById(R.id.addressTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        contactTextView = view.findViewById(R.id.contactTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        myListingsButton = view.findViewById(R.id.myListingsButton);
        languageImageButton = view.findViewById(R.id.languageImageButton);

        fetchUserData(user!!.uid)
        signOutTextView.setOnClickListener { (activity as? MainActivity)?.signOut() };
        editProfileButton.setOnClickListener { openEditProfileFragment() }
        myListingsButton.setOnClickListener { openMyListingsFragment() }
        languageImageButton.setOnClickListener { openLanguageSelectionModal() }
    }

    private fun openEditProfileFragment(){
        (activity as? MainActivity)?.setEditProfileFragment();
    }

    private fun openMyListingsFragment(){
        (activity as? MainActivity)?.setMyListingsFragment();
    }

    private fun openLanguageSelectionModal(){
        Log.d("DEBUG", "Opening language selection modal ...")
        val newFragment = LanguagePickerDialogFragment()
        newFragment.show(requireActivity().supportFragmentManager, "language selection")
    }

    private fun fetchUserData(userUid: String) {

        Log.d("DEBUG", "Fetching user data ...")
        databaseRef.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Item exists in the database
                val fetchedUser =
                    dataSnapshot.getValue(User::class.java) // Assuming you have a model class called Item
                fetchedUser?.let {
                    // Do something with the fetched item
                    // For example, print the item's name

                    Log.d("DEBUG", "User name: ${fetchedUser.email}")
                    displayUserData(fetchedUser);
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur while fetching the item
                Log.d("ERROR", "Fetch user error ${databaseError.message}")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun displayUserData(fetchedUser:User){
        Log.d("DEBUG", "Displaying user data ...")

        if(fetchedUser.address != ""){
            addressTextView.text = fetchedUser.address;
        }
        else{
            addressTextView.text = "/";
        }

        if(fetchedUser.contactNumber != ""){
            contactTextView.text = fetchedUser.contactNumber;
        }
        else{
            contactTextView.text = "/"
        }

        emailTextView.text = fetchedUser.email;
        fullNameTextView.text = fetchedUser.name + " " + fetchedUser.surname;
    }
}