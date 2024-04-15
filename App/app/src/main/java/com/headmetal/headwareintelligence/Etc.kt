package com.headmetal.headwareintelligence

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
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Description
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material.AlertDialog
import androidx.compose.ui.window.Dialog
import androidx.compose.material.TextButton

@Preview(showBackground = true)
@Composable
fun Etc() {
    var licenseDialog by remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9))
    {
        Column(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.padding(20.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "기타",
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
                                text = "작업자",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )

                        Row {
                            Text(
                                text = "버전 정보",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(top = 5.dp)
                            )
                            Text(
                                text = "1.0.0",
                                fontSize = 16.sp,
                                style = TextStyle(textAlign = TextAlign.End),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 7.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )

                        Row {

                            Text(
                                text = "라이센스",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.clickable { licenseDialog = true }
                            )
                        }
                        if (licenseDialog) {
                            AlertDialog(
                                onDismissRequest = { licenseDialog = false },
                                title = {
                                    Text(text = "라이센스 정보")
                                },
                                text = {
                                    Column {
                                        Text(text = "라이센스 1")
                                        Text(text = "라이센스 2")
                                        Text(text = "라이센스 3")
                                        Text(text = "라이센스 4")
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { licenseDialog= false }) {
                                        Text(text = "확인")
                                    }
                                }
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )

                        Row {

                            Text(
                                text = "App Info",
                                color = Color.Black,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Row {

                            Text(
                                text = "Team : Head-Metal",
                                color = Color.Black,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Row {

                            Text(
                                text = "Site : https://github.com/Jo-Minseok/headware-intelligence\n",
                                color = Color.Black,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }


                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }
        }
    }
}