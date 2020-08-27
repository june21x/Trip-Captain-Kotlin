package com.example.tripcaptainkotlin.view.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.FragmentArticleListBinding
import com.example.tripcaptainkotlin.model.Article
import com.example.tripcaptainkotlin.view.adapter.ArticleAdapter
import com.example.tripcaptainkotlin.view.callback.ArticleClickCallback
import com.example.tripcaptainkotlin.view.ui.activity.MainActivity
import com.example.tripcaptainkotlin.viewModel.ArticleListViewModel

class ArticleListFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(ArticleListViewModel::class.java)
    }

    private lateinit var binding: FragmentArticleListBinding

    private val articleAdapter: ArticleAdapter = ArticleAdapter(object : ArticleClickCallback {
        override fun onClickArticle(article: Article) {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) && activity is MainActivity) {
                viewArticle(article)
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_article_list, container, false)
        binding.apply {
            rvArticles.adapter = articleAdapter
            isLoading = true
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.articleListLiveData.observe(viewLifecycleOwner, Observer { articles ->
            if (articles != null) {
                binding.isLoading = false
                articleAdapter.setArticleList(articles)
            }
        })
    }

    fun viewArticle(article: Article) {
        val articleFragment = ArticleFragment.forArticle(article.url) //詳細のFragment

        (activity as MainActivity).supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, articleFragment, null)
            .commit()

    }
}

