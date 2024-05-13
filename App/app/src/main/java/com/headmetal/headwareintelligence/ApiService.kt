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
        @Body requestBody: RegisterEmployeeResponse
    ): Call<RegisterEmployeeResponse>

    @POST("/register/manager")
    fun registerManager(
        @Body requestBody: RegisterManagerResponse
    ): Call<RegisterManagerResponse>

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
        @Body userEmployee: EmployeeForgotPw
    ): Call<RedirectResponse>

    @POST("/forgot/manager/pw")
    fun confirmManager(
        @Body userManager: ManagerForgotPw
    ): Call<RedirectResponse>

    @POST("/forgot/employee/pw/change")
    fun changeEmployeePassword(
        @Body request: EmployeePasswordChangeRequest
    ): Call<Unit>

    @POST("/forgot/manager/pw/change")
    fun changeManagerPassword(
        @Body request: ManagerPasswordChangeRequest
    ): Call<Unit>


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
}
