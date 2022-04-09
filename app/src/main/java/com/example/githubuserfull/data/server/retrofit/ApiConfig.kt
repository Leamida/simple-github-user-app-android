package com.example.githubuserfull.data.server.retrofit

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.githubuserfull.BuildConfig

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val loggingInterceptor =
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }
            val headerAuthorizationInterceptor = Interceptor { chain ->
                var request = chain.request()
                val headers: Headers =
                    request.headers.newBuilder().add("Authorization",BuildConfig.API_KEY ).build()
                request = request.newBuilder().headers(headers).build()
                chain.proceed(request)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headerAuthorizationInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}