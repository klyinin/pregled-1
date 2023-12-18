package com.bobi.vendorium.activities

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bobi.vendorium.R
import com.bobi.vendorium.adapters.OglAdapter
import com.bobi.vendorium.models.Listing
import com.google.firebase.database.*

class FetchingOglasiActivity : AppCompatActivity() {

    private lateinit var listingsRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var listings: ArrayList<Listing>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetching_oglasi)

        listingsRecyclerView = findViewById(R.id.rvListings)
        listingsRecyclerView.layoutManager = LinearLayoutManager(this)
        listingsRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        listings = arrayListOf<Listing>()

        fetchListingsData()
    }

    private fun fetchListingsData() {
        listingsRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Listings")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listings.clear()
                if (snapshot.exists()) {
                    for (listingSnapshot in snapshot.children) {
                        val listing = listingSnapshot.getValue(Listing::class.java)
                        listing?.let {
                            listings.add(it)
                        }
                    }
                    val mAdapter = OglAdapter(listings)
                    listingsRecyclerView.adapter = mAdapter

                    listingsRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
