package com.headmetal.headwareintelligence

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("/login")
    fun API_login(
        @Query("alert_token") alert_token:String,
        @Query("type") type:String,
        @Field("username") id:String?,
        @Field("password") pw:String?
    ): Call<LoginResponse>

    @POST("/register")
    fun API_register(
        @Body requestBody: RegisterInputModel
    ): Call<RegisterInputModel>


    @POST("/forgot/id")
    fun API_findid(
        @Body request: Forgot_Id_Request
    ): Call<Forgot_Id_Result>

    @PUT("/forgot/pw")
    fun API_changepw(
        @Body userEmployee: ForgotPw
    ): Call<ForgotPw>

    @GET("/trend/{start}/{end}")
    suspend fun getTrendData(
        @Path("start") start: String,
        @Path("end") end: String
    ): TrendResponse

    @GET("/map/{manager}/marker")
    suspend fun getAccidentData(
        @Path("manager") manager: String
    ): AccidentResponse

    @GET("/map/{manager}/marker/null")
    suspend fun getNullAccidentData(
        @Path("manager") manager: String
    ): NullAccidentResponse

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

    @GET("/processing/{manager}/{situationCode}")
    suspend fun getAllAccidentProcessingData(
        @Path("manager") manager: String,
        @Path("situationCode") situationCode: String
    ): AllAccidentProcessingResponse

    @GET("/")
    fun API_getStatus():Call<Void>
}
