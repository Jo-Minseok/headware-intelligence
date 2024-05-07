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
#include "camera_pins.h"
#include <base64.hpp>

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
#define CAMERA_MODEL_AI_THINKER // 카메라

// 헬멧 번호
int HELMET_NUM = 1;

// 카메라 버퍼
const int bufferSize = 1024 * 23; // 23552 bytes

// 백엔드
//String server_address = "minseok821lab.kro.kr:8000/accident";
String server_address = "bychul0424.kro.kr:8000/accident";
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
  #                                         CAMERA                                               #
  ################################################################################################
*/
void CAMERA_setup() {
  Serial.println("[SETUP] CAMERA: SETUP START");
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
  config.pin_sscb_sda = SIOD_GPIO_NUM;
  config.pin_sscb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG;
  config.frame_size = FRAMESIZE_240X240; //
  config.jpeg_quality = 30;
  config.fb_count = 2;

  if (esp_camera_init(&config) != ESP_OK) {
    while (true) {
      Serial.println("[ERROR] CAMERA: SETUP FAIL");
      tone(PIEZO, 1500, 250);
      delay(500);
    }
  }
  Serial.println("[SETUP] CAMERA: SETUP SUCCESS");
}

String generateBoundary() {
  String boundary = "--------------------------";
  for (int i = 0; i < 24; i++) {
    boundary += String(random(0, 10));
  }
  return boundary;
}

void capture_and_send_image(String send_id) {
  // 웹 소켓 잠깐 끊기
  client.close();
  // 이미지 촬영
  camera_fb_t * fb = esp_camera_fb_get();
  if (fb != NULL && fb->format == PIXFORMAT_JPEG && fb->len < bufferSize) {
    http.begin("http://" + server_address + "/upload_image");
    String boundary = generateBoundary();
    String fileName = user_id + "_" + send_id + ".jpg";

    // 파일 업로드를 위한 multipart/form-data 시작
    String body = "--";
    body += boundary + "\r\n";
    body += "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n";
    body += "Content-Type: image/jpeg\r\n\r\n";

    // 이미지 데이터 추가
    unsigned char *imageData = fb->buf;
    unsigned int imageDataLength = fb->len;
    unsigned int encodedLength = (imageDataLength + 2) / 3 * 4; // 인코딩 후 길이 계산
    char *encodedData = new char[encodedLength + 1]; // 인코딩된 데이터를 저장할 공간 할당
    encodedData[encodedLength] = '\0';
    encode_base64(imageData, imageDataLength, (unsigned char *)encodedData); // Base64로 인코딩
    // 이미지 촬영 후 메모리 해제
    esp_camera_fb_return(fb);
    // Base64로 인코딩된 데이터를 본문에 추가
    body += String(encodedData);
    
    // HTTP 헤더 설정
    http.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    http.addHeader("Content-Length", String(body.length()));

    // 메모리 해제
    delete[] encodedData;

    // multipart/form-data의 끝 부분 추가
    body += "\r\n--" + boundary + "--\r\n";
    int httpResponseCode = http.POST(body);

    // HTTP 응답 받기
    String response = http.getString();
    Serial.println("[SYSTEM] CAMERA: " + response);
    // HTTP 세션 종료
    http.end();
  }
  else {
    Serial.println("[ERROR] CAMERA: TAKE ERROR");
    WEBSOCKET_setup();
    return;
  }
  Serial.println("[SYSTEM] CAMERA: TAKE SUCCESS");

  // 웹 소켓 재연결
  WEBSOCKET_setup();
  client.send(send_id + ":카메라완료");
}

/*
  ################################################################################################
  #                                          PIEZO                                               #
  ################################################################################################
*/
void PIEZO_setup() {
  Serial.println("[SETUP] PIEZO: SETUP START");
  ledcSetup(0, 5000, 8);
  ledcAttachPin(PIEZO, 0);
  Serial.println("[SETUP] PIEZO: SETUP SUCCESS");
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
  Serial.println("[SYSTEM] PIEZO: HELP CALL");
  for (int i = 0; i < 10; i++) {
    for (int j = 0; j < 2; j++) {
      tone(PIEZO, policeSirenFreq[j], 250); // 주파수별로 소리 재생
      delay(250); // 소리 간 간격
    }
  }
}
/*
  ################################################################################################
  #                                           TIME                                               #
  ################################################################################################
*/
void TIME_setup() {
  Serial.println("[SETUP] TIME: SETUP START");
  timeClient.begin();
  timeClient.setTimeOffset(32400);
  timeClient.forceUpdate();
  Serial.println("[SETUP] TIME: SETUP SUCCESS");
}


/*
  ################################################################################################
  #                                        MPU6050 GYRO                                          #
  ################################################################################################
*/
void GYRO_setup() {
  Serial.println("[SETUP] MPU6050: SETUP START");
  //Wire.begin(13,15);
  Wire.begin(GYRO_1, GYRO_2);
  mpu.initialize();
  while (!mpu.testConnection())
  {
    mpu.initialize();
    Serial.println("[ERROR] MPU6050: SETUP FAIL");
    tone(PIEZO, 1500, 250);
    delay(500);
  }
  mpu.setXAccelOffset(-3597);
  mpu.setYAccelOffset(-5201);
  mpu.setZAccelOffset(1188);
  mpu.setXGyroOffset(-371);
  mpu.setYGyroOffset(-27);
  mpu.setZGyroOffset(-12);
  Serial.println("[SETUP] MPU6050: SETUP SUCCESS");
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
  #                                         CDS LIGHT                                            #
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
  #                                         BLUETOOTH                                            #
  ################################################################################################
*/
void BT_connect()
{
  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP START");
  // 블루투스 ON
  SerialBT.begin("HEADWARE " + String(HELMET_NUM) + "번 헬멧");

  // [블루투스] ID 등록
  while (user_id == "")
  {
    bluetooth_data = SerialBT.readStringUntil('\n');
    if (bluetooth_data[0] == 'i')
    {
      int spacePos = bluetooth_data.indexOf(' ');
      user_id = bluetooth_data.substring(spacePos + 1);
      Serial.println("[SYSTEM] BLUETOOTH: ID=" + user_id);
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
  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP SUCCESS");
}

/*
  ################################################################################################
  #                                          WIFI                                                #
  ################################################################################################
*/
void WIFI_connect()
{
  Serial.println("[SETUP] WIFI: " + String(HELMET_NUM) + ".NO HELMET WIFI SETUP START");
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
      Serial.println("[SYSTEM] WIFI: SSID= " + ssid);
    }
    else if (bluetooth_data[0] == 'p')
    {
      int spacePos = bluetooth_data.indexOf(' ');
      password = bluetooth_data.substring(spacePos + 1);
      Serial.println("[SYSTEM] WIFI: PASSWORD= " + password);
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
  Serial.println("[SETUP] WIFI: " + String(HELMET_NUM) + ".NO HELMET WIFI SETUP SUCCESS");
}

/*
  ################################################################################################
  #                                         HTTP                                                 #
  ################################################################################################
*/
void SendingData(String accident_type)
{
  Serial.println("[SYSTEM] ACCIDENT: " + accident_type + " 사고 감지");
  client.close();
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

    send_data["type"] = accident_type;
    send_data["date"][0] = timeInfo->tm_year + 1900;
    send_data["date"][1] = timeInfo->tm_mon + 1;
    send_data["date"][2] = timeInfo->tm_mday;
    send_data["time"][0] = timeInfo->tm_hour;
    send_data["time"][1] = timeInfo->tm_min;
    send_data["time"][2] = timeInfo->tm_sec;
    send_data["user_id"] = user_id;
    serializeJsonPretty(send_data, json_to_string);
    send_data.clear();

    int httpResponseCode = http.POST(json_to_string); // http 방식으로 전송 후 반환 값 저장
    json_to_string.clear();
    if (httpResponseCode > 0)
    {
      String response = http.getString(); // http 방식으로 보낸 코드 출력
      Serial.println("[SYSTEM] HTTP: " + response);           // http 방식으로 전송 후 받은 응답 코드 출력
    }
    else
    { // 반환 값이 올바르지 않다면
      Serial.print("[ERROR] HTTP: ");
      Serial.println(httpResponseCode);
    }
    http.end();
    WEBSOCKET_setup();
  }
  else
  {
    WIFI_connect();
  }
}
/*
  ################################################################################################
  #                                        WEBSOCKET                                             #
  ################################################################################################
*/
void WEBSOCKET_setup() {
  client.close();
  Serial.println("[SETUP] WEBSOCKET: SETUP START");
  client.onMessage(onMessageCallback);
  client.onEvent(onEventsCallback);
  client.connect("ws://" + server_address + "/ws/" + work_id + "/" + user_id);
  Serial.println("[SETUP] WEBSOCKET: SETUP SUCCESS");
}

void onMessageCallback(WebsocketsMessage message) {
  String receiveData = message.data();
  int firstColonIndex = receiveData.indexOf(":");
  int secondColonIndex = receiveData.indexOf(":", firstColonIndex + 1);
  if (firstColonIndex == -1 || secondColonIndex == -1) {
    Serial.println("[ERROR] WEBSOCKET: NOT FORMAT");
    return;
  }
  String send_id = receiveData.substring(0, firstColonIndex);
  String receive_id = receiveData.substring(firstColonIndex + 1, secondColonIndex);
  String action = receiveData.substring(secondColonIndex + 1);
  if (user_id == receive_id) {
    if (action == "소리") {
      client.send(send_id + ":" + action + "전달");
      playSiren();
      client.send(send_id + ":" + action + "완료");
    }
    else if (action == "카메라") {
      client.send(send_id + ":" + action + "전달");
      capture_and_send_image(send_id);
    }
  }
}

void onEventsCallback(WebsocketsEvent event, String data) {
  if (event == WebsocketsEvent::ConnectionOpened) {
    Serial.println("[SYSTEM] WEBSOCKET: CONNECT");
  } else if (event == WebsocketsEvent::ConnectionClosed) {
    Serial.println("[SYSTEM] WEBSOCKET: CLOSE");
  }
}

/*
  ################################################################################################
  #                                         SETUP                                                #
  ################################################################################################
*/

void PIN_setup() {
  Serial.println("[SETUP] PIN: SETUP START");
  pinMode(SHOCK, INPUT);    // 충격
  pinMode(PIEZO, OUTPUT);   // 피에조
  //pinMode(CDS, INPUT);      // 조명
  //pinMode(LED_PIN, OUTPUT); // LED
  Serial.println("[SETUP] PIN: SETUP SUCCESS");
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
  #                                          LOOP                                                #
  ################################################################################################
*/
void loop()
{
  client.poll();
  // 빛 감지 (어두우면 자동으로 LED ON)
  //light();

  // WEBSOCKET_check(); // 웹소켓 확인

  // 자이로센서 인식
  mpu.getAcceleration(&ax, &ay, &az);
  mpu.getRotation(&gx, &gy, &gz);
  //MPU6050_check();

  // 충격 감지 (낙하 사고)
  if (digitalRead(SHOCK) == HIGH)
  {
    SendingData("낙하");
  }

  // 추락 감지 (낙상 사고)
  /*
    else if(){
    SendingData("낙상");
    Serial.println("낙상 발생!");
    }
  */
}
