package com.rakeshdemo.imgurgallary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rakeshdemo.imgurgallary.api.ApiClient
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val context = this@MainActivity

    // Type of List Grid List==1 or Linear List==2
    private var listType:Int=1
    var list=ArrayList<ImageResponse>()
    lateinit var adapter:GallaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter=GallaryAdapter(context,listType)


        // Check Internet Connection
        if(isOnline(context)){
            getImagesGallery()  //call Function API
        }else{
            context.showToast("Please Check Your Internet Connection!!")
        }

        /** Edit text filter List */
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter.filter.filter(charSequence)
            }
            override fun afterTextChanged(editable: Editable) {

            }
        })

        /* Swipe Refresh Layout -- Reload Data */
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            edtSearch.setText("")
            getImagesGallery()  //call Function API
        }
    }
    /** Recycler View set Grid Adapter **/
    private fun gridAdapter() {
        adapter=GallaryAdapter(context,listType)
        recyclerViewGallery.layoutManager = GridLayoutManager(context, 2)
        recyclerViewGallery.adapter = adapter
        adapter.setReload(list)
    }
    /** Recycler View set List Adapter **/
    private fun linearAdapter() {
        adapter=GallaryAdapter(context,listType)
        recyclerViewGallery.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        recyclerViewGallery.addItemDecoration(VerticalSpaceItemDecoration(dpToPx(10)))
        recyclerViewGallery.adapter = adapter
        adapter.setReload(list)
    }

    /**  Imgur gallery api call **/
    private fun getImagesGallery() {
        /**
         * get Gallary Image API Call
         * Sort - Top list
         * Window - Week,days,month
         * Page - Index page 0 to next until pagination
         * showViral - show viral image true or false
         * */

        val pd=context.initLoading("progress")
        val call = ApiClient.apiService.getGalleryImages("top","week",0,showViral = false)
        call.enqueue(object : Callback<FeedResponse> {
            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                if (response.isSuccessful) {
                    pd.dismiss()
                    val post = response.body()
                    list= post!!.data as ArrayList<ImageResponse>
                    gridAdapter().takeIf { listType==1 }?:linearAdapter()
                } else {
                    pd.dismiss()
                    context.showToast("Sorry!! No Record Found")
                }
            }
            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                pd.dismiss()
                context.showToast("No data found.Please pull to refresh and try again")
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (listType==1){
            listType=2    
            item.setIcon(R.drawable.ic_grid)
            linearAdapter()

        }else if (listType==2){
            listType=1
            item.setIcon(R.drawable.ic_list)
            gridAdapter()
        }
        return super.onOptionsItemSelected(item)
    }
}