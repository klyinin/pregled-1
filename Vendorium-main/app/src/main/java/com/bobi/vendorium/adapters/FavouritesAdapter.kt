package com.bobi.vendorium.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bobi.vendorium.R
import com.bobi.vendorium.models.Listing
import com.bumptech.glide.Glide

class FavouritesAdapter(private var listings: ArrayList<Listing>) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite_listing, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentListing = listings[position]
        Log.d("FavouritesAdapter", "Binding Listing: ${currentListing.name}")
        holder.tvFavouriteListingName.text = currentListing.name
        holder.tvFavouriteListingPrice.text = "€${currentListing.price}"

        if (currentListing.images.isNotEmpty()) {
            Glide.with(holder.ivFavouriteListingImage)
                .load(currentListing.images[0])
                .placeholder(R.drawable.placeholder_image)
                .into(holder.ivFavouriteListingImage)
        } else {
            holder.ivFavouriteListingImage.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount(): Int {
        return listings.size
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {

        val ivFavouriteListingImage: ImageView = itemView.findViewById(R.id.ivFavouriteListingImage)
        val tvFavouriteListingName: TextView = itemView.findViewById(R.id.tvFavouriteListingName)
        val tvFavouriteListingPrice: TextView = itemView.findViewById(R.id.tvFavouriteListingPrice)

        init {
            itemView.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }
    }

    fun updateList(newListings: ArrayList<Listing>) {
        listings.clear()
        listings.addAll(newListings)
        notifyDataSetChanged() // Notify the adapter that the data has changed
        Log.d("FavouritesAdapter", "Updated ${listings.size} favourite listings in the adapter.")
    }
}
