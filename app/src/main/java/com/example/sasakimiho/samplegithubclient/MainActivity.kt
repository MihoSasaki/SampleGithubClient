package com.example.sasakimiho.samplegithubclient

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.sasakimiho.samplegithubclient.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    lateinit var mainBinding: ActivityMainBinding
    private val searchFragment = SearchFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val menuItem: MenuItem = menu.findItem(R.id.search_view)
        this.searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        this.searchView.setIconifiedByDefault(true)
        this.searchView.isSubmitButtonEnabled = false
        this.searchView.queryHint = resources.getString(R.string.abc_search_hint)

        this.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchFragment.updateSearchWord(it)
                    searchFragment.asyncSearchExecute(it, true)
                }
                return true
            }
        })

        this.searchView.setOnQueryTextFocusChangeListener { view, b ->
            if (b) {
                this.supportActionBar?.title = ""
                if (supportFragmentManager.findFragmentByTag(SearchFragment.TAG) == null) {
                    supportFragmentManager.beginTransaction()
                            .add(R.id.search_fragment, searchFragment, SearchFragment.TAG).commit()
                } else {
                    mainBinding.searchFragment.visibility = View.VISIBLE
                }
            } else {
                mainBinding.searchFragment.visibility = View.GONE
                this.supportActionBar?.title = getString(R.string.app_name)
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.options_sort_menu) {
            searchFragment.sortItemClicked()
        }
        if (item.itemId == R.id.options_order_menu) {
            searchFragment.orderItemClicked()
        }
        return super.onOptionsItemSelected(item)
    }
}
