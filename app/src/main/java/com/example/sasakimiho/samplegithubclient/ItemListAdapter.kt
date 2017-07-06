package com.example.sasakimiho.samplegithubclient

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.bindView

class ItemListAdapter(val items: List<String>, val itemClicked: (String) -> Unit) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ItemListAdapter.ViewHolder, position: Int) {
        holder.setUp(items[position])
    }

    override fun getItemCount(): Int =
            this.items.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view = view, itemClicked = itemClicked)
    }

    class ViewHolder(view: View, val itemClicked: (String) -> Unit) : RecyclerView.ViewHolder(view) {
        private val textView: TextView by bindView(R.id.repository_name)
        private val eachList: RelativeLayout by bindView(R.id.each_item)

        fun setUp(itemName: String) {
            this.textView.text = itemName
            this.eachList.setOnClickListener { itemClicked(itemName) }
        }
    }

}
