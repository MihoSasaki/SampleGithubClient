package com.example.sasakimiho.samplegithubclient

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.bindView
import com.bumptech.glide.Glide

class ItemListAdapter(val items: MutableList<Repository>, val itemClicked: (Repository) -> Unit) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ItemListAdapter.ViewHolder, position: Int) {
        holder.setUp(items[position])
    }

    override fun getItemCount(): Int =
            this.items.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view = view, itemClicked = itemClicked)
    }

    class ViewHolder(val view: View, val itemClicked: (Repository) -> Unit) : RecyclerView.ViewHolder(view) {
        private val textView: TextView by bindView(R.id.repository_name)
        private val nameView: TextView by bindView(R.id.user_name)
        private val eachList: RelativeLayout by bindView(R.id.each_item)
        private val icon: ImageView by bindView(R.id.repository_icon)
        private val ratingBar: RatingBar by bindView(R.id.rating_bar)
        private val ratingNum: TextView by bindView(R.id.num_rating)

        fun setUp(repository: Repository) {
            this.textView.text = repository.fullName
            this.nameView.text = repository.owner.login
            this.eachList.setOnClickListener { itemClicked(repository) }
            this.ratingBar.rating = setRatingBarNum(repository.score)
            this.ratingNum.text = repository.score.toString()
            Glide.with(view.context).load(repository.owner.avatarUrl).into(this.icon)
        }

        fun setRatingBarNum(num: Float): Float {
            if (num < 20.toFloat()) {
                return 1.toFloat()
            } else if (num >= 20.toFloat() && num < 40.toFloat()) {
                return 2.toFloat()
            } else if (num >= 40.toFloat() && num < 60.toFloat()) {
                return 3.toFloat()
            } else if (num >= 60.toFloat() && num < 80.toFloat()) {
                return 4.toFloat()
            } else if (num >= 80.toFloat()) {
                return 5.toFloat()
            }
            return 0.toFloat()
        }
    }

    fun addRepository(repository: Repository) {
        synchronized(items) {
            items.add(repository)
            notifyItemInserted(items.size - 1)
        }
    }

    fun addRepositories(repositories: List<Repository>) {
        synchronized(items) {
            val currentItemSize: Int = items.size
            items.addAll(repositories)
            notifyItemRangeInserted(currentItemSize, repositories.size)
        }
    }

    fun refreshRepositories() {
        synchronized(items) {
            items.clear()
            notifyDataSetChanged()
        }
    }
}
