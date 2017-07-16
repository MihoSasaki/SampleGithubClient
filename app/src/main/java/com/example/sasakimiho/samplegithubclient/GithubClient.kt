package com.example.sasakimiho.samplegithubclient

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface GithubClient {

    @GET("/search/repositories")
    fun search(@QueryMap options: Map<String, String>): Call<Page<Repository>>
}