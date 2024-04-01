package com.headmetal.headwareintelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun Processing(modifier: Modifier = Modifier) {
    var searchText by remember {
        mutableStateOf("")
    }
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    Column {
        Text(
            text = "처리 내역",
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = modifier.height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxSize()
            )
        }
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = modifier.background(Color(0xFFFFFFFF)),
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("처리 내역") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = {selectedTabIndex = 1},
                text = { Text("오작동 처리") }
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> ProcessingHistoryScreen()
                1 -> MalfunctionHistoryScreen()
            }
        }
    }
}

@Composable
fun ProcessingHistoryScreen() {
    Text(
        "처리 내역 임시"
    )
}

@Composable
fun MalfunctionHistoryScreen() {
    Text(
        "오작동 내역 임시"
    )
}