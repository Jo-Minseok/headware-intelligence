/*
  ################################################################################################
  #                                    include/define                                            #
  ################################################################################################
*/
#include <ArduinoJson.h>
#include <ArduinoWebsockets.h>
#include <WiFi.h>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <HTTPClient.h>
#include <Wire.h>
#include <BluetoothSerial.h>
#include <MPU6050.h>
#include "esp_camera.h"

using namespace websockets;

// GPIO 4 -> 보드 자체 LED, GPIO 16 -> 보드 자체 WIFI 핀이니까 사용하면 안 됨
#define SHOCK 2                 // 충격 센서 핀
#define PIEZO 14                // 피에조 소자
#define LED_PIN 12              // LED
#define CDS 0                   // 조도 센서
#define GPS_1 1                 // GPS
#define GPS_2 3                 // GPS
#define GYRO_1 13               // 자이로스코프 센서
#define GYRO_2 15               // 자이로스코프 센서
#define CAMERA_MODEL_AI_THINKER // 카메라 모듈

// 백엔드
String server_address = "minseok821lab.kro.kr:8000";
WebsocketsClient client;

// 블루투스
BluetoothSerial SerialBT;

// 앱으로부터 User_ID, 핫스팟 SSID, password 받기
String user_id = "";
String work_id = "";
String ssid = "";
String password = "";
String bluetooth_data = "";

// 시간 구하기
const char *ntpServer = "pool.ntp.org";
const long gmtOffset_sec = 32400;
const int daylightOffset_sec = 0;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, ntpServer, gmtOffset_sec);

// 가속도, 자이로
MPU6050 mpu;
int16_t ax, ay, az; // 가속도
int16_t gx, gy, gz; // 자이로

// 피에조 소자 음
const int melody[] = {262, 294, 330, 349, 392, 440, 494, 523}; // 도, 레, 미, 파, 솔, 라, 시, 도
/*
  ################################################################################################
  #                                       setup()                                                #
  ################################################################################################
*/
void setup()
{
  // 통신 속도 조정
  Serial.begin(115200); // 시리얼 통신 속도를 115200으로 설정

  // 핀 설정
  pinMode(SHOCK, INPUT);    // 충격
  pinMode(PIEZO, OUTPUT);   // 피에조
  pinMode(CDS, INPUT);      // 조명
  pinMode(LED_PIN, OUTPUT); // LED

  // 피에조 설정
  ledcSetup(0, 5000, 8);
  ledcAttachPin(PIEZO, 0);

  // 자이로스코프 설정
  Wire.begin(GYRO_1, GYRO_2);
  mpu.initialize();
  if (mpu.testConnection())
  {
    Serial.println("MPU 연결 성공");
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

  // 블루투스 연결
  BT_connect();

  // 와이파이 연결
  WIFI_connect();

  // 웹소켓 설정
  client.onMessage(onMessageCallback);
  client.onEvent(onEventsCallback);
  client.connect("ws://" + server_address + "/ws/" + work_id + "/" + user_id);

  // 시간
  timeClient.begin();
  timeClient.setTimeOffset(32400);
  timeClient.forceUpdate();

  // 초기 셋팅 완료
  delay(1000);
  tone(PIEZO, melody[0], 500);
  delay(500);
  tone(PIEZO, melody[1], 500);
  delay(500);
  tone(PIEZO, melody[2], 500);
  delay(500);
  tone(PIEZO, melody[3], 500);
  delay(500);
  tone(PIEZO, melody[4], 500);
  delay(500);
  tone(PIEZO, melody[5], 500);
  delay(500);
  tone(PIEZO, melody[6], 500);
  delay(500);
  tone(PIEZO, melody[7], 500);
}

/*
  ################################################################################################
  #                                        loop()                                                #
  ################################################################################################
*/
void loop()
{
  // 빛 감지 (어두우면 자동으로 LED ON)
  light();

  if (WiFi.status() == WL_CONNECTED)
  {
    client.poll();
    // 충격 감지 (낙하 사고)
    if (digitalRead(SHOCK) == HIGH)
    {
      SendingData("낙하");
    }

    // 추락 감지 (낙상 사고)
    mpu.getAcceleration(&ax, &ay, &az);
    mpu.getRotation(&gx, &gy, &gz);
    MPU6050_check();
    /*
      else if(){
      SendingData("낙상");
      Serial.println("낙상 발생!");
      }
    */
  }
  else
  {
    WIFI_connect();
  }
}
/*
  ################################################################################################
  #                                    MPU6050_check()                                           #
  ################################################################################################
*/
void MPU6050_check()
{
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
}

/*
  ################################################################################################
  #                                         light()                                              #
  ################################################################################################
*/
void light()
{
  if (digitalRead(CDS) == HIGH)
  { // 어두울 경우
    digitalWrite(LED_PIN, HIGH);
    Serial.println("어두움");
  }
}

/*
  ################################################################################################
  #                                         블루투스()                                            #
  ################################################################################################
*/
void BT_connect()
{
  // 블루투스 ON
  SerialBT.begin("HEADWARE 1번 헬멧");

  // [블루투스] ID 등록
  SerialBT.println("id");
  while (user_id == "")
  {
    bluetooth_data = SerialBT.readStringUntil('\n');
    if (bluetooth_data[0] == 'i')
    {
      int spacePos = bluetooth_data.indexOf(' ');
      user_id = bluetooth_data.substring(spacePos + 1);
      Serial.println(user_id);
    }
    tone(PIEZO, melody[7], 500);
    delay(1000);
  }
  SerialBT.println("id success!");
  tone(PIEZO, melody[0], 500);
  delay(500);
  tone(PIEZO, melody[1], 500);
  delay(500);
  tone(PIEZO, melody[2], 500);
}

/*
  ################################################################################################
  #                                         WIFI()                                              #
  ################################################################################################
*/
void WIFI_connect()
{
  ssid = "";
  password = "";
  SerialBT.println("wifi");
  while ((ssid == "") || (password == "") || (WiFi.status() != WL_CONNECTED))
  {
    bluetooth_data = SerialBT.readStringUntil('\n');
    if (bluetooth_data[0] == 's')
    {
      int spacePos = bluetooth_data.indexOf(' ');
      ssid = bluetooth_data.substring(spacePos + 1);
      Serial.println("ssid: " + ssid);
    }
    else if (bluetooth_data[0] == 'p')
    {
      int spacePos = bluetooth_data.indexOf(' ');
      password = bluetooth_data.substring(spacePos + 1);
      Serial.println("password: " + password);
    }
    WiFi.begin(ssid, password);
    tone(PIEZO, melody[0], 500);
    delay(1000);
  }
  SerialBT.println("wifi success!");
  tone(PIEZO, melody[5], 500);
  delay(500);
  tone(PIEZO, melody[6], 500);
  delay(500);
  tone(PIEZO, melody[7], 500);
}

/*
  ################################################################################################
  #                                      SendingData()                                           #
  ################################################################################################
*/
void SendingData(String type)
{
  if (WiFi.status() == WL_CONNECTED)
  { // WIFI가 연결되어 있으면
    HTTPClient http;
    http.begin("http://" + server_address + "/accident/upload");// 대상 서버 주소
    http.addHeader("Content-Type", "application/json"); // POST 전송 방식 json 형식으로 전송 multipart/form-data는 이미지 같은 바이너리 데이터

    // Json 형식 설정
    String json_to_string = "";
    JsonDocument send_data; // Json 크기 설정

    // 사고 발생 날짜, 시간 설정
    time_t epochTime = timeClient.getEpochTime();
    struct tm *timeInfo;
    timeInfo = localtime(&epochTime);

    send_data["type"] = type;
    send_data["date"][0] = timeInfo->tm_year + 1900;
    send_data["date"][1] = timeInfo->tm_mon + 1;
    send_data["date"][2] = timeInfo->tm_mday;
    send_data["time"][0] = timeInfo->tm_hour;
    send_data["time"][1] = timeInfo->tm_min;
    send_data["time"][2] = timeInfo->tm_sec;
    send_data["id"] = user_id;
    serializeJsonPretty(send_data, json_to_string);
    send_data.clear();

    int httpResponseCode = http.POST(json_to_string); // http 방식으로 전송 후 반환 값 저장
    json_to_string.clear();
    if (httpResponseCode > 0)
    {
      String response = http.getString(); // http 방식으로 보낸 코드 출력
      Serial.println(response);           // http 방식으로 전송 후 받은 응답 코드 출력
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
    WIFI_connect();
  }
}
/*
  ################################################################################################
  #                                        webSocket()                                           #
  ################################################################################################
*/
void onMessageCallback(WebsocketsMessage message) {
  String receiveData = message.data();
  if (receiveData.startsWith(user_id)) {
    String action = receiveData.substring(receiveData.indexOf(":") + 1);
    if (action == "소리") {
      for (int freq = 150; freq <= 1800; freq = freq + 2) {
        tone(PIEZO, freq, 10);
      }
      for (int freq = 1800; freq <= 150; freq = freq - 2) {
        tone(PIEZO, freq, 10);
      }
    }
  }
}

void onEventsCallback(WebsocketsEvent event, String data) {
    if(event == WebsocketsEvent::ConnectionOpened) {
        Serial.println("웹 소켓 오픈");
    } else if(event == WebsocketsEvent::ConnectionClosed) {
        Serial.println("웹 소켓 폐쇄");
    } else if(event == WebsocketsEvent::GotPing) {
        Serial.println("서버 핑!");
    } else if(event == WebsocketsEvent::GotPong) {
        Serial.println("서버 퐁!");
    }
}
