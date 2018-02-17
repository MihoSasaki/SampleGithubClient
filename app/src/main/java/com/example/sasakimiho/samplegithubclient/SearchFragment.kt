package com.example.sasakimiho.samplegithubclient

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import co.metalab.asyncawait.async
import com.example.sasakimiho.samplegithubclient.databinding.FragmentSearchBinding
import retrofit2.Response
import ru.gildor.coroutines.retrofit.awaitResponse

class SearchFragment : Fragment() {
    companion object {
        val TAG: String = "SearchFragment:TAG"
    }
    lateinit var searchBinding: FragmentSearchBinding

    val stringSortTitles: Array<String> = SortQuery.getAllQueries()
    val stringOrderTitles: Array<String> = OrderQuery.getAllQueries()

    private val SORT_NONE_INDEX: Int = SortQuery.NONE.ordinal //stringSortTitles.indexOf(SortQuery.NONE.query)
    private val ORDER_NONE_INDEX: Int = stringOrderTitles.indexOf(OrderQuery.NONE.query)
    private var sortSelectedId: Int = SORT_NONE_INDEX
    private var orderSelectedId: Int = ORDER_NONE_INDEX

    private var sortQuerySelected: SortQuery = SortQuery.NONE
    private var orderQuerySelected: OrderQuery = OrderQuery.NONE

    val itemList: MutableList<Repository> = mutableListOf<Repository>()
    val adapter = ItemListAdapter(items = itemList) { repo ->
        Toast.makeText(context, repo.description, Toast.LENGTH_SHORT).show()
    }

    var searchWord: String = ""

    private var currentPage: Int = 1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return searchBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
    }

    fun setUpBinding() {
        searchBinding.recyclerView.addOnScrollListener(object : EndlessScrollListener(searchBinding.recyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(current_page: Int) {
                currentPage = current_page
                asyncSearchExecute(searchWord, true)
            }
        })
        searchBinding.recyclerView.adapter = adapter
    }

    fun sortItemClicked() {
        var currentSelectedId: Int = sortSelectedId
        AlertDialog.Builder(context)
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
                        asyncSearchExecute(searchWord, false)
                    }
                })
                .setNegativeButton("Cancel", null)
                .show()
    }

    fun orderItemClicked() {
        var currentSelectedId: Int = orderSelectedId
        AlertDialog.Builder(context)
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
                        asyncSearchExecute(searchWord, false)
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

    fun asyncSearchExecute(query: String, loadNextPage: Boolean) = async {
        searchRepository(query, currentPage, loadNextPage)
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
            val appModule = AppModule()
            val githubClient: GithubClient = appModule.providerGithubClient(appModule.provideRetrofit(appModule.providerGson(), appModule.getClient()))
            val result: Response<Page<Repository>> = githubClient.search(queryMap, pageMap).awaitResponse()
            if (result.isSuccessful) {
                activity.runOnUiThread {
                    //if successful
                    val repository: Repository? = result.body()?.items?.get(0)
                    println(result.body())
                    result.body()?.items?.let {
                        if (loadNextPage == true) {
//                            if (loadNextPage == false) {
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

    fun updateSearchWord(newWords: String) {
        this.searchWord = newWords
        //should be 0 because refreshing page
        this.currentPage = 0
    }
}

