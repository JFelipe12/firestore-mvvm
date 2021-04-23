package com.felipedeveloper.shareplacesapp.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.felipedeveloper.shareplacesapp.data.models.remote.PlacesData
import com.felipedeveloper.shareplacesapp.databinding.LsvItemPlacesBinding
import com.felipedeveloper.shareplacesapp.utilities.BaseRecyclerClickListener
import com.squareup.picasso.Picasso


class PlacesAdapter(private var clickListener: BaseRecyclerClickListener<PlacesData>) :
    ListAdapter<PlacesData, PlacesAdapter.ViewHolder>(DiffUtilPlaces()) {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LsvItemPlacesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    inner class ViewHolder(val binding: LsvItemPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PlacesData, clickListener: BaseRecyclerClickListener<PlacesData>?) =
            with(binding) {

                data.photos.let { imageList ->

                    Picasso.get().load(imageList!![0].imageUrl)
                        .into(ivPlaceOne)

                    if (imageList.size > 1) Picasso.get().load(imageList[1].imageUrl)
                        .into(ivPlaceTwo)
                }

                tvDescription.text = data.description


                root.setOnClickListener {
                    clickListener?.onClick(data, adapterPosition)
                }

            }
    }

    private class DiffUtilPlaces : DiffUtil.ItemCallback<PlacesData>() {

        override fun areItemsTheSame(oldItem: PlacesData, newItem: PlacesData): Boolean {
            return newItem.placesId == oldItem.placesId
        }

        override fun areContentsTheSame(oldItem: PlacesData, newItem: PlacesData): Boolean {
            return newItem == oldItem
        }
    }
}

