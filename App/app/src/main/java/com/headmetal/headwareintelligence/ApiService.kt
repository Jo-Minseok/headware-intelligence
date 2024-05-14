package com.headmetal.headwareintelligence

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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
    fun registerEmployee(
        @Body requestBody: RegisterResponse
    ): Call<RegisterResponse>

    @POST("/register/manager")
    fun registerManager(
        @Body requestBody: RegisterResponse
    ): Call<RegisterResponse>

    @POST("/forgot/employee/id")
    fun findemployeeId(
        @Body request: Forgot_Id_Request
    ): Call<Forgot_Id_Result>

    @POST("/forgot/manager/id")
    fun findmanagerId(
        @Body request: Forgot_Id_Request
    ): Call<Forgot_Id_Result>

    @POST("/forgot/employee/pw")
    fun confirmEmployee(
        @Body userEmployee: ForgotPw
    ): Call<ForgotPw>

    @POST("/forgot/manager/pw")
    fun confirmManager(
        @Body userManager: ForgotPw
    ): Call<ForgotPw>

    @GET("/trend/{start}/{end}")
    suspend fun getTrendData(
        @Path("start") start: String,
        @Path("end") end: String
    ): TrendResponse

    @GET("/map/marker")
    suspend fun getAccidentData(
    ): AccidentResponse

    @GET("/map/marker/{no}")
    suspend fun getAccidentProcessingData(
        @Path("no") no: Int
    ): AccidentProcessingResponse

    @PUT("/map/marker/{no}/{situationCode}")
    fun updateAccidentSituation(
        @Path("no") no: Int,
        @Path("situationCode") situationCode: String,
        @Body requestBody: AccidentProcessingUpdateRequest
    ): Call<AccidentProcessingUpdateRequest>

    @GET("/company/list")
    fun getCompanyList():Call<CompanyList>

    @GET("/accident/processing")
    suspend fun getAllAccidentProcessingData(): AllAccidentProcessingResponse

    @GET("/accident/malfunction")
    suspend fun getAllAccidentProcessingMalfunctionData(): AllAccidentProcessingMalfunctionResponse

    @GET("/")
    fun getStatus():Call<Void>
}
