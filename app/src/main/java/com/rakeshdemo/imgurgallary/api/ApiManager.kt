package com.rakeshdemo.imgurgallary.api

import com.rakeshdemo.imgurgallary.FeedResponse
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiManager {
    @GET("gallery/{sort}/{window}/{page}")
    fun getGalleryImages(
        @Path("sort") sort: String,
        @Path("window") window: String,
        @Path("page") page: Int,
        @Query("showViral") showViral: Boolean=true): Call<FeedResponse>
}
object RetrofitClient {
    //Base URl , Header and Client Token
    private const val BASE_URL = "https://api.imgur.com/3/"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", "Client-ID 6fbe2f253a43461").build()
                chain.proceed(request)
            }.build())
            .build()
    }
}
object ApiClient {
    val apiService: ApiManager by lazy {
        RetrofitClient.retrofit.create(ApiManager::class.java)
    }
}
