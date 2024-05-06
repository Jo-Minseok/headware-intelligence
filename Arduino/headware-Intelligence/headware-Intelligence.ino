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
#include <Base64.h>
#include "esp_camera.h"
#include "camera_pins.h"

using namespace websockets;

// GPIO 4 -> 보드 자체 LED, GPIO 16 -> 보드 자체 WIFI 핀이니까 사용하면 안 됨
#define SHOCK 2                 // 충격 센서 핀
#define PIEZO 14                // 피에조 소자
//#define LED_PIN 12              // LED
//#define CDS 0                   // 조도 센서
#define GPS_1 1                 // GPS
#define GPS_2 3                 // GPS
#define GYRO_1 13               // 자이로스코프 센서
#define GYRO_2 15               // 자이로스코프 센서
#define CAMERA_MODEL_AI_THINKER // 카메라

// 백엔드
String server_address = "minseok821lab.kro.kr:8000/accident";
WebsocketsClient client;
HTTPClient http;

// 블루투스
BluetoothSerial SerialBT;

// 앱으로부터 User_ID, 핫스팟 SSID, password 받기
String user_id = "";
String work_id = "1234";
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
const int policeSirenFreq[] = {500, 1500};

/*
  ################################################################################################
  #                                        Cameara                                               #
  ################################################################################################
*/
void CAMERA_setup() {
  // 카메라 초기화
  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sccb_sda = SIOD_GPIO_NUM;
  config.pin_sccb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.frame_size = FRAMESIZE_HVGA;
  config.pixel_format = PIXFORMAT_JPEG; // for streaming
  //config.pixel_format = PIXFORMAT_RGB565; // for face detection/recognition
  config.grab_mode = CAMERA_GRAB_WHEN_EMPTY;
  config.fb_location = CAMERA_FB_IN_PSRAM;
  config.jpeg_quality = 12; // 원래 12
  config.fb_count = 1;

  if (esp_camera_init(&config) != ESP_OK) {
    while (true) {
      Serial.println("카메라 초기화 실패");
      tone(PIEZO, 1500, 250);
      delay(500);
    }
  }
  Serial.println("카메라 초기화 성공");
}

void capture_and_send_image(String send_id) {
  // 이미지 촬영
  camera_fb_t *fb = esp_camera_fb_get();
  if (!fb) {
    Serial.println("이미지 촬영 실패");
    return;
  }

  // 서버로 전송할 이미지를 메모리에 저장
  if (!client.available()) {
    Serial.println("웹 소켓 연결 불가");
    esp_camera_fb_return(fb);
    return;
  }

  size_t fb_len = fb->len;
  uint8_t *fb_buf = fb->buf;

  JsonDocument send_data;
  send_data["send_id"] = send_id;
  send_data["image_size"] = String(fb_len);
  String base64_encoded_image = base64::encode(fb_buf, fb_len);
  send_data["image_data"] = base64_encoded_image;
  String jsonString;
  serializeJson(send_data, jsonString);
  Serial.println(jsonString);

  client.send(send_id+":카메라완료");
  if (client.send(jsonString) != 1) {
    Serial.println("이미지 전송 실패");
  } else {
    Serial.println("이미지 전송 성공");
  }

  // 이미지 촬영 후 메모리 해제
  esp_camera_fb_return(fb);
}

/*
  ################################################################################################
  #                                         Melody                                               #
  ################################################################################################
*/
void PIEZO_setup() {
  ledcSetup(0, 5000, 8);
  ledcAttachPin(PIEZO, 0);
}

void setup_success() {
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

void playSiren() {
  for (int i = 0; i < 10; i++) {
    for (int j = 0; j < 2; j++) {
      tone(PIEZO, policeSirenFreq[j], 250); // 주파수별로 소리 재생
      delay(250); // 소리 간 간격
    }
  }
}
/*
  ################################################################################################
  #                                           시간                                               #
  ################################################################################################
*/
void TIME_setup() {
  timeClient.begin();
  timeClient.setTimeOffset(32400);
  timeClient.forceUpdate();
}
/*
  ################################################################################################
  #                                  MPU6050 자이로스코프                                         #
  ################################################################################################
*/
void GYRO_setup() {
  //Wire.begin(13,15);
  Wire.begin(GYRO_1, GYRO_2);
  mpu.initialize();
  while (!mpu.testConnection())
  {
    mpu.initialize();
    Serial.println("MPU 연결 실패");
    tone(PIEZO, 1500, 250);
    delay(500);
  }
  Serial.println("MPU 연결 성공");
  Serial.println("MPU 보정 시작");
  mpu.setXAccelOffset(-3597);
  mpu.setYAccelOffset(-5201);
  mpu.setZAccelOffset(1188);
  mpu.setXGyroOffset(-371);
  mpu.setYGyroOffset(-27);
  mpu.setZGyroOffset(-12);
}

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
  #                                       CDS 조도센서                                            #
  ################################################################################################
*/
/*
  void light()
  {
  if (digitalRead(CDS) == HIGH)
  { // 어두울 경우
    digitalWrite(LED_PIN, HIGH);
    Serial.println("어두움");
  }
  }
*/

/*
  ################################################################################################
  #                                         블루투스                                              #
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
  #                                          WIFI                                                #
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
  #                                         HTTP                                                 #
  ################################################################################################
*/
void SendingData(String type)
{
  if (WiFi.status() == WL_CONNECTED)
  { // WIFI가 연결되어 있으면
    http.begin("http://" + server_address + "/upload");// 대상 서버 주소
    http.addHeader("Content-Type", "application/json"); // POST 전송 방식 json 형식으로 전송 multipart/form-data는 이미지 같은 바이너리 데이터

    // Json 형식 설정
    String json_to_string = "";
    JsonDocument send_data;

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
  #                                        WebSocket                                             #
  ################################################################################################
*/
void WEBSOCKET_setup() {
  client.onMessage(onMessageCallback);
  client.onEvent(onEventsCallback);
  client.connect("ws://" + server_address + "/ws/" + work_id + "/" + user_id);
}

void onMessageCallback(WebsocketsMessage message) {
  String receiveData = message.data();
  int firstColonIndex = receiveData.indexOf(":");
  int secondColonIndex = receiveData.indexOf(":", firstColonIndex + 1);
  if (firstColonIndex == -1 || secondColonIndex == -1) {
    Serial.println("Invalid Message Format");
    return;
  }
  String send_id = receiveData.substring(0, firstColonIndex);
  String receive_id = receiveData.substring(firstColonIndex + 1, secondColonIndex);
  String action = receiveData.substring(secondColonIndex + 1);
  Serial.println("Message:" + receiveData);
  Serial.println("send_id:" + send_id);
  Serial.println("receive_id:" + receive_id);
  Serial.println("action:" + action);
  if (user_id == receive_id) {
    if (action == "소리") {
      client.send(send_id + ":" + action + "완료");
      playSiren();
    }
    else if (action == "카메라") {
      capture_and_send_image(send_id);
    }
  }
}

void onEventsCallback(WebsocketsEvent event, String data) {
  if (event == WebsocketsEvent::ConnectionOpened) {
    Serial.println("웹 소켓 오픈");
  } else if (event == WebsocketsEvent::ConnectionClosed) {
    Serial.println("웹 소켓 폐쇄");
  }
}

/*
  ################################################################################################
  #                                       setup()                                                #
  ################################################################################################
*/

void PIN_setup() {
  pinMode(SHOCK, INPUT);    // 충격
  pinMode(PIEZO, OUTPUT);   // 피에조
  //pinMode(CDS, INPUT);      // 조명
  //pinMode(LED_PIN, OUTPUT); // LED
}

void setup()
{
  // 통신 속도 조정
  Serial.begin(115200); // 시리얼 통신 속도를 115200으로 설정

  // 핀 설정
  PIN_setup();

  // 피에조 설정
  PIEZO_setup();

  // 자이로스코프 설정
  GYRO_setup();

  // 카메라
  CAMERA_setup();

  // 블루투스 연결
  BT_connect();

  // 와이파이 연결
  WIFI_connect();

  // 웹소켓 설정
  WEBSOCKET_setup();

  // 시간
  TIME_setup();

  // 초기 셋팅 완료
  setup_success();
}

/*
  ################################################################################################
  #                                        loop()                                                #
  ################################################################################################
*/
void loop()
{
  // 빛 감지 (어두우면 자동으로 LED ON)
  //light();

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
    //MPU6050_check();
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
