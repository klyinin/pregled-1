package com.bobi.vendorium.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bobi.vendorium.R
import com.bobi.vendorium.fragments.AddListingFragment
import com.bobi.vendorium.fragments.EditProfileFragment
import com.bobi.vendorium.fragments.FavouritesFragment
import com.bobi.vendorium.fragments.LanguagePickerDialogFragment
import com.bobi.vendorium.fragments.ListingsFragment
import com.bobi.vendorium.fragments.MyListingsFragment
import com.bobi.vendorium.fragments.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity(), LanguagePickerDialogFragment.NoticeDialogListener {

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null

    private lateinit var userFragment: Fragment
    private lateinit var listingFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the window's soft input mode to adjustResize
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        userFragment = UserFragment()
        listingFragment = ListingsFragment()
        val favouritesFragment = FavouritesFragment()
        val listingFragment = ListingsFragment()
        val addListingFragment = AddListingFragment()

        //init firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance()

        val user: FirebaseUser? = firebaseAuth?.currentUser
        Log.d("DEBUG", "logged user: " + user?.email)

        setCurrentFragment(addListingFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.favourites -> setCurrentFragment(favouritesFragment)
                R.id.add -> setCurrentFragment(addListingFragment)
                R.id.listings -> setCurrentFragment(listingFragment)
                R.id.user -> setCurrentFragment(userFragment)
            }
            true
        }
    }

    //for sing out button
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    fun setEditProfileFragment() {
        Log.d("DEBUG", "changing fragment to edit profile");
        val editProfileFragment = EditProfileFragment()
        setCurrentFragment(editProfileFragment);
    }

    fun setMyListingsFragment() {
        Log.d("DEBUG", "changing fragment to my listings page");
        val myListingsFragment = MyListingsFragment()
        setCurrentFragment(myListingsFragment);
    }
    fun setUserFragment() {
        setCurrentFragment(userFragment);
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        Log.d("DEBUG", "Language Changed!")
        val intent = intent
        finish()
        startActivity(intent)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User touched the dialog's negative button
    }
}

