#define ESP32_S3_CAM
// #define ESP32_CAM
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <BLEServer.h>
#include <BLE2902.h>
#include <WiFi.h>
#include <ArduinoWebsockets.h>
#include <HTTPClient.h>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <Wire.h>
#include <MPU6050.h>
#include <ArduinoJson.h>

#include "esp_camera.h"
#include "camera_pins.h"
#include "DFRobot_AXP313A.h"
#include "module_pins.h"

#define SERVICE_UUID "c672da8f-05c6-472f-87d8-34201a97468f"
#define CHARACTERISTIC_UUID "01e7eeab-2597-4c54-84e8-2fceb73c645d"
using namespace websockets;
unsigned int HELMET_NUM = 1;
WebsocketsClient client;

String user_id = "", work_id = "1234", bluetooth_data="", server_address = "minseok821lab.kro.kr:8000";

/*
############################################################################
                                  BLE
############################################################################
*/
bool deviceConnected = false;
BLECharacteristic *pTxCharacteristic;
BLECharacteristic *pRxCharacteristic;

class MyServerCallbacks : public BLEServerCallbacks {
  void onConnect(BLEServer* pServer) {
    Serial.println("Bluetooth connected");
    deviceConnected = true;
  }

  void onDisconnect(BLEServer* pServer) {
    Serial.println("Bluetooth disconnected");
    deviceConnected = false;
    pServer->startAdvertising();
  }
};

class MyCallbacks : public BLECharacteristicCallbacks {
  void onWrite(BLECharacteristic *pCharacteristic) {
    String rxValue = pCharacteristic->getValue().c_str();
    if (rxValue.length() > 0) {
      bluetooth_data = rxValue;
      Serial.println("Received: " + bluetooth_data);
    }
  }
};

void BT_setup() {
  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP START");
  String bluetooth_name = "HEADWARE " + String(HELMET_NUM) + "번 헬멧";
  BLEDevice::init(bluetooth_name.c_str());
  
  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  BLEService *pService = pServer->createService(SERVICE_UUID);
  pTxCharacteristic = pService->createCharacteristic(
                        CHARACTERISTIC_UUID,
                        BLECharacteristic::PROPERTY_READ |
                        BLECharacteristic::PROPERTY_NOTIFY
                      );
  pTxCharacteristic->addDescriptor(new BLE2902());

  pRxCharacteristic = pService->createCharacteristic(
                        CHARACTERISTIC_UUID,
                        BLECharacteristic::PROPERTY_WRITE
                      );
  pRxCharacteristic->setCallbacks(new MyCallbacks());

  pService->start();
  pServer->getAdvertising()->start();

  // Wait for user ID
  while (!deviceConnected) {
    delay(100);
  }

  pTxCharacteristic->setValue("id");
  pTxCharacteristic->notify();

  while (user_id == "") {
    delay(100);
    if (bluetooth_data.startsWith("i ")) {
      user_id = bluetooth_data.substring(2);
      Serial.println("User ID received: " + user_id);
    }
  }
  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP SUCCESS");
}
/*
############################################################################
                                  WIFI
############################################################################
*/
void WIFI_setup(){
  Serial.println("[SETUP] WIFI: " + String(HELMET_NUM) + ".NO HELMET WIFI SETUP START");
  String ssid = "",password = "";
  
  while ((ssid == "") || (password == "") || (WiFi.status() != WL_CONNECTED))
  {
    pTxCharacteristic->setValue("wifi");
    pTxCharacteristic->notify();
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
    delay(1000);
  }
  
  Serial.println("[SETUP] WIFI: " + String(HELMET_NUM) + ".NO HELMET WIFI SETUP SUCCESS");
}

/*
############################################################################
                                  TIME
############################################################################
*/
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP,"pool.ntp.org",32400);
void TIME_setup() {
  Serial.println("[SETUP] TIME: SETUP START");
  timeClient.begin();
  timeClient.setTimeOffset(32400);
  timeClient.forceUpdate();
  Serial.println("[SETUP] TIME: SETUP SUCCESS");
}

/*
############################################################################
                                  HTTP
############################################################################
*/
void HTTP_setup(){
  HTTPClient http;
  http.begin("http://"+server_address + "/");
  int httpResponseCode = http.GET();
  while(httpResponseCode != 200){
    Serial.println("[ERROR] 서버 접속 오류");
    tone(PIEZO,251);
    delay(5000);
  }
  Serial.println("[SETUP] SERVER: CONNECT SUCCESS");
}

void SendingData(String type)
{
  if (WiFi.status() == WL_CONNECTED)
  { // WIFI가 연결되어 있으면
    HTTPClient http;
    http.begin("http://" + server_address + "/accident/upload"); // 대상 서버 주소
    http.addHeader("Content-Type", "application/json");          // POST 전송 방식 json 형식으로 전송 multipart/form-data는 이미지 같은 바이너리 데이터

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
    WIFI_setup();
  }
}

/*
############################################################################
                                WEBSOCKET
############################################################################
*/
void WEBSOCKET_setup() {
  client.close();
  Serial.println("[SETUP] WEBSOCKET: SETUP START");
  client.onMessage(onMessageCallback);
  client.onEvent(onEventsCallback);
  client.connect("ws://bychul0424.kro.kr:8000/accident/ws/1234/seok3764");
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
  if (receive_id == "seok3764") {
    if (action == "카메라") {
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
############################################################################
                                CAMERA
############################################################################
*/
void CAMERA_setup(){
  Serial.println("[SETUP] CAMERA: SETUP START");
  DFRobot_AXP313A axp;
  axp.enableCameraPower(axp.eOV2640);//Enable the power for camera
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
  config.frame_size = FRAMESIZE_HVGA;
  config.pixel_format = PIXFORMAT_JPEG; // for streaming
  //config.pixel_format = PIXFORMAT_RGB565; // for face detection/recognition
  if(psramFound()){
      config.jpeg_quality = 10;
      config.fb_count = 2;
      config.grab_mode = CAMERA_GRAB_LATEST;
  } else {
      // Limit the frame size when PSRAM is not available
      config.frame_size = FRAMESIZE_SVGA;
      config.fb_location = CAMERA_FB_IN_DRAM;
  }

  if (esp_camera_init(&config) != ESP_OK) {
    while (true) {
      Serial.println("[ERROR] CAMERA: SETUP FAIL");
      delay(500);
    }
  }
  Serial.println("[SETUP] CAMERA: SETUP SUCCESS");
}

void capture_and_send_image(String send_id) {
  client.close();
  HTTPClient http;
  camera_fb_t * fb = esp_camera_fb_get();
  if (fb != NULL && fb->format == PIXFORMAT_JPEG) {
    http.begin("http://bychul0424.kro.kr:8000/accident/upload_image");
    String boundary = "--------------------------";
    for (int i = 0; i < 24; i++) {
      boundary += String(random(0, 10));
    }
    String fileName = "seok3764_admin.jpg";
    String body = "--";
    body += boundary + "\r\n";
    body += "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\nContent-Type: image/jpeg\r\n\r\n";

    for (size_t i = 0; i < fb->len; i++) {
      body += char(fb->buf[i]);
    }

    esp_camera_fb_return(fb);
    http.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    http.addHeader("Content-Length", String(body.length()));

    body += "\r\n--" + boundary + "--\r\n";
    int httpResponseCode = http.POST(body);

    String response = http.getString();
    Serial.println("[SYSTEM] CAMERA: " + response);
    http.end();
  }
  else {
    Serial.println("[ERROR] CAMERA: TAKE ERROR");
    WEBSOCKET_setup();
    return;
  }
  Serial.println("[SYSTEM] CAMERA: TAKE SUCCESS");

  WEBSOCKET_setup();
  client.send("admin:카메라완료");
}

/*
############################################################################
                                  GYRO
############################################################################
*/
MPU6050 mpu;
int16_t ax,ay,az,gx,gy,gz;
void GYRO_setup() {
  Serial.println("[SETUP] MPU6050: SETUP START");
  Wire.begin(GYRO_SCL, GYRO_SDA);
  mpu.initialize();
  while (!mpu.testConnection())
  {
    mpu.initialize();
    Serial.println("[ERROR] MPU6050: SETUP FAIL");
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

/*
############################################################################
                                  SPEAKER
############################################################################
*/
void PIEZO_setup() {
  Serial.println("[SETUP] PIEZO: SETUP START");
  ledcSetup(0, 5000, 8);
  ledcAttachPin(PIEZO, 0);
  Serial.println("[SETUP] PIEZO: SETUP SUCCESS");
}

/*
############################################################################
                    PIN SETUP(SHOCK, BUTTON, CDS, LED)
############################################################################
*/
void PIN_setup(){
  Serial.println("[SETUP] PIN: SETUP START");
  pinMode(SHOCK,INPUT); // 충격
  Serial.println("[SETUP] PIN: SETUP SUCCESS");
}

/*
############################################################################
                                  Main
############################################################################
*/

void setup(){
  Serial.begin(115200);

  // 0 OLED 디스플레이
  BT_setup(); // 1 블루투스
  WIFI_setup(); // 2 WIFI
  TIME_setup(); // 3 시간
  HTTP_setup(); // 4 HTTP
  WEBSOCKET_setup(); // 5 WEBSOCKET
  CAMERA_setup(); // 6 카메라
  GYRO_setup(); // 7 자이로스코프
  PIEZO_setup(); // 8 스피커
  PIN_setup(); // 9 핀 셋업
  // 10 GPS 셋업
}

void loop(){
  client.poll();
  mpu.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);
  if(digitalRead(SHOCK) == HIGH){
    Serial.println("낙하");
    // SendingData("낙하");
  }
}
