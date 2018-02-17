package com.example.sasakimiho.samplegithubclient

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class EndlessScrollListener(linearLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    var firstVisibleItem: Int = 0
    var totalItemCount: Int = 0
    var visibleItemCount: Int = 0
    val mLinearLayoutManager = linearLayoutManager
    private var previousTotal: Int = 0
    private var loading: Boolean = true
    private var current_page: Int = 1

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView?.childCount!!
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

        if (loading && totalItemCount > previousTotal) {
            loading = false
            previousTotal = totalItemCount
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleItemCount)) {
            current_page++

            onLoadMore(current_page)

            loading = true
        }
    }

    public abstract fun onLoadMore(current_page: Int)
}
