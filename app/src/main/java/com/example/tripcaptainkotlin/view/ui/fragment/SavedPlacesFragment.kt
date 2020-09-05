package com.example.tripcaptainkotlin.view.ui.fragment

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.FragmentSavedPlacesBinding
import com.example.tripcaptainkotlin.model.Place
import com.example.tripcaptainkotlin.view.adapter.SavedPlacesAdapter
import com.example.tripcaptainkotlin.view.ui.activity.ArPlaceActivity
import com.example.tripcaptainkotlin.view.ui.activity.MainActivity
import com.example.tripcaptainkotlin.viewModel.SavedPlacesViewModel

class SavedPlacesFragment : Fragment() {

    private val TAG = "SavedPlacesActivity"
    private lateinit var savedPlacesViewModel: SavedPlacesViewModel
    private lateinit var phoneNumber: String
    private val savedPlacesAdapter = SavedPlacesAdapter(this)
    private lateinit var binding: FragmentSavedPlacesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        savedPlacesViewModel =
            ViewModelProvider(this@SavedPlacesFragment).get(SavedPlacesViewModel::class.java)

        val sharedPref =
            (activity as MainActivity).getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
                ?: return
        val defaultValue = "0174270608"
        phoneNumber = sharedPref.getString(getString(R.string.phone_number), defaultValue)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_saved_places, container, false)
        binding.apply {
            mActivity = activity as MainActivity
            rvSavedPlaces.adapter = savedPlacesAdapter
            savedPlacesViewModel.getSavedPlaces((activity as MainActivity).application, phoneNumber)
                .observe(viewLifecycleOwner, Observer {
                    savedPlacesAdapter.setPlaceList(it)
                })
        }
        return binding.root
    }

    fun viewPlaceInAR(place: Place) {
        val intent = Intent(activity as MainActivity, ArPlaceActivity::class.java)
        intent.putExtra("Place", place)
        startActivity(
            intent,
            ActivityOptions.makeSceneTransitionAnimation(activity as MainActivity).toBundle()
        )
        onPause()
    }

    fun removePlace(place: Place) {
        savedPlacesViewModel.deletePlace(phoneNumber, place)
    }

}