package com.example.tripcaptainkotlin.view.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.transition.Explode
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.tripcaptainkotlin.BuildConfig
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.ActivityMainBinding
import com.example.tripcaptainkotlin.databinding.LayoutEditPhoneNumberBinding
import com.example.tripcaptainkotlin.databinding.LayoutUserGuideBinding
import com.example.tripcaptainkotlin.databinding.NavigationHeaderBinding
import com.example.tripcaptainkotlin.utility.CubeInDepthTransformation
import com.example.tripcaptainkotlin.view.adapter.ViewPager2Adapter
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
    private lateinit var layoutUserGuideBinding: LayoutUserGuideBinding
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
        val defaultValue = ""
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
                R.id.navHome -> {
                    clearFragments()
                }

                R.id.navEditPhoneNumber -> {
                    editPhoneNumber()
                }

                R.id.navSavedPlaces -> {
                    if (phoneNumber != "") {
                        clearFragments()
                        supportFragmentManager
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frameLayout, SavedPlacesFragment(), null)
                            .commit()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Phone Number is needed to view saved places",
                            Toast.LENGTH_SHORT
                        ).show()
                        editPhoneNumber()
                    }
                }

                R.id.navUserGuide -> {
                    openUserGuide()
                }
                else -> false
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        checkFirstRun()

    }

    private fun checkFirstRun() {
        val PREFS_NAME = "MyPrefsFile"
        val PREF_VERSION_CODE_KEY = "version_code"
        val DOESNT_EXIST = -1

        // Get current version code
        val currentVersionCode: Int = BuildConfig.VERSION_CODE

        // Get saved version code
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST)

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            return
        } else if (savedVersionCode == DOESNT_EXIST) {
            // This is a new install (or the user cleared the shared preferences)
            openUserGuide()
        } else if (currentVersionCode > savedVersionCode) {
            // This is an upgrade
            openUserGuide()
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply()
    }

    fun clearFragments() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }

    fun editPhoneNumber() {
        // create an alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Phone Number")

        layoutEditPhoneNumberBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_edit_phone_number,
            null,
            false
        )

        layoutEditPhoneNumberBinding.apply {
            phoneNumber = this@MainActivity.phoneNumber
            mainActivity = this@MainActivity
        }
        // set the custom layout
        builder.setView(layoutEditPhoneNumberBinding.getRoot())

        // create and show the alert dialog
        dialog = builder.create()
        dialog.show()
    }

    fun closeDialog() {
        dialog.dismiss()
    }

    fun savePhoneNumber(phoneNumber: String?) {
        var sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE) ?: return
        with(sharedPreferences.edit()) {
            putString(getString(R.string.phone_number), phoneNumber ?: "")
            commit()
        }

        this@MainActivity.phoneNumber = phoneNumber ?: ""

        navigationHeaderBinding.apply {
            phoneNumberDisplay = phoneNumber
        }

        closeDialog()

    }

    private fun openUserGuide() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // set the custom layout
        layoutUserGuideBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_user_guide,
            null,
            false
        )
        layoutUserGuideBinding.apply {
            viewPager2.adapter = ViewPager2Adapter(this@MainActivity)
        }

        builder.setView(layoutUserGuideBinding.getRoot())

        // create and show the alert dialog
        dialog = builder.create()
        dialog.show()
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
