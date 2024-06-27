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
    @GET("/")
    fun apiGetStatus(): Call<Void>

    @FormUrlEncoded
    @POST("/login")
    fun apiLogin(
        @Query("alertToken") alertToken: String,
        @Query("type") type: String,
        @Field("username") id: String?,
        @Field("password") pw: String?
    ): Call<LoginResponse>

    @POST("/logout")
    fun apiLogout(
        @Query("id") id: String,
        @Query("alertToken") alertToken: String
    ): Call<Void>

    @GET("/company/work_list")
    fun apiWorklist(
        @Query("userId") id: String
    ): Call<WorklistResponse>

    @GET("/company/list")
    fun getCompanyList(): Call<CompanyList>

    @POST("/register")
    fun apiRegister(
        @Body requestBody: RegisterInputModel
    ): Call<RegisterInputModel>

    @POST("/forgot/id")
    fun apiFindId(
        @Body request: ForgotIdRequest
    ): Call<ForgotIdResult>

    @PUT("/forgot/pw")
    fun apiChangePw(
        @Body userEmployee: ForgotPw
    ): Call<ForgotPw>

    @GET("/weather/{latitude}/{longitude}")
    suspend fun getWeather(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): WeatherResponse

    @GET("/trend/{start}/{end}")
    suspend fun getTrendData(
        @Path("start") start: String,
        @Path("end") end: String
    ): TrendResponse

    @GET("/processing/{manager}/{situationCode}")
    suspend fun getAllAccidentProcessingData(
        @Path("manager") manager: String,
        @Path("situationCode") situationCode: String
    ): AllAccidentProcessingResponse

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

    @GET("/work/search/{managerId}")
    fun searchWork(
        @Path("managerId") managerId: String
    ): Call<WorkShopList>

    @POST("/work/create/{managerId}")
    fun createWork(
        @Path("managerId") managerId: String,
        @Body requestBody: WorkShopInputData
    ): Call<WorkShopInputData>

    @GET("/work/search/detail/{workId}")
    fun searchWorker(
        @Path("workId") workId: Int
    ): Call<Worker>

    @POST("/work/update/{workId}")
    fun updateWork(
        @Path("workId") workId: Int,
        @Body requestBody: WorkShopInputData
    ): Call<WorkShopInputData>

    @PUT("/work/delete/{workId}")
    fun deleteWork(
        @Path("workId") workId: Int
    ): Call<Void>

    @GET("/work/user/{employeeId}")
    fun searchWorkerStatus(
        @Path("employeeId") employeeId: String
    ): Call<WorkerStatus>

    @PUT("/work/assign/{workId}/{employeeId}")
    fun assignWork(
        @Path("workId") workId: Int,
        @Path("employeeId") employeeId: String
    ): Call<Void>
}
