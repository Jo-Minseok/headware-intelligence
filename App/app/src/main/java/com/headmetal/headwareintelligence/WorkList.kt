package com.headmetal.headwareintelligence

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class WorkShopInputData(
    val name: String,
    val company: String,
    val startDate: String,
    val endDate: String?
)

data class WorkShopList(
    val workId: List<Int>,
    val name: List<String>,
    val company: List<String>,
    val startDate: List<String>,
    val endDate: List<String?>
)

@Preview(showBackground = true)
@Composable
fun WorkCreateDialogPreview() {
    WorkCreateDialog(onDismissRequest = {})
}

@Preview(showBackground = true)
@Composable
fun WorkListPreview(){
    WorkList()
}

@Preview(showBackground = true)
@Composable
fun InputWorkNamePreview(){
    InputWorkName()
}

@Preview(showBackground = true)
@Composable
fun ManagerCompanyPreview(){
    ManagerCompany()
}

@Preview(showBackground = true)
@Composable
fun WorkItemPreview(){
    WorkItem()
}

@Preview(showBackground = true)
@Composable
fun DatePreview(){
    Date(inputName = "시작일")
}

@Composable
fun WorkList(navController: NavController = rememberNavController()) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userId = sharedAccount.getString("userid", null)

    var showWorkDataInputDialog by remember { mutableStateOf(false) }
    var workshopId by remember { mutableStateOf(listOf<Int>()) }
    var workshopCompany by remember { mutableStateOf(listOf<String>()) }
    var workshopName by remember { mutableStateOf(listOf<String>()) }
    var workshopStartDate by remember { mutableStateOf(listOf<String>()) }
    var workshopEndDate by remember { mutableStateOf(listOf<String?>()) }

    LaunchedEffect(showWorkDataInputDialog) {
        if (userId != null) {
            RetrofitInstance.apiService.searchWork(userId).enqueue(object : Callback<WorkShopList> {
                override fun onResponse(
                    call: Call<WorkShopList>,
                    response: Response<WorkShopList>
                ) {
                    if (response.isSuccessful) {
                        val workShopList: WorkShopList? = response.body()
                        workShopList?.let {
                            workshopId = it.workId
                            workshopName = it.name
                            workshopCompany = it.company
                            workshopStartDate = it.startDate
                            workshopEndDate = it.endDate
                        }
                    }
                }

                override fun onFailure(call: Call<WorkShopList>, t: Throwable) {
                    println("서버 통신 실패: ${t.message}")
                }
            })
        }
    }

    if (showWorkDataInputDialog) {
        WorkCreateDialog(onDismissRequest = { showWorkDataInputDialog = false })
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column {
            BackIcon(modifier = Modifier.clickable { navController.navigateUp() })
            ScreenTitleText(text = "작업장 관리")
            Text(
                text = "+ 작업장 생성",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 10.dp)
                    .clickable { showWorkDataInputDialog = true }
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(vertical = 16.dp)
            ) {
                for (i in workshopId.indices) {
                    Button(
                        onClick = { navController.navigate("WorkScreen/${workshopId[i]}") },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            Color(255, 150, 0, 80)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .padding(vertical = 16.dp)
                    ) {
                        WorkItem(workshopId[i],workshopName[i],workshopCompany[i],workshopStartDate[i],workshopEndDate[i])
                    }
                }
            }
        }
    }
}

@Composable
fun WorkCreateDialog(
    onDismissRequest: () -> Unit
) {
    val sharedAccount: SharedPreferences =
        LocalContext.current.getSharedPreferences("Account", Activity.MODE_PRIVATE)
    val userId = sharedAccount.getString("userid", null)

    val selectableCompany = remember { mutableStateOf(listOf<String>()) }
    val inputWorkName = remember { mutableStateOf("") }
    val inputWorkCompany = remember { mutableStateOf("") }
    val inputWorkStartDate = remember { mutableStateOf("") }
    val inputWorkEndDate = remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val builder = AlertDialog.Builder(context)

    LaunchedEffect(Unit) {
        RetrofitInstance.apiService.getCompanyList().enqueue(object : Callback<CompanyList> {
            override fun onResponse(call: Call<CompanyList>, response: Response<CompanyList>) {
                if (response.isSuccessful) {
                    val companyList: CompanyList? = response.body()
                    companyList?.let {
                        selectableCompany.value = it.companies
                    }
                }
            }

            override fun onFailure(call: Call<CompanyList>, t: Throwable) {
                println("서버 통신 실패: ${t.message}")
            }
        })
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            AlertTitleText("작업장 생성")
        },
        text = {
            Column {
                InputWorkName(
                    textContent = "작업장 이름",
                    inputContent = inputWorkName
                )
                Spacer(modifier = Modifier.height(16.dp))
                ManagerCompany(textContent = "담당 회사",expanded = expanded, inputWorkCompany = inputWorkCompany, selectableCompany = selectableCompany)
                Spacer(modifier = Modifier.height(16.dp))
                Date(inputName = "시작일")
                Spacer(modifier = Modifier.height(16.dp))
                Date(inputName = "종료일")
            }
        },
        confirmButton = {
            FunctionButton(modifier = Modifier, "등록", content = {
                Text(
                    text = "등록",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold) },
                colors = ButtonDefaults.buttonColors(Color.Transparent), onClick = {enrollButton(
                    userId = userId,
                    inputWorkName = inputWorkName.value,
                    inputWorkCompany = inputWorkCompany.value,
                    inputWorkStartDate = inputWorkStartDate.value,
                    inputWorkEndDate = inputWorkEndDate.value,
                    builder = builder,
                    onDismissRequest = onDismissRequest
                )})
        },
        dismissButton = {
            FunctionButton(modifier = Modifier,"취소", content = {
                Text("취소", color = Color.Black, fontWeight = FontWeight.Bold)
            },colors = ButtonDefaults.buttonColors(Color.Transparent),onClick=onDismissRequest)
        }
    )
}

@Composable
fun WorkItem(workshopId:Int=1,
             workshopName:String="test",
             workshopCompany:String="",
             workshopStartDate:String="2024-01-01",
             workshopEndDate:String?="2024-01-01"){
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.Construction,
            contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "작업장 아이디 : $workshopId",
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = workshopCompany,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = workshopName,
                color = Color.Black,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "시작일 : ${workshopStartDate.substring(2)}",
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "종료일 : ${workshopEndDate?.substring(2)}",
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun InputWorkName(textContent:String = "작업장 이름", inputContent: MutableState<String> = remember {
    mutableStateOf("")
}){
    Column {
        Text(textContent, modifier = Modifier.padding(bottom=4.dp))
        TextField(
            value = inputContent.value,
            onValueChange = { inputContent.value = it },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(255, 150, 0, 80),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerCompany(
    textContent: String = "담당 회사",
    expanded: MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
    inputWorkCompany: MutableState<String> = remember{mutableStateOf("")},
    selectableCompany: MutableState<List<String>> = remember{mutableStateOf(arrayOf("","").toList())}
    ){
    Column {
        Text(text = textContent, modifier = Modifier.padding(bottom = 4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            TextField(
                value = inputWorkCompany.value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.menuAnchor(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(255, 150, 0, 80),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier.background(Color.White)
            ) {
                selectableCompany.value.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            expanded.value = false
                            inputWorkCompany.value = item
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Date(inputWorkEndDate: MutableState<String> = remember{ mutableStateOf("")},inputName:String = ""){
    Column {
        Text(text = inputName, modifier = Modifier.padding(bottom = 4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp, end = 5.dp)
            )
            TextField(
                value = inputWorkEndDate.value,
                onValueChange = { inputWorkEndDate.value = it },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(255, 150, 0, 80),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

fun enrollButton(userId: String?,
                 inputWorkName: String,
                 inputWorkCompany: String,
                 inputWorkStartDate: String,
                 inputWorkEndDate: String,
                 builder: AlertDialog.Builder,
                 onDismissRequest: () -> Unit){
    LoadingState.show()
    if (userId != null) {
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
                    builder.setTitle("작업장 생성 성공")
                    builder.setMessage("작업장 생성에 성공하였습니다.")
                    builder.setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                        onDismissRequest()
                    }
                } else {
                    builder.setTitle("작업장 생성 실패")
                    builder.setMessage("입력한 내용을 다시 한 번 확인해주세요.")
                    builder.setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                val dialog = builder.create()
                dialog.show()
                LoadingState.hide()
            }

            override fun onFailure(call: Call<WorkShopInputData>, t: Throwable) {
                Log.e("HEAD METAL", t.message.toString())
                LoadingState.hide()
            }
        })
    }
}