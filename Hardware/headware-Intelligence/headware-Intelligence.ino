// #include <ArduinoJson.h>
// #include <ArduinoJson.hpp>
#include <WiFi.h>
#include <HTTPClient.h>
#include <Wire.h>
#include <BluetoothSerial.h>
#include <MPU6050.h>

// GPIO 4 -> 보드 자체 LED, GPIO 16 -> 보드 자체 WIFI
#define SHOCK 2    // 충격 센서 핀
#define PIEZO 14   // 피에조 소자
#define LED_PIN 12 // LED
#define CDS 0      // 조도 센서
#define GPS_1 1    // GPS
#define GPS_2 3    // GPS
#define GYRO_1 13  // 자이로스코프 센서
#define GYRO_2 15  // 자이로스코프 센서

// WIFI
const char *server_address = "http://minseok821lab.kro.kr:8000/"; // 백엔드 주소
const char *ssid = "seok3764";
const char *password = "";

// Bluetooth
BluetoothSerial SerialBT;

// 가속도, 자이로
MPU6050 mpu;
int16_t ax, ay, az; // 가속도
int16_t gx, gy, gz; // 자이로

void setup()
{
  // 통신 속도 조정
  Serial.begin(115200); // 시리얼 통신 속도를 115200으로 설정

  // 핀 설정
  pinMode(SHOCK, INPUT);    // 충격
  pinMode(PIEZO, OUTPUT);   // 피에조
  pinMode(CDS, INPUT);      // 조명
  pinMode(LED_PIN, OUTPUT); // LED

  // 블루투스 연결
  SerialBT.begin("HEADWARE 1번 헬멧");

  // WiFi 연결
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(1000);
    Serial.println("Connecting to Wifi..");
  }
  Serial.println("Connected to the Wifi network");

  // MPU6050(자이로스코프)
  Wire.begin(GYRO_1, GYRO_2);
  mpu.initialize();
  if (mpu.testConnection())
  {
    Serial.println("MPU 연결");
    Serial.println("MPU 보정 시작");
    mpu.setXAccelOffset(-3597);
    mpu.setYAccelOffset(-5201);
    mpu.setZAccelOffset(1188);
    mpu.setXGyroOffset(-371);
    mpu.setYGyroOffset(-27);
    mpu.setZGyroOffset(-12);
  }
  else
  {
    Serial.println("MPU 연결 실패");
  }
}

void loop()
{
  // 빛 감지 (어두우면 자동으로 LED ON)
  if (digitalRead(CDS) == HIGH)
  { // 어두울 경우
    digitalWrite(LED_PIN, HIGH);
    Serial.println("어두움");
  }

  if (WiFi.status() == WL_CONNECTED)
  {
    // 충격 감지 (낙하 사고)
    if (digitalRead(SHOCK) == HIGH)
    {
      SendingData("낙하");
      Serial.println("SHOCK!");
    }

    // 추락 감지 (낙상 사고)
    mpu.getAcceleration(&ax, &ay, &az);
    mpu.getRotation(&gx, &gy, &gz);
    Serial.print("기울기:");
    Serial.print("    X=");
    Serial.print(ax / 8192);
    Serial.print("    |    Y=");
    Serial.print(ay / 8192);
    Serial.print("    |    Z=");
    Serial.print(az / 8192);
    Serial.print("    |    가속도:");
    Serial.print("    |    X=");
    Serial.print(gx / 1310);
    Serial.print("    |    Y=");
    Serial.print(gy / 1310);
    Serial.print("    |    Z=");
    Serial.println(gz / 1310);
    /*
    else if(){
      SendingData("낙상");
      Serial.println("낙상 발생!");
    }
    */
  }
  else
  {
    // tone(PIEZO,330,125); // 330HZ는 '미'음에 해당
  }
}

void SendingData(String type)
{
  if (WiFi.status() == WL_CONNECTED)
  {
    HTTPClient http;                                                     // HTTP 객체 생성
    http.begin(server_address);                                          // 대상 서버 주소
    http.addHeader("Content-Type", "application/x-www-form-urlencoded"); // POST 전송 방식 가장 기본 content-type. 데이터를 url 인코딩 후 웹 서버 전송. multipart/form-data는 이미지 같은 바이너리 데이터
    String httpRequestData = "type=" + type + "&" + "";
    int httpResponseCode = http.POST(httpRequestData); // http 방식으로 전송 후 반환 값 저장
    if (httpResponseCode > 0)
    {
      String response = http.getString(); // http 방식으로 보낸 코드 출력
      Serial.println(httpResponseCode);   // http 방식으로 전송 후 받은 응답 코드 출력
    }
    else
    { // 반환 값이 올바르지 않다면
      Serial.print("Error on sending POST: ");
      Serial.println(httpResponseCode);
    }
    http.end();
  }
  else
  {
    // tone(PIEZO,262,125); // 1000 / 8= 125 8분음표
  }
}
