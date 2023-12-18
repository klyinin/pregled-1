package com.bobi.vendorium.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bobi.vendorium.R
import com.bobi.vendorium.models.Listing
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditListingDetailsActivity : AppCompatActivity() {

    private lateinit var etListingName: EditText
    private lateinit var etListingPrice: EditText
    private lateinit var etListingDescription: EditText
    private lateinit var btnUpdateListing: Button

    private lateinit var listingId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_listing_details)

        etListingName = findViewById(R.id.etListingName)
        etListingPrice = findViewById(R.id.etListingPrice)
        etListingDescription = findViewById(R.id.etListingDescription)
        btnUpdateListing = findViewById(R.id.btnUpdateListing)

        // Retrieve listing ID from intent extras
        listingId = intent.getStringExtra("listingId").toString()

        // Fetch listing data from Firebase and populate the EditText fields
        fetchListingData(listingId)

        // Set click listener for update button
        btnUpdateListing.setOnClickListener {
            updateListing()
        }
    }

    private fun fetchListingData(listingId: String) {
        val database = FirebaseDatabase.getInstance().reference
        val listingRef = database.child("Listings").child(listingId)

        // Retrieve the listing data from Firebase Realtime Database
        listingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listing = dataSnapshot.getValue(Listing::class.java)

                // Populate the EditText fields with the fetched data
                listing?.let {
                    etListingName.setText(it.name)
                    etListingPrice.setText(it.price.toString())
                    etListingDescription.setText(it.description)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error if fetching the data fails
                Toast.makeText(
                    baseContext,
                    "Failed to fetch listing data: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun updateListing() {
        // TODO: Get the updated values from the EditText fields
        val updatedName = etListingName.text.toString()
        val updatedPrice = etListingPrice.text.toString().toDoubleOrNull() ?: 0.0
        val updatedDescription = etListingDescription.text.toString()

        // TODO: Perform the update operation in Firebase Realtime Database
        val database = FirebaseDatabase.getInstance().reference
        val listingRef = database.child("Listings").child(listingId)

        listingRef.child("name").setValue(updatedName)
        listingRef.child("price").setValue(updatedPrice)
        listingRef.child("description").setValue(updatedDescription)
        // TODO: Update other listing properties if needed

        // TODO: Add any necessary logic or UI updates after the update is completed

        finish() // Finish the activity after the update is done
    }
}
