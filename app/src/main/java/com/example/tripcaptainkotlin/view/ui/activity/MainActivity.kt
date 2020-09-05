package com.example.tripcaptainkotlin.view.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.transition.Explode
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.ActivityMainBinding
import com.example.tripcaptainkotlin.databinding.LayoutEditPhoneNumberBinding
import com.example.tripcaptainkotlin.databinding.NavigationHeaderBinding
import com.example.tripcaptainkotlin.utility.CubeInDepthTransformation
import com.example.tripcaptainkotlin.view.adapter.ViewPagerAdapter
import com.example.tripcaptainkotlin.view.ui.fragment.ArticleListFragment
import com.example.tripcaptainkotlin.view.ui.fragment.RecommendationsFragment
import com.example.tripcaptainkotlin.view.ui.fragment.SavedPlacesFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var layoutEditPhoneNumberBinding: LayoutEditPhoneNumberBinding
    private lateinit var navigationHeaderBinding: NavigationHeaderBinding
    private lateinit var dialog: AlertDialog
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            // set an exit transition
            enterTransition = Explode()
            allowEnterTransitionOverlap = true
        }

        activityMainBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setSupportActionBar(toolbar)

        viewPagerAdapter.addFragment(ArticleListFragment(), "News")
        viewPagerAdapter.addFragment(RecommendationsFragment(), "Recommendations")
//        viewPagerAdapter.addFragment(MyTripFragment(), "My Trip")

        viewPager.setPageTransformer(true, CubeInDepthTransformation())
        viewPager.cancelPendingInputEvents()
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        val sharedPref = getSharedPreferences("SharedPref", Context.MODE_PRIVATE) ?: return
        val defaultValue = "0174270608"
        phoneNumber = sharedPref.getString(getString(R.string.phone_number), defaultValue)!!

        navigationHeaderBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.navigation_header,
            activityMainBinding.navigationView,
            false
        )

        navigationHeaderBinding.apply {
            phoneNumberDisplay = phoneNumber
        }

        activityMainBinding.navigationView.addHeaderView(navigationHeaderBinding.root)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navEditPhoneNumber -> {
                    editPhoneNumber()
                }

                R.id.navSavedPlaces -> {
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frameLayout, SavedPlacesFragment(), null)
                        .commit()
                }
                else -> false
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }


    private fun editPhoneNumber() {
        // create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Phone Number")
        // set the custom layout
        layoutEditPhoneNumberBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_edit_phone_number,
            null,
            false
        )
        layoutEditPhoneNumberBinding.apply {
            mainActivity = this@MainActivity
        }
        builder.setView(layoutEditPhoneNumberBinding.getRoot())

        // create and show the alert dialog
        dialog = builder.create()
        dialog.show()
    }

    fun closeDialog() {
        dialog.dismiss()
    }

    fun savePhoneNumber(phoneNumber: String) {

        var sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE) ?: return
        with(sharedPreferences.edit()) {
            putString(getString(R.string.phone_number), phoneNumber)
            commit()
        }

        navigationHeaderBinding.apply {
            phoneNumberDisplay = phoneNumber
        }

        closeDialog()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return true
    }

}
