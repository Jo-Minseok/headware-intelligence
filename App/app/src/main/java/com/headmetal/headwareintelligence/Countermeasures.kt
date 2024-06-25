package com.headmetal.headwareintelligence

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun Countermeasures(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF9F9F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable { navController.navigateUp() }
            )
            Text(
                text = "안전 행동 요령",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(Color(0xD0D9F7BE))
                ) {
                    Column (modifier = Modifier.padding(10.dp) ){
                        Row {
                            Text(
                                text = "평상시 행동 요령",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )
                            Text(
                                text = "평상시",
                                style = TextStyle(textAlign = TextAlign.End),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp, top = 10.dp)
                            )
                        }
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "안전 난간, 작업 발판 설치",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "추락 방호망 설치",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "안전대 부착 설비 ",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "기계 조립, 해체 방법 준수",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "소화기, 불티 비산 방지 덮개 설비",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(Color(0xD0FFCCC7))
                ) {
                    Column (modifier = Modifier.padding(10.dp) ){
                        Row {
                            Text(
                                text = "특보시 행동 요령",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )
                            Text(
                                text = "기상정보: 폭염시",
                                style = TextStyle(textAlign = TextAlign.End),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp, top = 10.dp)
                            )
                        }
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "작업자 근무 환경 근처 통풍이 잘 되는 곳에 그늘막 설치",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "시원하고 깨끗한 물 제공/ 작업 중 규칙적으로 물 섭취",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "10~15분 이상 규칙적인 휴식 부여",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "무더운 시간대(14~17시) 휴식을 부여하여 " +
                                        "옥외 작업 최소화",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                    }
                }


                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(Color(0xCCCEEFFF))
                ) {
                    Column (modifier = Modifier.padding(10.dp) ){
                        Row {
                            Text(
                                text = "특보시 행동 요령",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                            )
                            Text(
                                text = "기상정보: 우천시",
                                style = TextStyle(textAlign = TextAlign.End),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp, top = 10.dp)
                            )
                        }
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "옹벽 등 붕괴 우려 장소 출입 통제, " +
                                        "굴착면/사면 비닐 보양",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "배수시설 사전 안전점검 및 정비, 악천후 시 " +
                                        "작업 중지 및 대피",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "침수된 장소 출입 통제, 누전 차단길 연결 및 접지/절영 상태 점검",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Color(0xFFFF6600),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = "충번부 및 배전반 등 빗물 유입 방지 조치, " +
                                        "전기기계/기구 젖은 손으로 취급 금지",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 5.dp,bottom = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
