package com.headmetal.headwareintelligence

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

data class WorklistResponse(
    val work_list: List<String>
)

data class DeviceData(
    val name: String,
    val serviceUUID: String,
    val address: String
)

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Helmet(navController: NavController) {
    // UI 관련 유틸 변수들
    val context = LocalContext.current
    val auto: SharedPreferences =
        LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
    val autoEdit = auto.edit()

    // 작업장 선택 변수들
    var expanded by remember { mutableStateOf(false) }
    var itemOptions by remember { mutableStateOf(listOf<String>()) }
    var selectedOption by remember { mutableStateOf("") }
    val apiServiceWorklist =
        RetrofitInstance.apiService.apiWorklist(id = auto.getString("userid", null).toString())
    apiServiceWorklist.enqueue(object : Callback<WorklistResponse> {
        override fun onResponse(
            call: Call<WorklistResponse>,
            response: Response<WorklistResponse>
        ) {
            if (response.isSuccessful) {
                response.body()?.let { workListResponse ->
                    itemOptions = workListResponse.work_list
                }
            }
        }

        override fun onFailure(call: Call<WorklistResponse>, t: Throwable) {
            Log.e("HEAD METAL", "Failed to fetch work list")
        }
    })

    // 권한 요청
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Proceed with BLE operations.
        } else {
            // Permission is denied. Handle accordingly.
        }
    }

    // BLE
    val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager // 블루투스 서비스를 가져옴
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter // 블루투스 서비스로부터 어댑터를 받아옴
    val isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) } // 블루투스 어댑터가 있다면 최초 설정 값을 true로 설정함. 아닐 경우 false로 설정
    val bluetoothLeScanner =
        bluetoothAdapter!!.bluetoothLeScanner // 블루투스 LE 스캐너를 만듦. 어댑터를 통해서 만드는 것
    // 탐색 설정 - 로우 레이턴시
    val scanSettings =
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
    val scanList = remember { mutableStateListOf<DeviceData>() }
    // 블루투스 다이얼로그 변수
    var showScanDialog by remember { mutableStateOf(false) }
    var showWIFIDialog by remember { mutableStateOf(false) }
    // wifi ID/PW 변수
    val wifiID = remember { mutableStateOf("") }
    val wifiPW = remember { mutableStateOf("") }
    // 블루투스 UUID
    var serviceUUID: UUID? by remember { mutableStateOf(null) }
    var readUUID: UUID? by remember { mutableStateOf(null) }
    var writeUUID: UUID? by remember { mutableStateOf(null) }
    var notifyUUID: UUID? by remember { mutableStateOf(null) }
    // BluetoothGatt에 대한 객체로 remember로 받음 초기 값음 null
    var connectedGatt: BluetoothGatt? by remember { mutableStateOf(null) }
    var service: BluetoothGattService? by remember {
        mutableStateOf(
            connectedGatt?.getService(
                serviceUUID
            )
        )
    }

    // UI 변수
    var helmetid by remember { mutableStateOf("") }

    // BLE 스캔 콜백
    val scanCallback: ScanCallback = object : ScanCallback() {
        // 스캔 결과 (한 BLE 장치에 대해서만 찾음. 찾을때마다 실행됨)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (ActivityCompat.checkSelfPermission(
                    navController.context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if ((result.device.name != null) && (result.device.name).startsWith("HEADWARE")) { // 결과값의 장치 이름이 NULL이 아니라면
                    var serviceuuid = "null" // uuid를 NULL로 설정
                    if (result.scanRecord?.serviceUuids != null) { // 서비스 UUID가 null이 아니라면
                        serviceuuid =
                            result.scanRecord!!.serviceUuids.toString() // uuid에 결과 uuid 저장
                    }
                    val scanItem = DeviceData( // DeviceData 클래스에 대한 scanItem 객체 생성
                        result.device.name ?: "null", // 디바이스 이름 설정. NULL일 경우 null
                        serviceuuid, // uuid
                        result.device.address ?: "null" // 디바이스 주소값 설정. NULL일 경우 null
                    )

                    if (!scanList.contains(scanItem)) { // 만약 검색한 장치가 스캔 목록 안에 없다면
                        scanList.add(scanItem) // 스캔 목록에 아이템 추가
                    }
                }
            }
        }

        // 스캔 실패
        override fun onScanFailed(errorCode: Int) {
            Log.e("HEAD METAL", "BLUETOOTH SCAN FAILED $errorCode") // 스캔 실패 LOG 출력
        }
    }

    // BLE 연결 콜백
    val gattCallback = object : BluetoothGattCallback() {
        // 연결 상태 변경 감지
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.d("HEAD METAL", "BLE onConnectionStateChange")
            super.onConnectionStateChange(
                gatt,
                status,
                newState
            ) // 상위 클래스 onConnectionStateChange 호출
            if (newState == BluetoothProfile.STATE_CONNECTED) { // BLE 장치의 새로운 상태가 연결 상태이면
                if (ActivityCompat.checkSelfPermission( // 만약 권한이 없으면
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("HEAD METAL", "BLE NOT PERMISSION") // 권한이 없다고 출력하고
                    return // 함수 종료
                }
                else{
                    Log.d("HEAD METAL", "BLE STATE_CONNECTED") // 연결됐다고 로그 출력
                }
                gatt?.discoverServices()
                Log.d("HEAD METAL", "BLE DISCOVER SERVICES")
                connectedGatt = gatt // 변경된 장치의 BLE GATT을 받아옴 -> UI 자체 변수에 저장
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // BLE 장치의 새로운 상태가 연결 해제됐다면
                Log.d("HEAD METAL", "BLE DISCONNECT")
            }
        }

        // 기기를 선택했을 때, 서비스 탐색
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.let {
                    Log.d("HEAD METAL", "Service UUID: ${it.services[2].uuid}")
                    for (characteristic in it.services[2].characteristics) {
                        val properties = characteristic.properties
                        val propertyList = mutableListOf<String>()

                        if (properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
                            propertyList.add("READ")
                            readUUID = characteristic.uuid
                        }
                        else if(properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) {
                            propertyList.add("WRITE")
                            writeUUID = characteristic.uuid
                        }
                        else if(properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0){
                            propertyList.add("NOTIFY")
                            notifyUUID = characteristic.uuid
                        }
                        Log.d(
                            "HEAD METAL",
                            "Characteristic UUID: ${characteristic.uuid}, Properties: $propertyList"
                        )
                    }
                    serviceUUID = it.services[2].uuid // 기본 서비스 2개, 사용자 정의 서비스 1개이기에 3번째 인덱스 접근
                    service = gatt.getService(serviceUUID)
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("HEAD METAL", "BLE NOTIFICATION START")
                        val characteristicNotify = service!!.getCharacteristic(notifyUUID)
                        gatt.setCharacteristicNotification(characteristicNotify, true)
                        val desc = characteristicNotify.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                        if(desc == null){
                            Log.e("HEAD METAL","BLE DESCRIPTOR NULL")
                        }
                        else {
                            desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            val success = gatt.writeDescriptor(desc)
                            Log.d("HEAD METAL","BLE DESCRIPTOR $success")
                        }
                    }
                }
            }
        }

        // 데이터 보낼 때
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            when (status) { // 상태 확인
                BluetoothGatt.GATT_SUCCESS -> { // 데이터가 보내졌다면,
                    Log.d("HEAD METAL", "Data Send Success")
                }

                else -> { // 데이터가 안 보내졌다면
                    Log.d("HEAD METAL", "Data Send Fail")
                }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.d("HEAD METAL", "CHANGE " + characteristic.value?.toString(Charsets.UTF_8))
            if (characteristic.value?.toString(Charsets.UTF_8) == "wifi") {
                showWIFIDialog = true
            }
        }
    }

    // AlertDialog
    // Device Scan Dialog
    if (showScanDialog) {
        AlertDialog(
            onDismissRequest = {
                showScanDialog = false
            },
            title = { Text(text = "헬멧 스캔 중") },
            text = {
                Column {
                    if (scanList.isEmpty()) {
                        Text("스캔된 디바이스가 없습니다.")
                    } else {
                        scanList.forEach { device ->
                            TextButton(onClick = {
                                bluetoothAdapter.getRemoteDevice(device.address)
                                    .connectGatt(context, false, gattCallback)
                                showScanDialog = false
                            }) {
                                Text(text = device.name)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        bluetoothLeScanner.stopScan(scanCallback)
                        showScanDialog = false
                    }
                ) {
                    Text(text = "닫기")
                }
            }
        )
        // WIFI Input Dialog
    } else if (showWIFIDialog) {
        AlertDialog(
            onDismissRequest = {
                showWIFIDialog = false
            },
            title = { Text(text = "WIFI 설정") },
            text = {
                Column() {
                    Text(text = "WIFI ID")
                    TextField(
                        value = wifiID.value,
                        onValueChange = {
                            wifiID.value = it
                        }
                    )
                    Text(text = "WIFI 비밀번호")
                    TextField(
                        value = wifiPW.value,
                        onValueChange = {
                            wifiPW.value = it
                        }
                    )
                }
            },
            buttons = {
                Row() {
                    TextButton(
                        onClick = {
                            val service = connectedGatt?.getService(serviceUUID)
                            val characteristic = service?.getCharacteristic(writeUUID)
                            val id = "wi " + wifiID.value
                            val pw = "wp " + wifiPW.value
                            if (service != null && characteristic != null) {
                                characteristic.value = id.toByteArray()
                                connectedGatt?.writeCharacteristic(characteristic)

                                characteristic.value = pw.toByteArray()
                                connectedGatt?.writeCharacteristic(characteristic)
                            } else {
                                Log.e("HEAD METAL", "BLEGatt 또는 특성을 찾을 수 없음")
                            }
                            showWIFIDialog = false
                        }
                    ) {
                        Text(text = "설정")
                    }
                    TextButton(onClick = {
                        showWIFIDialog = false
                    }) {
                        Text(text = "취소")
                    }
                }
            }
        )
    }

    // 컴포저블 내에서 가장 먼저 실행
    LaunchedEffect(Unit) {
        // 블루투스 기능 유무 체크
        // 기능이 없다면
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e("HEAD METAL", "NO FEATURE_BLUETOOTH_LE")
            AlertDialog.Builder(navController.context)
                .setTitle("블루투스 연결 실패")
                .setMessage("본 기기는 블루투스를 지원하지 않습니다.")
                .setPositiveButton("확인") { dialog, which ->
                    navController.navigate("mainScreen")
                }
                .show()
            // 기능이 있다면
        } else {
            // 블루투스 권한 체크
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("HEAD METAL", "BLE SCAN START")
                    // 스캔 시작
                    bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
                }
                // 권한이 없다면 요청
                else -> {
                    Log.e("HEAD METAL", "BLE AUTHORITY REQUEST")
                    // 안드로이드 S 버전 밑일 경우
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH)
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)
                    } else { // 안드로이드 S 버전보다 높을 경우
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADVERTISE)
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                    }
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    // 메인 화면
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 뒤로가기
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable {
                        navController.navigate("mainScreen")
                    }
            )
            // 제목
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "안전모 등록",
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
                Spacer(modifier = Modifier.width(125.dp))
            }

            // 전체 컬럼
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp, vertical = 15.5.dp)
            ) {


                Row {
                    Text(
                        text = "작업자 정보",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row {

                    Text(
                        text = "작업자 ID : ",
                        color = Color.Black,
                        fontSize = 16.sp
                    )

                    Text(// 로그인 정보 연동 작업자 ID 출력
                        text = auto.getString("userid", null).toString(),
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Row {

                    Text(
                        text = "작업자 이름 : ",
                        color = Color.Black,
                        fontSize = 16.sp
                    )

                    Text(// 로그인 정보 연동 작업자 이름 출력
                        text = auto.getString("name", null).toString(),
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(
                    modifier = Modifier.height(30.dp)
                )

                Text(
                    text = "작업장 선택",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = auto.getString("workid", null).toString(),
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        readOnly = true,
                        textStyle = TextStyle.Default.copy(fontSize = 15.sp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        itemOptions.forEach { eachoption ->
                            DropdownMenuItem(onClick = {
                                selectedOption = eachoption
                                expanded = false
                                autoEdit.putString("workid", eachoption)
                                autoEdit.apply()
                            }) {
                                Text(text = eachoption, fontSize = 15.sp)
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(30.dp)
                )

                Row(
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "블루투스 상태 : ",
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                    Text(
                        text = if (isBluetoothEnabled) "켜짐" else "꺼짐",
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                }

                Row {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                if (!bluetoothAdapter.isEnabled) {
                                    bluetoothAdapter.enable()
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "이미 블루투스가 켜져있습니다",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFAA82B4)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "켜기",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                Row {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                if (bluetoothAdapter.isEnabled) {
                                    bluetoothAdapter.disable()
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "이미 블루투스가 꺼져있습니다",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            }
                        },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFAA82B4)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "끄기",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }


                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Column(
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "안전모 번호",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextField(
                        value = helmetid,
                        onValueChange = { helmetid = it },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .alpha(0.6f)
                            .width(350.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                Row {
                    Button(
                        onClick = {
                            // 블루투스가 안 켜져 있을 경우
                            if (!bluetoothAdapter.isEnabled) {
                                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            } else {
                                showScanDialog = true
                            }
                        },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFAA82B4)),
                        shape = RoundedCornerShape(8.dp),
                        enabled = connectedGatt == null
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "등록하기",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                Row {
                    Button(
                        onClick = {
                            connectedGatt?.disconnect()
                            connectedGatt?.close()
                            connectedGatt = null
                        },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFAA82B4)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "반납하기",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // UI 종료될 때
    DisposableEffect(Unit) {
        onDispose {
            Log.d("HEAD METAL", "BLE SCAN STOP")
            bluetoothLeScanner.stopScan(scanCallback)
        }
    }
}
