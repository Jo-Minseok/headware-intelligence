package com.headmetal.headwareintelligence


import LocationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("/login/employee")
    fun loginemployee(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("/login/manager")
    fun loginmanager(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("/register/employee")
    fun registerEmployee(@Body requestBody: RegisterEmployeeRequest): Call<RegisterEmployeeRequest>

    @POST("/register/manager")
    fun registerManager(@Body requestBody: RegisterManagerRequest): Call<RegisterManagerRequest>

    @GET("/trend/{start}/{end}")
    suspend fun getTrendData(
        @Path("start") start: String,
        @Path("end") end: String
    ): TrendResponse

    @GET("/map/marker")
    suspend fun getLocationData(
    ): LocationResponse
}
