package com.example.sleep

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiEndpoint {
    @GET("top-headlines?apiKey=519be6a944cb44e18c5a6b5ad771992f")
    fun fetch(@Query("q") keyword: String, @Query("page") page: Int, @Query("pageSize") pageSize: Int): Call<NewsApiResponse>
}
