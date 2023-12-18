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


class OglAdapter(private var listings: ArrayList<Listing>) :
    RecyclerView.Adapter<OglAdapter.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    // Original kak je blo za employee
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ogl_list_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentListing = listings[position]

        holder.tvListingName.text = currentListing.name
        holder.tvListingPrice.text = "€${currentListing.price}"

        if (currentListing.images.isNotEmpty()) {
            Glide.with(holder.ivListingImage)
                .load(currentListing.images[0])
                .placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                .into(holder.ivListingImage)
        } else {
            holder.ivListingImage.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount(): Int {
        return listings.size
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {

        val ivListingImage: ImageView = itemView.findViewById(R.id.ivListingImage)
        val tvListingName: TextView = itemView.findViewById(R.id.tvListingName)
        val tvListingPrice: TextView = itemView.findViewById(R.id.tvListingPrice)

        init {
            itemView.setOnClickListener {
                listener?.onItemClick(adapterPosition) // stara koda za employeee onItemClick(adapterPosition)
            }
        }
    }

    fun filterList(filterlist: ArrayList<Listing>) {
        listings = filterlist
        notifyDataSetChanged()
    }
}
