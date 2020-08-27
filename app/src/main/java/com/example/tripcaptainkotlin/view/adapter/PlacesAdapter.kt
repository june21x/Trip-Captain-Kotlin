package com.example.tripcaptainkotlin.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.ItemPlaceBinding
import com.example.tripcaptainkotlin.model.Place
import com.example.tripcaptainkotlin.view.callback.PlaceClickCallback

class PlacesAdapter(private val placeClickCallback: PlaceClickCallback?) :
    RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    private var placeList: List<Place>? = null

    fun setPlaceList(placeList: List<Place>) {
        if (this.placeList == null) {
            this.placeList = placeList
            notifyItemRangeInserted(0, placeList.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return requireNotNull(this@PlacesAdapter.placeList).size
                }

                override fun getNewListSize(): Int {
                    return placeList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldList = this@PlacesAdapter.placeList
                    return oldList?.get(oldItemPosition)?.id == placeList[newItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val place = placeList[newItemPosition]
                    val old = placeList[oldItemPosition]
                    return place.id == old.id
                }
            })
            this.placeList = placeList
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_place, parent,
            false
        ) as ItemPlaceBinding
        binding.callback = placeClickCallback
        return PlaceViewHolder(binding)
    }

    override fun getItemCount(): Int = this.placeList?.size ?: 0

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.binding.place = placeList?.get(position)
        holder.binding.executePendingBindings()
    }

    open class PlaceViewHolder(val binding: ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root)
}