package com.bobi.vendorium.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bobi.vendorium.R
import com.bobi.vendorium.activities.ListingDetailsActivity
import com.bobi.vendorium.adapters.OglAdapter
import com.bobi.vendorium.models.Listing
import com.bobi.vendorium.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyListingsFragment : Fragment(R.layout.fragment_my_listings) {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fetchedUser: User

    private lateinit var userListings: ArrayList<Listing>

    private lateinit var myListingsRecyclerView: RecyclerView
    private lateinit var loadingDataTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Listings");

        //Fetching user & his data
        val user: FirebaseUser? = firebaseAuth.currentUser

        myListingsRecyclerView = view.findViewById(R.id.myListingsRecyclerView)
        myListingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myListingsRecyclerView.setHasFixedSize(true)
        loadingDataTextView = view.findViewById(R.id.loadingDataTextView)
        userListings = arrayListOf()

        fetchUserListings(user!!.uid);
    }


    private fun fetchUserListings(userUid: String) {
        Log.d("DEBUG", "Fetching user listings. . . ")
        Log.d("DEBUG", "Owner ID : $userUid")
        myListingsRecyclerView.visibility = View.GONE
        loadingDataTextView.visibility = View.VISIBLE
        userListings.clear()

        databaseRef.orderByChild("ownerID").equalTo(userUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (listingSnapshot in dataSnapshot.children) {
                            val listing = listingSnapshot.getValue(Listing::class.java)
                            listing?.let {
                                userListings.add(it)
                                Log.d("DEBUG", "Fetchan listing: ${it.name}")
                            }
                        }
                        displayMyListings();
                    }
                    else{
                        Log.d("DEBUG", "No user listings found!")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }


    private fun displayMyListings() {

        val mAdapter = OglAdapter(userListings)
        myListingsRecyclerView.adapter = mAdapter

        mAdapter.setOnItemClickListener(object : OglAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedListing = userListings[position]
                val intent = Intent(requireContext(), ListingDetailsActivity::class.java)
                intent.putExtra("selectedListing", selectedListing.name)
                intent.putExtra("listingPrice", selectedListing.price)
                intent.putExtra("listingImage", selectedListing.images[0])
                intent.putExtra("listingDescription", selectedListing.description)
                intent.putExtra("listingOwnerID", selectedListing.ownerID)
                intent.putExtra("listingID", selectedListing.id)
                startActivity(intent)
            }
        })

        myListingsRecyclerView.visibility = View.VISIBLE
        loadingDataTextView.visibility = View.GONE
    }
}