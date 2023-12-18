package com.bobi.vendorium.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bobi.vendorium.R
import com.bobi.vendorium.adapters.ImageAdapterListing
import com.bobi.vendorium.models.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class ListingDetailsActivity : AppCompatActivity() {

    //Listing
    private lateinit var listingId: String
    private lateinit var tvListingName: TextView
    private lateinit var tvListingPrice: TextView
    private lateinit var tvListingDescription: TextView
    private lateinit var tvListingCategory: TextView
    private lateinit var tvListingDate: TextView
    //Images
    private lateinit var rvListingImages: RecyclerView
    private lateinit var imageAdapter: ImageAdapterListing

    //Owner
    private lateinit var tvOwnerName: TextView
    private lateinit var tvOwnerPhone: TextView
    private lateinit var tvOwnerAddress: TextView
    private lateinit var ownerID: String
    private lateinit var currentUserID: String

    private lateinit var btnUpdateListing: Button
    private lateinit var btnDeleteListing: Button
    private lateinit var toggleFavorite: ToggleButton

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_details)

        tvListingName = findViewById(R.id.tvListingName)
        tvListingPrice = findViewById(R.id.tvListingPrice)
        tvListingDescription = findViewById(R.id.tvListingDescription)

        tvListingDate = findViewById(R.id.tvListingDate)
        tvListingCategory = findViewById(R.id.tvListingCategory)

        tvOwnerName = findViewById(R.id.tvOwnerName)
        tvOwnerPhone = findViewById(R.id.tvOwnerPhone)
        tvOwnerAddress = findViewById(R.id.tvOwnerAdress)

        btnUpdateListing = findViewById(R.id.btnUpdateListing)
        btnDeleteListing = findViewById(R.id.btnDeleteListing)
        toggleFavorite = findViewById(R.id.toggleFavorite)

        // Initialize the RecyclerView and its adapter
        rvListingImages = findViewById(R.id.rvListingImages)
        imageAdapter = ImageAdapterListing(ArrayList())
        rvListingImages.adapter = imageAdapter


        dbRef = FirebaseDatabase.getInstance().reference.child("Users")
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

        // Retrieve data from intent extras
        val listingName = intent.getStringExtra("listingName")
        val listingPrice = intent.getDoubleExtra("listingPrice", 0.0)
        val listingDescription = intent.getStringExtra("listingDescription")
        val listingImages = intent.getStringArrayListExtra("listingImages")
        val listingCategory = intent.getStringExtra("category")
        val listingDate =  intent.getStringExtra("dateOfCreation")
        val formattedlistingDate = listingDate?.let { formatListingDate(it) }


        listingId = intent.getStringExtra("listingID").toString()
        ownerID = intent.getStringExtra("listingOwnerID").toString()

        // Set the data to the corresponding views
        tvListingName.text = listingName
        tvListingPrice.text = "€${listingPrice}"
        tvListingDescription.text = listingDescription
        tvListingDate.text = formattedlistingDate
        tvListingCategory.text = listingCategory

        // Load listing images using Glide library and update the adapter data
        if (!listingImages.isNullOrEmpty()) {
            imageAdapter.setImageUrls(listingImages)
        }

        // Fetch owner data
        fetchUserData(ownerID)

        // Check if the current user is the owner of the listing
        val isOwner = currentUserID == ownerID

        // Show/hide the edit and delete buttons based on ownership
        if (isOwner) {
            btnUpdateListing.visibility = View.VISIBLE
            btnDeleteListing.visibility = View.VISIBLE
        } else {
           // Toast.makeText(baseContext, "You cannot edit this listing", Toast.LENGTH_SHORT).show()
            btnUpdateListing.visibility = View.INVISIBLE
            btnDeleteListing.visibility = View.INVISIBLE
        }

        // Set click listener for update button
        btnUpdateListing.setOnClickListener {
            val intent = Intent(this, EditListingDetailsActivity::class.java)
            intent.putExtra("listingId", listingId)
            startActivity(intent)
        }

        // Set click listener for delete button
        btnDeleteListing.setOnClickListener {
            // TODO: Implement delete listing functionality
            showDeleteConfirmationDialog()
        }

        // Set click listener for favorite toggle button
        toggleFavorite.setOnClickListener {
            toggleFavorite()
        }


        updateFavoriteButtonState()
    }

    private fun fetchUserData(userUid: String) {
        Log.d("DEBUG", "Fetching user data ...")
        dbRef.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fetchedUser = dataSnapshot.getValue(User::class.java)
                fetchedUser?.let {
                    Log.d("DEBUG", "User name: ${fetchedUser.name}")
                    displayUserData(fetchedUser)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ERROR", "Fetch user error ${databaseError.message}")
            }
        })
    }

    private fun formatListingDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Delete Listing")
        alertDialogBuilder.setMessage("Are you sure you want to delete this listing?")
        alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
            deleteListing()
        }
        alertDialogBuilder.setNegativeButton("Cancel", null)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteListing() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Listings")
        val listingRef = dbRef.child(listingId)

        listingRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Listing deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // Finish the activity and return to the ListingsFragment
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Failed to delete listing: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayUserData(user: User) {
        tvOwnerName.text = user.name
        tvOwnerPhone.text = user.contactNumber
        tvOwnerAddress.text = user.address
    }

    private fun toggleFavorite() {
        dbRef.child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    val favoriteListings = user.favouriteListings.toMutableList()

                    if (favoriteListings.contains(listingId)) {
                        favoriteListings.remove(listingId)
                    } else {
                        favoriteListings.add(listingId)
                    }

                    // Check if the list of favorite listings has changed
                    if (user.favouriteListings != favoriteListings) {
                        user.favouriteListings = favoriteListings
                        dbRef.child(currentUserID).setValue(user)
                        updateFavoriteButtonState(favoriteListings)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ERROR", "Toggle favorite error ${databaseError.message}")
            }
        })
    }
    private fun updateFavoriteButtonState(favoriteListings: List<String>) {
        toggleFavorite.isChecked = favoriteListings.contains(listingId)
    }



    private fun updateFavoriteButtonState() {
        dbRef.child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    val favoriteListings = user.favouriteListings

                    if (favoriteListings.contains(listingId)) {
                        toggleFavorite.isChecked = true
                       // ivFavorite.setImageResource(R.drawable.ic_star_full_background)
                    } else {
                        toggleFavorite.isChecked = false
                       // ivFavorite.setImageResource(R.drawable.ic_star_empty_background)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ERROR", "Update favorite button state error ${databaseError.message}")
            }
        })
    }
}
