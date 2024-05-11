package com.headmetal.headwareintelligence


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.math.BigInteger

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

    @GET("/map/marker/{no}")
    suspend fun getAccidentData(
        @Path("no") no: Int
    ): AccidentResponse

    @POST("/map/marker/{no}/complete")
    fun updateAccidentComplete(
        @Path("no") no: Int,
        @Field("detail") detail: String
    ): Call<Void>

    @POST("/map/marker/{no}/{situation}")
    fun updateAccidentSituation(
        @Path("no") no: Int,
        @Path("situation") situation: String
    ): Call<Void>
}
