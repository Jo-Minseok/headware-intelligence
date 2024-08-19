package com.headmetal.headwareintelligence

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// 프리뷰
@Preview(showBackground = true)
@Composable
fun CountermeasurePreview() {
    Countermeasure()
}

@Preview(showBackground = true)
@Composable
fun CountermeasureCardPreview() {
    CountermeasureCard(
        title = "평상시 행동 요령",
        weatherInfo = "평상시",
        cardColor = CardDefaults.cardColors(Color(0xD0D9F7BE)),
        cardContents = listOf(
            "안전 난간, 작업 발판 설치",
            "추락 방호망 설치",
            "안전대 부착 설비",
            "기계 조립, 해체 방법 준수",
            "소화기, 불티 비산 방지 덮개 설비"
        )
    )
}

@Composable
fun Countermeasure(navController: NavController = rememberNavController()) {
    IconScreen(
        imageVector = Icons.Default.ArrowBackIosNew,
        onClick = { navController.navigateUp() },
        content = {
            Column {
                ScreenTitleText(text = "안전 행동 요령")
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    CountermeasureCard(
                        title = "평상시 행동 요령",
                        weatherInfo = "평상시",
                        cardColor = CardDefaults.cardColors(Color(0xD0D9F7BE)),
                        cardContents = listOf(
                            "안전 난간, 작업 발판 설치",
                            "추락 방호망 설치",
                            "안전대 부착 설비",
                            "기계 조립, 해체 방법 준수",
                            "소화기, 불티 비산 방지 덮개 설비"
                        )
                    )
                    CountermeasureCard(
                        title = "특보시 행동 요령",
                        weatherInfo = "폭염시",
                        cardColor = CardDefaults.cardColors(Color(0xD0FFCCC7)),
                        iconColor = Color(0xFFFF6600),
                        cardContents = listOf(
                            "작업자 근무 환경 근처 통풍이 잘 되는 곳에 그늘막 설치",
                            "시원하고 깨끗한 물 제공 / 작업 중 규칙적으로 물 섭취",
                            "10~15분 이상 규칙적인 휴식 부여",
                            "무더운 시간대(14~17시) 휴식을 부여하여 옥외 작업 최소화"
                        )
                    )
                    CountermeasureCard(
                        title = "특보시 행동 요령",
                        weatherInfo = "우천시",
                        cardColor = CardDefaults.cardColors(Color(0xCCCEEFFF)),
                        iconColor = Color(0xFFFF6600),
                        cardContents = listOf(
                            "옹벽 등 붕괴 우려 장소 출입 통제, 굴착면/사면 비닐 보양",
                            "배수시설 사전 안전점검 및 정비, 악천후 시 작업 중지 및 대피",
                            "침수된 장소 출입 통제, 누전 차단길 연결 및 접지/절영 상태 점검",
                            "충번부 및 배전반 등 빗물 유입 방지 조치, 전기기계/기구 젖은 손으로 취급 금지"
                        )
                    )
                }
            }
        }
    )
}

@Composable
fun CountermeasureCard(
    title: String = "",
    weatherInfo: String = "",
    cardColor: CardColors = CardDefaults.cardColors(),
    iconColor: Color = Color.Black,
    cardContents: List<String>
) {
    Card(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = cardColor
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row {
                Text(
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    text = title,
                    fontSize = 16.sp,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, top = 10.dp),
                    text = "기상정보 : $weatherInfo",
                    style = TextStyle(textAlign = TextAlign.End)
                )
            }
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            cardContents.forEach { cardContent ->
                Row {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 10.dp)
                            .size(16.dp),
                        imageVector = Icons.Default.TripOrigin,
                        contentDescription = null,
                        tint = iconColor
                    )
                    Text(
                        modifier = Modifier.padding(start = 5.dp, bottom = 5.dp),
                        text = cardContent,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
