package com.headmetal.headwareintelligence

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

object RetrofitInstance {
    private const val BASE_URL: String = "http://minseok821lab.kro.kr:8000"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RetryInterceptor(3)) // 재시도 Interceptor 추가
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

class RetryInterceptor(private val maxRetry: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var attempt = 0
        var lastException: IOException? = null

        while (attempt < maxRetry) {
            try {
                response = chain.proceed(request)
                if (response.isSuccessful) {
                    return response
                }
            } catch (e: IOException) {
                lastException = e
            }

            attempt++
        }

        // 마지막 시도 후에도 실패 시 명확한 예외 발생
        if (lastException != null) {
            throw IOException("Failed to execute request after $maxRetry retries", lastException)
        }

        // 모든 시도가 실패했을 경우에도 예외 발생
        return response ?: throw IOException("Unknown error during request")
    }
}