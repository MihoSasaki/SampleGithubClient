package com.example.sasakimiho.samplegithubclient

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppModule {
    val clientId: String = "5bfbd283d12323000a40"
    val clientSecret: String = "559e0230276c2a3b9d9e8b10d29117e4207d3ba5"

    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit = Retrofit.Builder().baseUrl("https://api.github.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    fun providerGson(): Gson = GsonBuilder()
            //lowerのresponseをcamelに変換
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    fun getClient(): OkHttpClient {
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder().addNetworkInterceptor(loggingInterceptor)
                .addInterceptor(GithubAuthAuthentication(clientId, clientSecret))
                .build()
    }

    fun providerGithubClient(retrofit: Retrofit): GithubClient = retrofit.create(GithubClient::class.java)
}
