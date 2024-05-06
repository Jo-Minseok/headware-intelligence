package com.headmetal.headwareintelligence

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("/login/employee") // /login/employee 또는 /login/manager
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("/trend/{start}/{end}")
    suspend fun getTrendData(
        @Path("start") start: String,
        @Path("end") end: String
    ): TrendResponse
}
