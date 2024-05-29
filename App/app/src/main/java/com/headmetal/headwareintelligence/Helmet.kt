package com.headmetal.headwareintelligence

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.patrykandpatrick.vico.core.extension.getFieldValue
import org.apache.http.conn.scheme.HostNameResolver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

data class Work_list_Response(
    val work_list: List<String>
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Helmet(navController: NavController) {
    val context = LocalContext.current
    val auto: SharedPreferences =
        LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)

    var helmetid by remember {
        mutableStateOf("")
    }

    var expanded by remember { mutableStateOf(false) }
    var itemOptions by remember { mutableStateOf(listOf<String>()) }
    var selectedOption by remember { mutableStateOf("") }
    val apiService_worklist =
        RetrofitInstance.apiService.API_work_list(id = auto.getString("userid", null).toString())
    apiService_worklist.enqueue(object : Callback<Work_list_Response> {
        override fun onResponse(
            call: Call<Work_list_Response>,
            response: Response<Work_list_Response>
        ) {
            if (response.isSuccessful) {
                response.body()?.let { workListResponse ->
                    itemOptions = workListResponse.work_list
                }
            }
        }
        override fun onFailure(call: Call<Work_list_Response>, t: Throwable) {
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

    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }

    LaunchedEffect(Unit) {
        // 블루투스 기능 유무 체크
        if (bluetoothAdapter == null || !context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            AlertDialog.Builder(navController.context)
                .setTitle("블루투스 연결 실패")
                .setMessage("본 기기는 블루투스를 지원하지 않습니다.")
                .setPositiveButton("확인") { dialog, which ->
                    navController.navigate("mainScreen")
                }
                .show()
        } else {
            // 블루투스 권한 체크
            when {
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                }

                else -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH)
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADVERTISE)
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                    }
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9))
    {
        Column(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable {
                        navController.navigate("mainScreen")
                    }
            )
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp, vertical = 15.5.dp)
            ) {
                Box(

                ) {
                    Column(
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
                                value = selectedOption,
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
                                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                        if (bluetoothAdapter?.isEnabled == false) {
                                            bluetoothAdapter.enable()
                                        } else {
                                            Toast.makeText(
                                                navController.context,
                                                "이미 블루투스가 켜져있습니다",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    else{
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
                                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                        if (bluetoothAdapter?.isEnabled == true) {
                                            bluetoothAdapter.disable()
                                        } else {
                                            Toast.makeText(
                                                navController.context,
                                                "이미 블루투스가 꺼져있습니다",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    else{
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
                                    if(bluetoothAdapter?.isEnabled == false){
                                        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                                    }
                                    else{
                                        var mScanning:Boolean = false
                                        var arrayDevices = ArrayList<BluetoothDevice>()
                                        fun scanLeDevice(enable : Boolean,navController: NavController){
                                            // 검색을 10초만 하게 했다.
                                            val SCAN_PERIOD:Long = 10000
                                            val handler = Handler()
                                            val scanCallback = object: ScanCallback(){
                                                // 스캔 실패
                                                override fun onScanFailed(errorCode: Int) {
                                                    super.onScanFailed(errorCode)
                                                    Toast.makeText(navController.context,"BLE Scan Failed : " + errorCode, Toast.LENGTH_SHORT).show()
                                                }

                                                // 스캔 결과 단일 기종
                                                override fun onScanResult(
                                                    callbackType: Int,
                                                    result: ScanResult?
                                                ) {
                                                    result?.let{
                                                        if(!arrayDevices.contains(it.device))
                                                            arrayDevices.add(it.device)
                                                    }
                                                }

                                                // 스캔 결과 리스트
                                                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                                                    results?.let{
                                                        for(result in it){
                                                            if(!arrayDevices.contains(result.device))
                                                                arrayDevices.add(result.device)
                                                        }
                                                    }
                                                }
                                            }

                                            when(enable){
                                                true -> {
                                                    // 만약, 검색을 하고 있다면, 10초동안 검색하게 한다.
                                                    handler.postDelayed({
                                                        mScanning = false
                                                        if (ActivityCompat.checkSelfPermission(
                                                                navController.context,
                                                                Manifest.permission.BLUETOOTH_SCAN
                                                            ) != PackageManager.PERMISSION_GRANTED
                                                        ) {
                                                            // TODO: Consider calling
                                                            //    ActivityCompat#requestPermissions
                                                            // here to request the missing permissions, and then overriding
                                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                            //                                          int[] grantResults)
                                                            // to handle the case where the user grants the permission. See the documentation
                                                            // for ActivityCompat#requestPermissions for more details.
                                                        }
                                                        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
                                                    },SCAN_PERIOD)
                                                    mScanning = true
                                                    arrayDevices.clear()
                                                    bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
                                                }
                                                // 검색을 안 하게 한다면. 자동으로 끈다.
                                                else->{
                                                    mScanning = false
                                                    bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
                                                }
                                            }
                                        }

                                        val gattUpdateReceiver = object: BroadcastReceiver(){
                                            override fun onReceive(context: Context?, intent: Intent?){
                                                val action = intent?.action
                                                when(action){
                                                    BluetoothLeService.ACTION_DATA_AVAILABLE->{
                                                        val resp: String = intent.getStringExtra(BluetoothLeService.EXTRA_DATA)
                                                            .toString()
                                                    }
                                                }
                                            }
                                        }

                                        val handler = Handler()
                                        // LE 디바이스 검색 시작
                                        scanLeDevice(true,navController)

                                        // 디바이스를 선택하는 코드를 적어야해요~

                                        // 0번째 디바이스를 선택
                                        val device = arrayDevices.get(0)

                                        // 디바이스의 주소값을 알아내
                                        val deviceAddress = device.getAddress()

                                        // 만약 스캔중이라면
                                        if(mScanning){
                                            // 스캔을 꺼
                                            scanLeDevice(false,navController)
                                        }

                                        // BLE 장치와 연결을 시도한다.
                                        var bluetoothService: BluetoothLeService? = null
                                        var writeCharacteristic : BluetoothGattCharacteristic? = null
                                        var notifyCharacteristic: BluetoothGattCharacteristic? = null

                                        fun SelectCharacteristicData(gattServices:List<BluetoothGattService>){
                                            for(gattService in gattServices) {
                                                var gattCharacteristics: List<BluetoothGattCharacteristic> =
                                                    gattService.characteristics

                                                for (gattCharacteristic in gattCharacteristics) {
                                                    when (gattCharacteristic.uuid) {
                                                        BluetoothLeService.UUID_DATA_WRITE -> writeCharacteristic =
                                                            gattCharacteristic

                                                        BluetoothLeService.UUID_DATA_NOTIFY -> notifyCharacteristic =
                                                            gattCharacteristic
                                                    }
                                                }
                                            }
                                        }
                                        // SendData에서 writeCharacteristic과 setCharacteristicNotification 메서드를 사용
                                        fun SendData(data:String){
                                            writeCharacteristic?.let{
                                                if(it.properties or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0){
                                                    bluetoothService?.writeCharacteristic(it,data)
                                                }
                                            }

                                            notifyCharacteristic?.let{
                                                if(it.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY >0){
                                                    bluetoothService?.setCharacteristicNotification(it,true)
                                                }
                                            }
                                        }
                                        val serviceConnection = object: ServiceConnection {
                                            // 연결이 끊긴다면
                                            override fun onServiceDisconnected(name: ComponentName?) {
                                                bluetoothService = null
                                            }
                                            // 연결이 된다면
                                            override fun onServiceConnected(
                                                name: ComponentName?,
                                                service: IBinder?
                                            ) {
                                                bluetoothService = (service as BluetoothLeService.LocalBinder).service
                                                bluetoothService?.connect(deviceAddress)
                                            }
                                        }
                                        val gattServiceIntent = Intent(navController.context,BluetoothLeService::class.java)
                                        navController.context.bindService(gattServiceIntent,serviceConnection,Context.BIND_AUTO_CREATE)
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
                                onClick = {},
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
        }
    }
}