/*

  DEFINE

*/
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <BLEServer.h>
#include <BLE2902.h>
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

/*

  ALL AREA

*/
DFRobot_AXP313A axp;
unsigned int HELMET_NUM = 1;

WebsocketsClient client;
HTTPClient http;
MPU6050 mpu;
int16_t ax,ay,az,gx,gy,gz;

String user_id = "", work_id = "1234", bluetooth_data="";
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP,"pool.ntp.org",32400);

/*

  BLUETOOTH Low Energy

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

void BT_connect() {
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

  WIFI

*/

void WIFI_connect(){
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

  CAMERA

*/

void CAMERA_setup(){
  Serial.println("[SETUP] CAMERA: SETUP START");
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

  WEBSOCKET

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

  PIN_SETUP

*/

void PIN_setup(){
  Serial.println("[SETUP] PIN: SETUP START");
  pinMode(SHOCK,INPUT); // 충격
  Serial.println("[SETUP] PIN: SETUP SUCCESS");
}

/*

  SETUP

*/

void setup(){
  Serial.begin(115200);
  PIN_setup();
  CAMERA_setup();
  BT_connect();
  WIFI_connect();
  WEBSOCKET_setup();
}

/*

  LOOP

*/

void loop(){
  client.poll();
  mpu.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);
  if(digitalRead(SHOCK) == HIGH){
    Serial.println("낙하");
  }
}
