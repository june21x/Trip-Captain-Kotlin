package com.example.tripcaptainkotlin.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.ItemUserGuideBinding
import com.example.tripcaptainkotlin.view.ui.activity.MainActivity
import kotlinx.android.synthetic.main.item_user_guide.view.*

class ViewPager2Adapter(val activity: MainActivity) : RecyclerView.Adapter<PagerVH>() {

    //array of colors to change the background color of screen
    private val colors = intArrayOf(
        android.R.color.background_light,
        android.R.color.holo_red_light,
        android.R.color.holo_purple,
        android.R.color.holo_blue_light,
        android.R.color.holo_blue_light,
        android.R.color.background_light
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_user_guide, parent,
            false
        ) as ItemUserGuideBinding
        binding.apply {
            mActivity = activity

        }
        return PagerVH(binding)
    }

    //get the size of color array
    override fun getItemCount(): Int = 6

    //binding the screen with view
    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
        if (position == 0) {
            tvTutorialTitle.text = "Welcome to\nTRIP CAPTAIN"
            tvTutorialTitle.setTextColor(resources.getColor(R.color.colorPrimary, null))
            ivTutorialImage.setImageResource(R.drawable.ic_trip_captain)
            btnClose.visibility = View.GONE
            container.setBackgroundResource(colors[position])
        }
        if (position == 1) {
            tvTutorialTitle.text = "Browse The\nLatest News"
//            ivTutorialImage.visibility = View.VISIBLE
            ivTutorialImage.setImageResource(R.drawable.tutorial_1)
            btnClose.visibility = View.GONE
            container.setBackgroundResource(colors[position])
        }
        if (position == 2) {
            tvTutorialTitle.text = "Search\nNearby Places"
//            ivTutorialImage.visibility = View.VISIBLE
            ivTutorialImage.setImageResource(R.drawable.tutorial_2)
            btnClose.visibility = View.GONE
            container.setBackgroundResource(colors[position])
        }
        if (position == 3) {
            tvTutorialTitle.text = "View Place in AR"
//            ivTutorialImage.visibility = View.VISIBLE
            ivTutorialImage.setImageResource(R.drawable.tutorial_3)
            btnClose.visibility = View.GONE
            container.setBackgroundResource(colors[position])
        }
        if (position == 4) {
            tvTutorialTitle.text = "View Place in AR (continued)"
//            ivTutorialImage.visibility = View.VISIBLE
            ivTutorialImage.setImageResource(R.drawable.tutorial_4)
            btnClose.visibility = View.GONE
            container.setBackgroundResource(colors[position])
        }
        if (position == 5) {
            tvTutorialTitle.text = "Have Fun!"
            tvTutorialTitle.setTextColor(resources.getColor(R.color.colorPrimary, null))
            ivTutorialImage.visibility = View.GONE
            btnClose.visibility = View.VISIBLE
            container.setBackgroundResource(colors[position])
        }
    }
}

class PagerVH(val binding: ItemUserGuideBinding) : RecyclerView.ViewHolder(binding.root)