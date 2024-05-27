//#define ESP32_CAM
#define ESP32_S3_CAM

#ifdef ESP32_S3_CAM
  #include <BLEDevice.h>
  #include <BLEUtils.h>
  #include <BLEScan.h>
  #include <BLEAdvertisedDevice.h>
  #include <BLEServer.h>
  #include <BLE2902.h>
#elif ESP32_CAM
  #include <BTAddress.h>
  #include <BTAdvertisedDevice.h>
  #include <BTScan.h>
  #include <BluetoothSerial.h>
#endif
#include <WiFi.h>
#include <ArduinoWebsockets.h>
#include <HTTPClient.h>
#include <base64.hpp>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <Wire.h>
#include <MPU6050.h>

#include "esp_camera.h"
#include "camera_pins.h"
#include "DFRobot_AXP313A.h"
#include "module_pins.h"

#define SERVICE_UUID "c672da8f-05c6-472f-87d8-34201a97468f"
#define CHARACTERISTIC_UUID "01e7eeab-2597-4c54-84e8-2fceb73c645d"
using namespace websockets;

unsigned int HELMET_NUM = 1;
/*

1 자이로스코프 센서

*/
MPU6050 mpu;
int16_t ax, ay,az,gx,gy,gz;

void GYRO_setup() {
  Serial.println("[SETUP] MPU6050: SETUP START");
  Wire.begin(GYRO_1, GYRO_2);
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

void MPU6050_check(){
  Serial.print("[MPU6050 CHECK] ");
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

2 충격 센서

*/
void SHOCK_check(){
  Serial.println("[SHOCK CHECK] " + analogRead(SHOCK));
}

/*

3 GPS 센서

*/

/*

4 스피커

*/
void PIEZO_setup() {
  Serial.println("[SETUP] PIEZO: SETUP START");
  ledcSetup(0, 5000, 8);
  ledcAttachPin(PIEZO, 0);
  Serial.println("[SETUP] PIEZO: SETUP SUCCESS");
}

void PIEZO_check(){
  tone(PIEZO,261);
}
/*

5 포토레지스터 센서

*/
void CDS_check(){
  Serial.println("[CDS CHECK] " + analogRead(CDS));
}

/*

6 버튼 긴급 호출

*/

void button_check(){

}

/*

7 WIFI 테스트

*/
void WIFI_connect(){
  String ssid = "LAB821(AI&ASIC)",password = "deucom821!";
  Serial.println("[SETUP] WIFI SSID = " + ssid + "WIFI PW = " + password);
  WiFi.begin(ssid, password);
  delay(5000);
}

void WIFI_check(){
  if(WiFi.status() == WL_CONNECTED){
    Serial.println("[WIFI CHECK] 와이파이 연결 O");
  }
  else{
    Serial.println("[WIFI CHECK] 와이파이 연결 X");
  }
}

/*

8 HTTP 테스트

*/
HTTPClient http;
void HTTP_check()
{
  client.close();
  if (WiFi.status() == WL_CONNECTED)
  { // WIFI가 연결되어 있으면
    http.begin("http://" + server_address + "/");// 대상 서버 주소
    int httpResponseCode = http.POST(); // http 방식으로 전송 후 반환 값 저장
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

9 Websocket 테스트

*/
WebsocketsClient client;

void WEBSOCKET_setup() {
  Serial.println("[SETUP] WEBSOCKET: SETUP START");
  client.onMessage(onMessageCallback);
  client.onEvent(onEventsCallback);
  client.connect("ws://" + server_address + "/ws/1234/seok3764");
  Serial.println("[SETUP] WEBSOCKET: SETUP SUCCESS");
}

void onMessageCallback(WebsocketsMessage message) {
  String receiveData = message.data();
  Serial.println("[WEBSOCKET TEST] receiveData = " + receiveData);
}

void onEventsCallback(WebsocketsEvent event, String data) {
  if (event == WebsocketsEvent::ConnectionOpened) {
    Serial.println("[SYSTEM] WEBSOCKET: CONNECT");
  } else if (event == WebsocketsEvent::ConnectionClosed) {
    Serial.println("[SYSTEM] WEBSOCKET: CLOSE");
  }
}

/*

10 카메라 테스트

*/
void CAMERA_setup() {
  Serial.println("[SETUP] CAMERA: SETUP START");
#ifdef ESP32_S3_CAM
  DFRobot_AXP313A axp;
  axp.enableCameraPower(axp.eOV2640);
#endif
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
  config.frame_size = FRAMESIZE_HVGA;
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



/*

11 블루투스 테스트

*/
#ifdef ESP32_S3_CAM
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

  void BT_connect() {
    Serial.println("[SETUP] BLUETOOTH SETUP START");
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

    pTxCharacteristic->setValue("test");
    pTxCharacteristic->notify();
    string test = "";
    while (test == "") {
      delay(100);
      test = bluetooth_data.substring(0);
      Serial.println("Test Data received: " + user_id);
    }
    Serial.println("[SETUP] BLUETOOTH SETUP SUCCESS");
  }

#elif ESP32_CAM

#endif


/*

12 시간 테스트

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

void TIME_check(){
  time_t epochTime = timeClient.getEpochTime();
  struct tm *timeInfo;
  timeInfo = localtime(&epochTime);
  Serial.println("[TIME CHECK] " + timeInfo->tm_year+1900 + "년 " + (timeInfo->tm_mon + 1) +"월 " + (timeInfo->tm_mday) + "일 " +(timeInfo->tm_hour) + "시 " + (timeInfo->tm_min) + "분 " + (timeInfo->tm_sec) + "초");
}

/*

모듈 테스트

*/
void PIN_setup() {
  Serial.println("[SETUP] PIN: SETUP START");
  pinMode(SHOCK, INPUT);    // 충격
  pinMode(PIEZO, OUTPUT);   // 피에조
  pinMode(CDS, INPUT);      // 조명
  Serial.println("[SETUP] PIN: SETUP SUCCESS");
}

void setup(){
  Serial.begin(115200);
  PIN_setup();

  GYRO_setup(); // 1 자이로스코프
  // 3 GPS 센서
  PIEZO_setup(); // 4 스피커
  WIFI_connect(); // 7 WIFI 
  // 8 HTTP
  WEBSOCKET_setup(); // 9 Websocket
  CAMERA_setup(); // 10 카메라
  BT_connect(); // 11 블루투스
  TIME_setup(); // 12 시간
  // 13 OLED
}

void loop(){
  mpu.getMotion6(&ax,&ay,&az,&gx,&gy,&gz); // 1 자이로스코프
  MPU6050_check(); // 1 자이로스코프
  SHOCK_check(); // 2 충격 센서
  // 3 GPS 센서
  PIEZO_check(); // 4 스피커
  CDS_check(); // 5 포토레지스터
  // 6 버튼 긴급 호출
  WIFI_check();// 7 WIFI 테스트
  HTTP_check();// 8 HTTP 테스트
  client.poll(); // 9 Websocket 테스트
  // 10 카메라 테스트
  // 11 블루투스 테스트
  TIME_check(); // 12 시간 테스트
  // 13 OLED 디스플레이
  Serial.println("\n\n\n");
  delay(3000);
}