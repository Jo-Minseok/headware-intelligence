package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import android.location.Location
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 회사 목록 GET
 */
fun companyListGET(
    companyList: MutableState<List<String>>,
    navController: NavController,
    onDismissRequest: () -> Unit,
    defaultValue: List<String> = listOf()
) {
    LoadingState.show()
    RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
        override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
            if (response.isSuccessful) {
                val companyListResponse: CompanyList? = response.body()
                companyListResponse?.let {
                    companyList.value = defaultValue + it.companies
                }
            } else {
                errorBackApp(
                    navController = navController,
                    error = response.message(),
                    title = "회사 목록 로드 오류",
                    message = "회사 목록을 불러오지 못 했습니다.",
                    action = onDismissRequest
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<CompanyList>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "회사 목록 로드 오류",
                message = "네트워크 문제로 인해 회사 목록을 불러오지 못 했습니다.",
                action = onDismissRequest
            )
        }
    })
}

/**
 * 회원가입 POST
 */
fun registerPOST(
    id: String,
    pw: String,
    rePw: String,
    name: String,
    email: String,
    phone: String,
    selectCompany: String,
    isManager: Boolean,
    navController: NavController
) {
    LoadingState.show()
    RetrofitInstance.apiService.apiRegister(
        RegisterInputModel(
            id,
            pw,
            rePw,
            name,
            email,
            phone,
            if (selectCompany == "없음") null else selectCompany,
            if (isManager) "manager" else "employee"
        )
    ).enqueue(object : Callback<RegisterInputModel> {
        override fun onResponse(
            call: Call<RegisterInputModel>,
            response: Response<RegisterInputModel>
        ) {
            if (response.isSuccessful) {
                showAlertDialog(
                    context = navController.context,
                    title = "회원가입 성공",
                    message = "로그인 화면으로 이동합니다.",
                    buttonText = "확인"
                ) { navController.navigate("LoginScreen") }
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "회원가입 실패",
                    message = "이미 존재하는 회원 또는 잘못된 정보입니다.",
                    buttonText = "확인"
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(
            call: Call<RegisterInputModel>,
            t: Throwable
        ) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "네트워크 오류",
                message = "네트워크 문제로 회원 가입을 할 수 없습니다."
            )
        }
    })
}

/**
 * 작업장 생성
 */
fun workshopPOST(
    userId: String,
    inputWorkName: String,
    inputWorkCompany: String,
    inputWorkStartDate: String,
    inputWorkEndDate: String,
    navController: NavController,
    onDismissRequest: () -> Unit
) {
    LoadingState.show()
    RetrofitInstance.apiService.createWork(
        userId,
        WorkShopInputData(
            inputWorkName,
            inputWorkCompany,
            inputWorkStartDate,
            inputWorkEndDate
        )
    ).enqueue(object : Callback<WorkShopInputData> {
        override fun onResponse(
            call: Call<WorkShopInputData>,
            response: Response<WorkShopInputData>
        ) {
            if (response.isSuccessful) {
                showAlertDialog(
                    context = navController.context,
                    title = "작업장 생성 성공",
                    message = "작성장 생성에 성공하였습니다.",
                    buttonText = "확인",
                    onButtonClick = {
                        onDismissRequest()
                    }
                )
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "작업장 생성 실패",
                    message = "입력한 내용을 다시 한 번 확인해주세요.",
                    buttonText = "확인",
                    onButtonClick = {}
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<WorkShopInputData>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업장 등록 오류",
                message = "네트워크 오류로 작업장 등록을 못 했습니다.",
                action = onDismissRequest
            )
        }
    })
}

/**
 * 작업장 목록 받아오기
 */
fun workshopListGET(
    userId: String,
    navController: NavController,
    workshopId: MutableState<List<Int>>,
    workshopName: MutableState<List<String>>,
    workshopStartDate: MutableState<List<String>>,
    workshopEndDate: MutableState<List<String>>,
    workshopCompany: MutableState<List<String>>
) {
    LoadingState.show()
    RetrofitInstance.apiService.searchWork(userId).enqueue(object : Callback<WorkShopList> {
        override fun onResponse(
            call: Call<WorkShopList>,
            response: Response<WorkShopList>
        ) {
            if (response.isSuccessful) {
                val workShopList: WorkShopList? = response.body()
                workShopList?.let {
                    workshopId.value = it.workId
                    workshopName.value = it.name
                    workshopCompany.value = it.company
                    workshopStartDate.value = it.startDate
                    workshopEndDate.value = it.endDate
                }
            } else {
                errorBackApp(
                    navController = navController,
                    error = response.message(),
                    title = "작업장 목록 오류",
                    message = "작업장 목록을 불러올 수 없습니다.",
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<WorkShopList>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업장 목록 오류",
                message = "네트워크 문제로 인해 작업장 목록을 불러올 수 없습니다.",
            )
        }
    })
}

/**
 * 작업자 등록
 */
fun addWorkerPOST(
    workId: Int,
    workerId: String,
    navController: NavController,
    onDismissRequest: () -> Unit
) {
    LoadingState.show()
    RetrofitInstance.apiService.assignWork(
        workId,
        workerId
    ).enqueue(object : Callback<Void> {
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            if (response.isSuccessful) {
                showAlertDialog(
                    context = navController.context,
                    title = "작업자 등록 성공",
                    message = "작업자 등록에 성공하였습니다.",
                    buttonText = "확인",
                    onButtonClick = onDismissRequest
                )
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "작업자 등록 실패",
                    message = response.message(),
                    buttonText = "확인",
                    onButtonClick = {}
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업자 추가 오류",
                message = "네트워크 오류로 인해 작업자를 추가할 수 없습니다.",
                action = onDismissRequest
            )
        }
    })
}

/**
 * 작업장 삭제
 */
fun removeWorkshopPUT(
    workId: Int,
    navController: NavController,
    onDismissRequest: () -> Unit
) {
    LoadingState.show()
    RetrofitInstance.apiService.deleteWork(
        workId
    ).enqueue(object : Callback<Void> {
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            if (response.isSuccessful) {
                showAlertDialog(
                    context = navController.context,
                    title = "작업장 삭제 성공",
                    message = "작업장 삭제에 성공하였습니다.",
                    buttonText = "확인",
                    onButtonClick = {
                        onDismissRequest()
                        navController.navigateUp()
                    }
                )
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "작업장 삭제 실패",
                    message = "작업장 삭제를 실패했습니다",
                    buttonText = "확인",
                    onButtonClick = onDismissRequest
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업장 삭제 오류",
                message = "네트워크 문제로 인해 작업장에 대한 정보를 찾을 수 없습니다.",
                action = onDismissRequest
            )
        }
    })
}

/**
 * 작업장 수정
 */
fun updateWorkshopPUT(
    workId: Int,
    inputWorkName: String,
    inputWorkCompany: String,
    inputWorkStartDate: String,
    inputWorkEndDate: String,
    navController: NavController,
    onDismissRequest: () -> Unit
) {
    LoadingState.show()
    RetrofitInstance.apiService.updateWork(
        workId,
        WorkShopInputData(
            inputWorkName,
            inputWorkCompany,
            inputWorkStartDate,
            inputWorkEndDate
        )
    ).enqueue(object : Callback<WorkShopInputData> {
        override fun onResponse(
            call: Call<WorkShopInputData>,
            response: Response<WorkShopInputData>
        ) {
            if (response.isSuccessful) {
                showAlertDialog(
                    context = navController.context,
                    title = "작업장 수정 성공",
                    message = "작업장 수정에 성공하였습니다.",
                    buttonText = "확인",
                    onButtonClick = onDismissRequest
                )
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "작업장 수정 실패",
                    message = "입력한 내용을 다시 한 번 확인해주세요.",
                    buttonText = "확인",
                    onButtonClick = {}
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<WorkShopInputData>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업장 수정 오류",
                message = "네트워크 문제로 작업장 수정을 할 수 없습니다."
            )
        }
    })
}

/**
 * 작업자 검색
 */
fun workerGET(
    workerId: String,
    onDismissRequest: () -> Unit,
    navController: NavController,
    workerName: MutableState<String>,
    workerPhone: MutableState<String>
) {
    LoadingState.show()
    RetrofitInstance.apiService.searchWorkerStatus(workerId)
        .enqueue(object : Callback<WorkerStatus> {
            override fun onResponse(
                call: Call<WorkerStatus>,
                response: Response<WorkerStatus>
            ) {
                if (response.isSuccessful) {
                    val workerStatus: WorkerStatus? = response.body()
                    workerStatus?.let {
                        workerName.value = it.name
                        workerPhone.value = it.phoneNo
                    }
                } else {
                    errorBackApp(
                        navController = navController,
                        error = response.message(),
                        title = "작업자 검색 오류",
                        message = "작업자에 대한 정보를 찾을 수 없습니다.",
                        action = onDismissRequest
                    )
                }
                LoadingState.hide()
            }

            override fun onFailure(call: Call<WorkerStatus>, t: Throwable) {
                errorBackApp(
                    navController = navController,
                    error = t.toString(),
                    title = "작업자 검색 오류",
                    message = "네트워크 문제로 인해 작업자에 대한 정보를 찾을 수 없습니다."
                )
            }
        })
}

fun workerListGET(
    workId: Int,
    workerId: MutableState<List<String>>,
    workerName: MutableState<List<String>>,
    navController: NavController
) {
    LoadingState.show()
    RetrofitInstance.apiService.searchWorker(workId).enqueue(object : Callback<Worker> {
        override fun onResponse(
            call: Call<Worker>,
            response: Response<Worker>
        ) {
            if (response.isSuccessful) {
                val worker: Worker? = response.body()
                worker?.let {
                    workerId.value = it.workerId
                    workerName.value = it.name
                }
            } else {
                errorBackApp(
                    navController = navController,
                    error = response.message(),
                    title = "작업장 검색 오류",
                    message = "작업장에 대한 정보를 찾을 수 없습니다."
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<Worker>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "작업장 검색 오류",
                message = "네트워크 문제로 작업장에 대한 정보를 찾을 수 없습니다."
            )
        }
    })
}

fun logoutPOST(
    navController: NavController,
    sharedAccount: SharedPreferences,
    sharedConfigure: SharedPreferences,
    sharedAlert: SharedPreferences,
    showLogoutDialog: MutableState<Boolean>
) {
    LoadingState.show()
    RetrofitInstance.apiService.apiLogout(
        id = sharedAccount.getString("userid", null).toString(),
        alertToken = sharedAlert.getString("alert_token", null).toString()
    ).enqueue(object : Callback<Void> {
        override fun onResponse(p0: Call<Void>, p1: Response<Void>) {
            LoadingState.hide()
            showLogoutDialog.value = false
            Toast.makeText(
                navController.context,
                "로그아웃을 성공하였습니다.",
                Toast.LENGTH_SHORT
            ).show()
            sharedAccount.edit().clear().apply()
            sharedConfigure.edit().clear().apply()
            navController.navigate("LoginScreen")
        }

        override fun onFailure(p0: Call<Void>, t: Throwable) {
            networkErrorFinishApp(navController = navController, error = t)
        }
    })
}

fun weatherGET(
    pos: Location,
    temperature: MutableState<Float>,
    airVelocity: MutableState<Float>,
    precipitation: MutableState<Float>,
    humidity: MutableState<Float>,
    navController: NavController
) {
    RetrofitInstance.apiService.getWeather(pos.latitude, pos.longitude)
        .enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weather: WeatherResponse? = response.body()
                    weather?.let {
                        temperature.value = it.temperature
                        airVelocity.value = it.airVelocity
                        precipitation.value = it.precipitation
                        humidity.value = it.humidity
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                networkErrorFinishApp(navController = navController, error = t)
            }
        })
}

fun loginPOST(
    inputId: String,
    inputPw: MutableState<String>,
    isManager: Boolean,
    navController: NavController
) {
    val sharedAlert: SharedPreferences =
        navController.context.getSharedPreferences(
            "Alert",
            Activity.MODE_PRIVATE
        )
    val sharedAccount: SharedPreferences =
        navController.context.getSharedPreferences(
            "Account",
            Activity.MODE_PRIVATE
        )
    val sharedAccountEdit: SharedPreferences.Editor = sharedAccount.edit()

    LoadingState.show()
    RetrofitInstance.apiService.apiLogin(
        alertToken = sharedAlert.getString("alert_token", null).toString(),
        type = if (isManager) "manager" else "employee",
        id = inputId,
        pw = inputPw.value
    ).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(
            call: Call<LoginResponse>,
            response: Response<LoginResponse>
        ) {
            if (response.isSuccessful) {
                sharedAccountEdit.putString("userid", response.body()?.id)
                sharedAccountEdit.putString("password", inputPw.value)
                sharedAccountEdit.putString("name", response.body()?.name)
                sharedAccountEdit.putString("phone", response.body()?.phoneNo)
                sharedAccountEdit.putString("email", response.body()?.email)
                sharedAccountEdit.putString(
                    "token",
                    response.body()?.accessToken
                )
                sharedAccountEdit.putString(
                    "token_type",
                    response.body()?.tokenType
                )
                sharedAccountEdit.putString(
                    "type",
                    if (isManager) "manager" else "employee"
                )
                sharedAccountEdit.apply()
                navController.navigate("MainScreen")
                Toast.makeText(
                    navController.context,
                    response.body()?.name + "님 반갑습니다",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "로그인 실패",
                    message = "아이디 및 비밀번호를 확인하세요.",
                    buttonText = "확인",
                    onButtonClick = { inputPw.value = "" }
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            networkErrorFinishApp(navController = navController, error = t)
        }
    })
}

fun userCompanyListGET(
    companyList: MutableState<List<String>>,
    userId: String,
    navController: NavController
) {
    LoadingState.show()
    RetrofitInstance.apiService.apiWorkList(id = userId)
        .enqueue(object : Callback<WorkListResponse> {
            override fun onResponse(
                call: Call<WorkListResponse>,
                response: Response<WorkListResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { worklistResponse ->
                        companyList.value = worklistResponse.workList
                    }
                }
                LoadingState.hide()
            }

            override fun onFailure(call: Call<WorkListResponse>, t: Throwable) {
                networkErrorFinishApp(navController = navController, error = t)
            }
        })
}

fun changePasswordPUT(
    pw: String,
    rePw: String,
    id: String,
    phone: String,
    isManager: Boolean,
    navController: NavController
) {
    LoadingState.show()
    RetrofitInstance.apiService.apiChangePw(
        ForgotPw(
            id,
            phone,
            pw,
            rePw,
            if (isManager) "manager" else "employee"
        )
    ).enqueue(object : Callback<ForgotPw> {
        override fun onResponse(
            call: Call<ForgotPw>,
            response: Response<ForgotPw>
        ) {
            if (response.isSuccessful) {
                showAlertDialog(
                    context = navController.context,
                    title = "비밀번호 변경 성공",
                    message = "로그인 화면으로 이동합니다.",
                    buttonText = "확인",
                    onButtonClick = { navController.navigate("LoginScreen") }
                )
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "비밀번호 변경 실패",
                    message = "존재하지 않는 계정입니다.",
                    buttonText = "확인"
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<ForgotPw>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "네트워크 오류",
                message = "네트워크 문제로 비밀번호를 찾을 수 없습니다."
            )
        }
    })
}

fun idSearchPOST(name: String, email: String, isManager: Boolean, navController: NavController) {
    LoadingState.show()
    RetrofitInstance.apiService.apiFindId(
        ForgotIdRequest(
            name,
            email,
            if (isManager) "manager" else "employee"
        )
    ).enqueue(object : Callback<ForgotIdResult> {
        override fun onResponse(
            call: Call<ForgotIdResult>,
            response: Response<ForgotIdResult>
        ) {
            if (response.isSuccessful) {
                val id = response.body()?.id
                showAlertDialog(
                    context = navController.context,
                    title = "아이디 찾기 성공",
                    message = "ID: $id",
                    buttonText = "확인"
                )
            } else {
                showAlertDialog(
                    context = navController.context,
                    title = "아이디 찾기 실패",
                    message = "일치하는 계정을 찾을 수 없습니다.",
                    buttonText = "확인"
                )
            }
            LoadingState.hide()
        }

        override fun onFailure(call: Call<ForgotIdResult>, t: Throwable) {
            errorBackApp(
                navController = navController,
                error = t.toString(),
                title = "네트워크 오류",
                message = "네트워크 문제로 ID를 찾을 수 없습니다."
            )
        }
    })
}