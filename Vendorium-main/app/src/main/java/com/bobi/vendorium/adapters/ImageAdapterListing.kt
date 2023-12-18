package com.bobi.vendorium.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bobi.vendorium.R

class ImageAdapterListing(private var imageUrls: ArrayList<String>) :
    RecyclerView.Adapter<ImageAdapterListing.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listing_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.itemView)
            .load(imageUrl)
            .apply(RequestOptions().placeholder(R.drawable.placeholder_image))
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivListingImage)

        init {
            // Apply padding to the ImageView
            val padding = itemView.context.resources.getDimensionPixelSize(R.dimen.image_padding)
            imageView.setPadding(padding, padding, padding, padding)
        }
    }

    fun setImageUrls(imageUrls: ArrayList<String>) {
        this.imageUrls = imageUrls
        notifyDataSetChanged()
    }
}
