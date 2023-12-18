package com.bobi.vendorium.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bobi.vendorium.R
import com.bobi.vendorium.activities.ListingActivity
import com.bobi.vendorium.models.Listing
import com.bumptech.glide.Glide

class ListingAdapter(private val listings: List<Listing>) :
    RecyclerView.Adapter<ListingAdapter.ListingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listing, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(listings[position])
    }

    override fun getItemCount(): Int {
        return listings.size
    }

    inner class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
        private val priceTextView: TextView = itemView.findViewById(R.id.price_text_view)
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        fun bind(listing: Listing) {
            titleTextView.text = listing.name
            priceTextView.text = listing.price.toString()
            Glide.with(itemView)
                .load(listing.images.get(0)) // Load the first image in the array
                .into(imageView)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ListingActivity::class.java)
                intent.putExtra("title", listing.name)
                intent.putExtra("price", listing.price)
                intent.putExtra("imageUrl", listing.images.get(0)) // Pass the first image as the imageUrl
                itemView.context.startActivity(intent)
            }
        }
    }
}
