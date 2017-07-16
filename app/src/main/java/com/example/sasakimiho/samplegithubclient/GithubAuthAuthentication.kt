package com.example.sasakimiho.samplegithubclient

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class GithubAuthAuthentication(val clientId: String, val clientSecret: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val url: HttpUrl = request.url()
                .newBuilder()
                .addQueryParameter("client_id", clientId)
                .addQueryParameter("client_secret", clientSecret)
                .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}

