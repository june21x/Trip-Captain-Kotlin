package com.example.tripcaptainkotlin.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tripcaptainkotlin.R
import com.example.tripcaptainkotlin.databinding.ItemArticleBinding
import com.example.tripcaptainkotlin.model.Article
import com.example.tripcaptainkotlin.view.callback.ArticleClickCallback

class ArticleAdapter(private val articleClickCallback: ArticleClickCallback?) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var articleList: List<Article>? = null

    fun setArticleList(articleList: List<Article>) {
        if (this.articleList == null) {
            this.articleList = articleList
            notifyItemRangeInserted(0, articleList.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return requireNotNull(this@ArticleAdapter.articleList).size
                }

                override fun getNewListSize(): Int {
                    return articleList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldList = this@ArticleAdapter.articleList
                    return oldList?.get(oldItemPosition)?.url == articleList[newItemPosition].url
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val article = articleList[newItemPosition]
                    val old = articleList[oldItemPosition]
                    return article.url == old.url
                }
            })
            this.articleList = articleList
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_article, parent,
            false
        ) as ItemArticleBinding
        binding.callback = articleClickCallback
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.binding.article = articleList?.get(position)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = this.articleList?.size ?: 0

    open class ArticleViewHolder(val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

}