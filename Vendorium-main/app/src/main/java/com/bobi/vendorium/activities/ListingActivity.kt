package com.bobi.vendorium.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bobi.vendorium.R
import com.bumptech.glide.Glide

class ListingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        val titleTextView: TextView = findViewById(R.id.listing_name)
        val priceTextView: TextView = findViewById(R.id.listing_price)
        val imageView: ImageView = findViewById(R.id.listing_image)

        val title = intent.getStringExtra("title")
        val price = intent.getDoubleExtra("price", 0.0)
        val imageUrl = intent.getStringExtra("imageUrl")

        titleTextView.text = title
        priceTextView.text = price.toString()
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)
    }
}
