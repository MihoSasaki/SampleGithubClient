package com.example.sasakimiho.samplegithubclient

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import butterknife.bindView

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private lateinit var searchView: SearchView

    private var searchWord: String = ""

    private val SORT_NONE_INDEX: Int = 3
    private val ORDER_NONE_INDEX: Int = 2
    private var sortSelectedId: Int = SORT_NONE_INDEX
    private var orderSelectedId: Int = ORDER_NONE_INDEX

    private var sortQuerySelected: SortQuery = SortQuery.NONE
    private var orderQuerySelected: OrderQuery = OrderQuery.NONE

    val itemList: MutableList <Repository> = mutableListOf<Repository>()
    val adapter = ItemListAdapter(items = itemList) { repo ->
        Toast.makeText(this, repo.description, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter
    }
}
