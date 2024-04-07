import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun Map() {
    val sheetState = rememberModalBottomSheetState()
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F9F9)) {
        Column(
            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = "작업 현장 1",fontSize=20.sp)
                Text(text = "0.0KM",
                    color = Color(0xFFFF6600),
                    modifier = Modifier.background(Color(0x26FF6600), RoundedCornerShape(4.dp)).width(100.dp),
                    textAlign = TextAlign.Center)

            }
            Row(modifier = Modifier.fillMaxWidth()){
                Icon(imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black)
                Column(){
                    Text(text = "작업자",color = Color.Gray)
                    Text(text = "홍길동")
                }
                Icon(imageVector = Icons.Default.VideoCameraFront,
                    contentDescription = null,
                    tint = Color(0xFFFF6600))
                Icon(imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = Color(0xFFFF6600))
                Icon(imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = Color(0xFFFF6600))
            }
            Row(){
                Text(text="정보")
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp), // 원하는 높이 설정 가능
                color = Color.Gray // 가로줄 색상 설정 가능
            )
            Row(){
                Button(onClick={},
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF2FA94E)),
                    modifier = Modifier.weight(1f)){
                    Text(text = "처리 완료",
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth())
                }
                Button(onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFFFA500)),
                    modifier = Modifier.weight(1f)){
                    Text(text = "처리 중",
                        color = Color.White)
                }
                Button(onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color.Gray),
                    modifier = Modifier.weight(1f)){
                    Text(text = "오작동",
                        color = Color.White)
                }
                Button(onClick = {},
                    colors = ButtonDefaults.buttonColors(Color(0xFFFF6600)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)){
                    Text(text = "119 신고",
                        color = Color.White,
                        softWrap = false)
                }
            }
        }
    }
}

/*
ModalBottomSheet(
onDismissRequest = { */
/*TODO*//*
 },
sheetState = sheetState,
shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
containerColor = Color.White,
dragHandle = null
) {
    Column(
        modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row() {
            Text(text = "작업 현장 1")
        }
        Row(){
            Icon(imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Black)
            Column(){
                Text(text = "작업자")
                Text(text = "홍길동")
            }
            Icon(imageVector = Icons.Default.VideoCameraFront,
                contentDescription = null,
                tint = Color(0xFFFF6600))
            Icon(imageVector = Icons.Default.Campaign,
                contentDescription = null)
        }
    }
}*/
