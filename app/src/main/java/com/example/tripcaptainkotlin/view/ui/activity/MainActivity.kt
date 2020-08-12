package com.example.tripcaptainkotlin.view.ui.activity

import android.os.Bundle
import android.transition.Explode
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.model.Article
import com.example.tripcaptainkotlin.utility.CubeInDepthTransformation
import com.example.tripcaptainkotlin.view.adapter.ViewPagerAdapter
import com.example.tripcaptainkotlin.view.ui.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

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

    fun viewArticle(article: Article) {
        val articleFragment = ArticleFragment.forArticle(article.url) //詳細のFragment

        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, articleFragment, null)
            .commit()

    }

    fun viewArticleinAR() {
        val articleArFragment = ArticleArFragment()

        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, articleArFragment, null)
            .commit()
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