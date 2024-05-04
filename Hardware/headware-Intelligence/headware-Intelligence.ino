#include <ArduinoJson.h>
#include <ArduinoJson.hpp>
#include <WiFi.h>
#include <HTTPClient.h>
#include <Wire.h>

#define SHOCK 2   // 충격 센서 핀
#define PIEZO 14   // 피에조 소자
#define LED 15    // LED
#define PHOTO 16   // 조도센서 임시로 16이고, 원래라면 0
#define GPS_1 1   // GPS
#define GPS_2 3   // GPS
#define GYRO_1 12 // 자이로스코프 센서
#define GYRO_2 13 // 자이로스코프 센서

const char* server_address = "http://minseok821lab.kro.kr:8000/"; // 백엔드 주소
int AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;

void setup()
{
  Serial.begin(9600);
  pinMode(SHOCK, INPUT); // 충격
  pinMode(PIEZO,OUTPUT); // 피에조
  pinMode(PHOTO, INPUT); // 조명
  pinMode(LED,OUTPUT); // LED
  
  /* MPU6050(자이로스코프)
  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(핀 주소);
  Wire.wrtie(0); MPU-6050 작동
  Wire.endTransmission(true);
  */
}

void loop()
{
  // 빛 감지 (어두우면 자동으로 LED ON)
  
  // 충격 감지 (낙하 사고)
  if (digitalRead(SHOCK)==HIGH){
    SendingData("낙하");
    Serial.println("충격이 발생!");
  }
  // 추락 감지 (낙상 사고)
  /*
  else if(){
    SedingData("낙상");
    Serial.println("낙상 발생!");
  }
  */
}

void SendingData(String type){
  if(WiFi.status() == WL_CONNECTED){
    HTTPClient http; // HTTP 객체 생성
    http.begin(server_address); // 대상 서버 주소
    http.addHeader("Content-Type", "application/x-www-form-urlencoded"); // POST 전송 방식 가장 기본 content-type. 데이터를 url 인코딩 후 웹 서버 전송. multipart/form-data는 이미지 같은 바이너리 데이터
    String httpRequestData = "type=" + type;
    int httpResponseCode = http.POST(httpRequestData); // http 방식으로 전송 후 반환 값 저장
    if(httpResponseCode > 0){
      String response = http.getString(); // http 방식으로 보낸 코드 출력
      Serial.println(httpResponseCode); // http 방식으로 전송 후 받은 응답 코드 출력
    }
    else{
      Serial.print("Error on sending POST: ");
      Serial.println(httpResponseCode);
    }
    http.end();
  }
  else{
    tone(PIEZO,262,125); // 1000 / 8= 125 8분음표
  }
}
