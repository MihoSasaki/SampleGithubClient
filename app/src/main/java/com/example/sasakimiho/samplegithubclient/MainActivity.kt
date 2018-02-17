package com.example.sasakimiho.samplegithubclient

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import butterknife.bindView
import co.metalab.asyncawait.async
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private lateinit var searchView: SearchView

    private var searchWord: String = ""

    val stringSortTitles: Array<String> = SortQuery.getAllQueries()
    val stringOrderTitles: Array<String> = OrderQuery.getAllQueries()
    private val SORT_NONE_INDEX: Int = SortQuery.NONE.ordinal //stringSortTitles.indexOf(SortQuery.NONE.query)
    private val ORDER_NONE_INDEX: Int = stringOrderTitles.indexOf(OrderQuery.NONE.query)


    private var sortSelectedId: Int = SORT_NONE_INDEX
    private var orderSelectedId: Int = ORDER_NONE_INDEX

    private var sortQuerySelected: SortQuery = SortQuery.NONE
    private var orderQuerySelected: OrderQuery = OrderQuery.NONE

    private var currentPage: Int = 1

    val itemList: MutableList <Repository> = mutableListOf<Repository>()
    val adapter = ItemListAdapter(items = itemList) { repo ->
        Toast.makeText(this, repo.description, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.addOnScrollListener(object : EndlessScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(current_page: Int) {
                currentPage = current_page
                asyncSearchExecute(searchWord, current_page, true)
            }
        })
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val menuItem: MenuItem = menu.findItem(R.id.search_view)
        this.searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        this.searchView.setIconifiedByDefault(true)
        this.searchView.isSubmitButtonEnabled = false

        if (this.searchWord != "") {
            this.searchView.setQuery(this.searchWord, false)
        } else {
            val queryHint: String = resources.getString(R.string.abc_search_hint)
            this.searchView.queryHint = queryHint
        }

        this.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchWord = newText as String
                //TODO:future work
                //textの結果をfragmentに送る
                asyncSearchExecute(newText, currentPage, false)
//                async {
//                    newText?.let { searchRepository(query = newText, ) }
//                }
                return true
            }
        })

        this.searchView.setOnQueryTextFocusChangeListener { view, b ->
            if (b) {
                //TODO:future work
                //true->fragmentを生成(もしfragmentを使用する場合)
                this.supportActionBar?.title = ""
            } else {
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
            sortItemClicked()
        }
        if (item.itemId == R.id.options_order_menu) {
            orderItemClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    fun sortItemClicked() {
        var currentSelectedId: Int = sortSelectedId
        AlertDialog.Builder(this)
                .setTitle("select sort item")
                .setSingleChoiceItems(stringSortTitles, sortSelectedId, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        currentSelectedId = p1
                    }
                })
                .setPositiveButton("Select", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        changeSortSelectedItem(stringSortTitles[currentSelectedId])
                        sortSelectedId = currentSelectedId
                        asyncSearchExecute(searchWord, currentPage, false)
                    }
                })
                .setNegativeButton("Cancel", null)
                .show()
    }

    fun orderItemClicked() {
        var currentSelectedId: Int = orderSelectedId
        AlertDialog.Builder(this)
                .setTitle("select order item")
                .setSingleChoiceItems(stringOrderTitles, orderSelectedId, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        currentSelectedId = p1
                    }
                })
                .setPositiveButton("Select", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        changeOrderSelectedItem(stringOrderTitles[currentSelectedId])
                        orderSelectedId = currentSelectedId
                        asyncSearchExecute(searchWord, currentPage, false)
                    }
                })
                .setNegativeButton("Cancel", null)
                .show()
    }

    fun changeSortSelectedItem(item: String) {
        when (item) {
            SortQuery.FORK.query -> sortQuerySelected = SortQuery.FORK
            SortQuery.STAR.query -> sortQuerySelected = SortQuery.STAR
            SortQuery.UPDATED.query -> sortQuerySelected = SortQuery.UPDATED
            SortQuery.NONE.query -> sortQuerySelected = SortQuery.NONE
        }
    }

    fun changeOrderSelectedItem(item: String) {
        when (item) {
            OrderQuery.ASC.query -> orderQuerySelected = OrderQuery.ASC
            OrderQuery.DESC.query -> orderQuerySelected = OrderQuery.DESC
            OrderQuery.NONE.query -> orderQuerySelected = OrderQuery.NONE
        }
    }

    fun asyncSearchExecute(query: String, page: Int, loadNextPage: Boolean) = async {
        searchRepository(query, page, loadNextPage)
    }

    suspend fun searchRepository(query: String, page: Int, loadNextPage: Boolean) {
        val queryMap: MutableMap<String, String> = mutableMapOf()
        val pageMap: MutableMap<String, Int> = mutableMapOf()
        queryMap.put("q", query)
        if (sortQuerySelected != SortQuery.NONE) {
            queryMap.put("sort", sortQuerySelected.query)
        }
        if (orderQuerySelected != OrderQuery.NONE) {
            queryMap.put("order", orderQuerySelected.query)
        }
        if (page != 1) {
            pageMap.put("page", page)
        }

        try {
            val appModule: AppModule = AppModule()
            val githubClient: GithubClient = appModule.providerGithubClient(appModule.provideRetrofit(appModule.providerGson(), appModule.getClient()))
            val result: Response<Page<Repository>> = githubClient.search(queryMap, pageMap).awaitResponse()
            if (result.isSuccessful) {

                runOnUiThread {
                    //if successful
                    val repository: Repository? = result.body()?.items?.get(0)
                    println(result.body())
                    result.body()?.items?.let {
                        if (loadNextPage == true) {
                            adapter.refreshRepositories()
                        }
                        adapter.addRepositories(it)
                    }
                }
            }
        } catch (e: Throwable) {
            //TODO: handle error!!
            Log.e("api call", "is not successful" + e)
        }
    }
}
