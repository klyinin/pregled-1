package com.bobi.vendorium.fragments

import com.bobi.vendorium.activities.ListingDetailsActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bobi.vendorium.R
import com.bobi.vendorium.adapters.FavouritesAdapter
import com.bobi.vendorium.models.Listing
import com.bobi.vendorium.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    private lateinit var favouritesRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var favourites: ArrayList<Listing> // Use separate ArrayList for favourites
    private lateinit var dbRef: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private lateinit var favouritesAdapter: FavouritesAdapter // Use the new FavouritesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouritesRecyclerView = view.findViewById(R.id.rvFavourites)
        favouritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        favouritesRecyclerView.setHasFixedSize(true)
        tvLoadingData = view.findViewById(R.id.tvLoadingData)

        favourites = arrayListOf() // Initialize the favourites list

        currentUser = FirebaseAuth.getInstance().currentUser!!

        favouritesAdapter = FavouritesAdapter(favourites) // Use the new FavouritesAdapter
        favouritesRecyclerView.adapter = favouritesAdapter

        fetchFavouriteListings()

        favouritesAdapter.setOnItemClickListener(object : FavouritesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedListing = favourites[position] // Use the favourites list
                val intent = Intent(requireContext(), ListingDetailsActivity::class.java)
                intent.putExtra("listingName", selectedListing.name)
                intent.putExtra("listingPrice", selectedListing.price)
                intent.putExtra("listingImage", selectedListing.images[0])
                intent.putExtra("listingDescription", selectedListing.description)
                intent.putExtra("listingOwnerID", selectedListing.ownerID)
                intent.putExtra("listingID", selectedListing.id)
                startActivity(intent)
            }
        })
    }

    private fun fetchFavouriteListings() {
        Log.d("FavouritesFragment", "fetchFavouriteListings: Fetching favourite listings...")

        favouritesRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        val dbRef = FirebaseDatabase.getInstance().getReference("Users/${currentUser.uid}")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    Log.d("FavouritesFragment", "fetchFavouriteListings: Favourite listings retrieved successfully.")

                    val favouriteListings = it.favouriteListings
                    if (favouriteListings.isNullOrEmpty()) {
                        Log.d("FavouritesFragment", "fetchFavouriteListings: No favourite listings found.")

                        val textTvLoadingDataResourceId = R.string.noListings
                        tvLoadingData.setText(textTvLoadingDataResourceId)

                        favourites.clear() // Clear the existing favourites
                        favouritesAdapter.notifyDataSetChanged() // Notify the adapter about the data change

                        favouritesRecyclerView.visibility = View.VISIBLE
                        tvLoadingData.visibility = View.GONE
                        return
                    }

                    val listingRef = FirebaseDatabase.getInstance().getReference("Listings")

                    listingRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            favourites.clear() // Clear the existing favourites
                            for (listingSnapshot in snapshot.children) {
                                val listing = listingSnapshot.getValue(Listing::class.java)
                                listing?.let {
                                    if (favouriteListings.contains(listing.id)) {
                                        favourites.add(it) // Add to the favourites list
                                    }
                                }
                            }
                            Log.d("FavouritesFragment", "Fetched ${favourites.size} favourite listings.")
                            favouritesAdapter.notifyDataSetChanged() // Notify the adapter about the data change

                            favouritesRecyclerView.visibility = View.VISIBLE
                            tvLoadingData.visibility = View.GONE
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("FavouritesFragment", "fetchFavouriteListings: Database error: ${error.message}")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FavouritesFragment", "fetchFavouriteListings: Database error: ${error.message}")
            }
        })
    }

}
