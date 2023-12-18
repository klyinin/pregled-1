package com.bobi.vendorium.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bobi.vendorium.R
import com.bobi.vendorium.activities.ListingDetailsActivity
import com.bobi.vendorium.adapters.OglAdapter
import com.bobi.vendorium.models.Listing
import com.google.firebase.database.*
import java.util.Locale


class ListingsFragment : Fragment(R.layout.fragment_listings) {

    private lateinit var listingsRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var listings: ArrayList<Listing>
    private lateinit var dbRef: DatabaseReference
    private lateinit var listingsSearchView: SearchView
    private lateinit var listingsAdapter: OglAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listingsRecyclerView = view.findViewById(R.id.rvListings)
        listingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        listingsRecyclerView.setHasFixedSize(true)
        tvLoadingData = view.findViewById(R.id.tvLoadingData)
        listingsSearchView =  view.findViewById(R.id.listingsSearchView)

        listings = arrayListOf()

        fetchListingsData()

        listingsSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText)
                return false
            }
        })

    }
    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<Listing> = ArrayList<Listing>()

        // running a for loop to compare elements.
        for (item in listings) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.name.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
//            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            listingsAdapter.filterList(filteredlist)
        }
    }


//Log.d("Debug", "USER UID : "+uid)
private fun fetchListingsData() {
    listingsRecyclerView.visibility = View.GONE
    tvLoadingData.visibility = View.VISIBLE

    // Get the database reference for the Listings node
    val dbRef = FirebaseDatabase.getInstance().getReference("Listings")

    dbRef.orderByChild("dateOfCreation").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            listings.clear()
            if (snapshot.exists()) {
                for (listingSnapshot in snapshot.children.reversed()) {
                    val listing = listingSnapshot.getValue(Listing::class.java)
                    listing?.let {
                        listings.add(it)
                    }
                }
                listingsAdapter = OglAdapter(listings)
                listingsRecyclerView.adapter = listingsAdapter

                listingsAdapter.setOnItemClickListener(object : OglAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val selectedListing = listings[position]
                        val intent = Intent(requireContext(), ListingDetailsActivity::class.java)
                        intent.putExtra("listingName", selectedListing.name)
                        intent.putExtra("listingPrice", selectedListing.price)
                        intent.putStringArrayListExtra("listingImages", ArrayList(selectedListing.images))
                        intent.putExtra("listingDescription", selectedListing.description)
                        intent.putExtra("listingOwnerID", selectedListing.ownerID)
                        intent.putExtra("listingID", selectedListing.id)
                        intent.putExtra("category", selectedListing.category)
                        intent.putExtra("dateOfCreation", selectedListing.dateOfCreation)
                        startActivity(intent)
                    }
                })

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


