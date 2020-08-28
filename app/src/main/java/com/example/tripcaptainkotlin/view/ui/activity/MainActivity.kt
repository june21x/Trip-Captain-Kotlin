package com.example.tripcaptainkotlin.view.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.LayoutEditPhoneNumberBinding
import com.example.tripcaptainkotlin.utility.CubeInDepthTransformation
import com.example.tripcaptainkotlin.view.adapter.ViewPagerAdapter
import com.example.tripcaptainkotlin.view.ui.fragment.ArticleListFragment
import com.example.tripcaptainkotlin.view.ui.fragment.RecommendationsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
    private lateinit var binding: LayoutEditPhoneNumberBinding
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            // set an exit transition
            enterTransition = Explode()
            allowEnterTransitionOverlap = true
        }

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        viewPagerAdapter.addFragment(ArticleListFragment(), "News")
        viewPagerAdapter.addFragment(RecommendationsFragment(), "Recommendations")
//        viewPagerAdapter.addFragment(MyTripFragment(), "My Trip")

        viewPager.setPageTransformer(true, CubeInDepthTransformation())
        viewPager.cancelPendingInputEvents()
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navEditPhoneNumber -> {
                    editPhoneNumber()
                }

                R.id.navSavedPlaces -> {
                    intent = Intent(this, SavedPlacesActivity::class.java)
                    startActivity(intent)
                    onPause()
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
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_edit_phone_number,
            null,
            false
        )
        binding.apply {
            mainActivity = this@MainActivity
        }
        builder.setView(binding.getRoot())

        // create and show the alert dialog
        dialog = builder.create()
        dialog.show()
    }

    fun closeDialog() {
        dialog.dismiss()
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
