package com.headmetal.headwareintelligence

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// 프리뷰
@Preview(showBackground = true)
@Composable
fun LicensePreview() {
    License()
}

@Preview(showBackground = true)
@Composable
fun LicenseOnlyYesAlertDialogPreview() {
    OnlyYesAlertDialog(
        title = "Apache License 2.0",
        textComposable = {
            Text(
                text = "Copyright [2024] [Head Metal]\n" +
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
                        "limitations under the License."
            )
        }
    )
}

@Composable
fun License(navController: NavController = rememberNavController()) {
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            var showApacheDialog by remember { mutableStateOf(false) }
            var showMITDialog by remember { mutableStateOf(false) }
            var showGPLDialog by remember { mutableStateOf(false) }

            if (showApacheDialog) {
                OnlyYesAlertDialog(
                    title = "Apache License 2.0",
                    textComposable = {
                        Text(
                            text = "Copyright [2024] [Head Metal]\n" +
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
                                    "limitations under the License."
                        )
                    }
                ) { showApacheDialog = false }
            }

            if (showMITDialog) {
                OnlyYesAlertDialog(
                    title = "MIT License",
                    textComposable = {
                        Text(
                            text = "The MIT License (MIT)\n" +
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
                                    "THE SOFTWARE."
                        )
                    }
                ) { showMITDialog = false }
            }

            if (showGPLDialog) {
                OnlyYesAlertDialog(
                    title = "GNU License",
                    textComposable = {
                        Text(
                            text = " GNU GENERAL PUBLIC LICENSE\n" +
                                    "Version 2, June 1991" +
                                    " Copyright (C) 2024 Free Head Metal.\n" +
                                    "59 Temple Place - Suite 330, Boston, MA  02111-1307, USA" +
                                    " Everyone is permitted to copy and distribute verbatim copies" +
                                    "of this license document, but changing it is not allowed."
                        )
                    }
                ) { showGPLDialog = false }
            }

            Column {
                ScreenTitleText(text = "라이센스")
                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                    LabelWithNextIcon(text = "Apache License 2.0", onClick = { showApacheDialog = true })
                    LabelWithNextIcon(text = "MIT License", onClick = { showMITDialog = true })
                    LabelWithNextIcon(text = "The GPL License 2.0", onClick = { showGPLDialog = true })
                }
            }
        }
    )
}
