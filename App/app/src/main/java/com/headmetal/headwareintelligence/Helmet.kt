package com.headmetal.headwareintelligence

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.widget.Toast
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Helmet(navController: NavController) {
    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val auto: SharedPreferences =
        LocalContext.current.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE)
    var helmetid by remember {
        mutableStateOf("")
    }

    var expanded by remember { mutableStateOf(false) }
    val ItemOptions = listOf("1324", "123", "1234")
    var selectedOption by remember { mutableStateOf(ItemOptions[0]) }
    if (mBluetoothAdapter == null) {
        if (ActivityCompat.checkSelfPermission(
                navController.context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        )
            AlertDialog.Builder(LocalContext.current)
                .setTitle("블루투스 연결 실패")
                .setMessage("본 기기는 블루투스를 지원하지 않습니다.")
                .setPositiveButton("확인") { dialog, which ->
                    navController.navigate("mainScreen")
                }
                .show()
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
                                ItemOptions.forEach { eachoption ->
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
                                text = if (true) "켜짐" else "꺼짐",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        }

                        Row {
                            Button(
                                onClick = {
                                    if (mBluetoothAdapter?.isEnabled == false) {

                                        mBluetoothAdapter.enable()
                                    } else {
                                        Toast.makeText(
                                            navController.context,
                                            "이미 블루투스가 켜져있습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
                                    if (mBluetoothAdapter?.isEnabled == true) {
                                        mBluetoothAdapter?.disable()
                                    } else {
                                        Toast.makeText(
                                            navController.context,
                                            "이미 블루투스가 꺼져있습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
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