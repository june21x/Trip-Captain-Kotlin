package com.example.tripcaptainkotlin.view.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.FragmentArticleBinding
import com.example.tripcaptainkotlin.view.ui.activity.MainActivity

class ArticleFragment : Fragment() {

    companion object {
        private const val KEY_ARTICLE_URL = "article_url"

        fun forArticle(articleUrl: String): ArticleFragment {
            val fragment = ArticleFragment()
            val args = Bundle()
            args.putString(KEY_ARTICLE_URL, articleUrl)
            fragment.arguments = args
            return fragment
        }
    }

    private val mArticleUrl by lazy {
        requireNotNull(
            arguments?.getString(KEY_ARTICLE_URL)
        ) {
            "mArticleUrl must not be null"
        }
    }


    private lateinit var binding: FragmentArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            articleUrl = mArticleUrl
            mActivity = activity as MainActivity
        }

    }
}