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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.navigation.NavController


@Composable
fun License(navController: NavController) {
    var mitDialog by remember { mutableStateOf(false) }
    var apacheDialog by remember { mutableStateOf(false) }
    var mariadbDialog by remember { mutableStateOf(false)}
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9))
    {
        Column(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.
                padding(20.dp).
                clickable {navController.navigateUp()}

            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp, vertical = 15.5.dp)
            ) {
                Box(

                ) {
                    Column(
                    ) {

                        Button(
                            onClick = {apacheDialog=true},
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(vertical = 2.5.dp)
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Row(
                            ) {
                                Text(
                                    "Apache License 2.0",
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null
                                )
                            }
                        }
                        if (apacheDialog) {
                            AlertDialog(
                                onDismissRequest = { apacheDialog = false },
                                title = {
                                    Text(text = "Apache License 2.0")
                                },
                                text = {
                                    Column {
                                        Text(text = "Copyright [2024] [Head Metal]\n" +
                                                "\n" +
                                                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                                                "you may not use this file except in compliance with the License.\n" +
                                                "You may obtain a copy of the License at\n" +
                                                "\n" +
                                                "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                                                "\n" +
                                                "Unless required by applicable law or agreed to in writing, software\n" +
                                                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                                                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                                                "See the License for the specific language governing permissions and\n" +
                                                "limitations under the License.")
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { apacheDialog = false }) {
                                        Text(text = "확인")
                                    }
                                }
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )

                        Button(
                            onClick = {mitDialog=true},
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(vertical = 2.5.dp)
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Row(
                            ) {
                                Text(
                                    "MIT License",
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null
                                )
                            }
                        }
                        if (mitDialog) {
                            AlertDialog(
                                onDismissRequest = { mitDialog = false },
                                title = {
                                    Text(text = "MIT License")
                                },
                                text = {
                                    Column {
                                        Text(text = "The MIT License (MIT)\n" +
                                                "Copyright (c) 2024 Head Metal\n" +
                                                "Permission is hereby granted, free of charge, to any person obtaining a copy" +
                                                "of this software and associated documentation files (the Software), to deal" +
                                                "in the Software without restriction, including without limitation the rights" +
                                                "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell" +
                                                "copies of the Software, and to permit persons to whom the Software is" +
                                                "furnished to do so, subject to the following conditions:" +
                                                "The above copyright notice and this permission notice shall be included in" +
                                                "all copies or substantial portions of the Software." +
                                                "THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR" +
                                                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY," +
                                                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE" +
                                                "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER" +
                                                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM," +
                                                "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN" +
                                                "THE SOFTWARE.")
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { mitDialog = false }) {
                                        Text(text = "확인")
                                    }
                                }
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )

                        Button(
                            onClick = {mariadbDialog=true},
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(vertical = 2.5.dp)
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Row(
                            ) {
                                Text(
                                    "The GPL License 2.0",
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null
                                )
                            }
                        }
                        if (mariadbDialog) {
                            AlertDialog(
                                onDismissRequest = { mariadbDialog = false },
                                title = {
                                    Text(text = "GNU License")
                                },
                                text = {
                                    Column {
                                        Text(text = " GNU GENERAL PUBLIC LICENSE\n" +
                                                "Version 2, June 1991" +
                                                " Copyright (C) 2024 Free Head Metal.\n" +
                                                "59 Temple Place - Suite 330, Boston, MA  02111-1307, USA" +
                                                " Everyone is permitted to copy and distribute verbatim copies" +
                                                "of this license document, but changing it is not allowed.")
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { mariadbDialog = false }) {
                                        Text(text = "확인")
                                    }
                                }
                            )
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