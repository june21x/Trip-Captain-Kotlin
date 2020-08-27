package com.example.tripcaptainkotlin.view.ui.activity

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.utility.CubeInDepthTransformation
import com.example.tripcaptainkotlin.view.adapter.ViewPagerAdapter
import com.example.tripcaptainkotlin.view.ui.fragment.ArticleListFragment
import com.example.tripcaptainkotlin.view.ui.fragment.MyTripFragment
import com.example.tripcaptainkotlin.view.ui.fragment.RecommendationsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_place_type_selection.view.*


class MainActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

    private val TAG = "Main Activity"

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
        actionBar = supportActionBar!!
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
        actionBar.setDisplayHomeAsUpEnabled(true)

        viewPagerAdapter.addFragment(ArticleListFragment(), "News")
        viewPagerAdapter.addFragment(RecommendationsFragment(), "Recommendations")
        viewPagerAdapter.addFragment(MyTripFragment(), "My Trip")

        viewPager.setPageTransformer(true, CubeInDepthTransformation())
        viewPager.cancelPendingInputEvents()
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

    }

    fun switchPlaceType() {
        // create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select a Place Type")
        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.layout_place_type_selection, null)
        builder.setView(customLayout)

        // create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()

        customLayout.rgPlaceType.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId)
            val placeType: String = radioButton.text.toString().toLowerCase().replace(' ', '_')
            Toast.makeText(
                applicationContext, " On checked change :" +
                        " ${placeType}",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }
    }

    fun viewPlacesInAR() {
        val intent = Intent(this, ArPlaceActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true // manage other entries if you have it ...
            }
        }
        return true
    }
}
