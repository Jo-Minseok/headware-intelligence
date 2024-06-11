#include <WiFi.h>
#include <WiFiUdp.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <BLEServer.h>
#include <BLE2902.h>
#include <HTTPClient.h>
#include <NTPClient.h>
#include <ArduinoWebsockets.h>
//#include <MPU6050.h>

#include "esp_camera.h"
#include "camera_pins.h"
#include "DFRobot_AXP313A.h"
#include "module_pins.h"

#include <Adafruit_SSD1306.h>
#include <Adafruit_GFX.h>
#include <Wire.h>

#define SERVICE_UUID "c672da8f-05c6-472f-87d8-34201a97468f"
#define CHARACTERISTIC_READ "01e7eeab-2597-4c54-84e8-2fceb73c645d"
#define CHARACTERISTIC_WRITE "5a9edc71-80cb-4159-b2e6-a2913b761026"
using namespace websockets;
unsigned int HELMET_NUM = 1;

Adafruit_SSD1306 display(128,64,&Wire,-1);

String user_id = "", work_id = "", bluetooth_data="",wifi_id = "", wifi_pw = "", server_address = "minseok821lab.kro.kr:8000";
int melody[] = {262, 294, 330, 349, 392, 440, 494, 523};
double latitude = 0.000000;
double longitude = 0.000000;
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
    Serial.println("[BLE] Bluetooth connected");
    deviceConnected = true;
  }

  void onDisconnect(BLEServer* pServer) {
    Serial.println("[BLE] Bluetooth disconnected");
    deviceConnected = false;
    pServer->startAdvertising();
  }
};

class MyCallbacks : public BLECharacteristicCallbacks {
  void onWrite(BLECharacteristic *pCharacteristic) {
    String rxValue = pCharacteristic->getValue().c_str();
    if (rxValue.length() > 0) {
      bluetooth_data = rxValue;
      if(bluetooth_data.startsWith("ui ")){
        user_id = bluetooth_data.substring(3);
        Serial.println("[BLE] USER ID = " + user_id);
      }
      else if(bluetooth_data.startsWith("wd ")){
        work_id = bluetooth_data.substring(3);
        Serial.println("[BLE] WORK ID = " + work_id);
      }
      else if(bluetooth_data.startsWith("wc ")){
        work_id = bluetooth_data.substring(3);
        Serial.println("[BLE] WORK ID = " +work_id);
        pTxCharacteristic->setValue("work_id_change");
        pTxCharacteristic->notify();
      }
      else if(bluetooth_data.startsWith("wi ")){
        wifi_id = bluetooth_data.substring(3);
        Serial.println("[BLE] WIFI ID = " + wifi_id);
      }
      else if(bluetooth_data.startsWith("wp ")){
        wifi_pw = bluetooth_data.substring(3);
        Serial.println("[BLE] WIFI PW = " + wifi_pw);
      }
      else if(bluetooth_data.startsWith("gps ")){
        Serial.println("[BLE] GPS = " + bluetooth_data);
        int latStart = bluetooth_data.indexOf("lat:") + 4;
        int latEnd = bluetooth_data.indexOf(",", latStart);
        int lonStart = bluetooth_data.indexOf("lon:") + 4;
        
        String latitudeStr = bluetooth_data.substring(latStart, latEnd);
        String longitudeStr = bluetooth_data.substring(lonStart);

        latitude = latitudeStr.toDouble();
        longitude = longitudeStr.toDouble();
      }
    }
  }
};

void BT_setup() {
  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP START");
  String bluetooth_name = "HEADWARE " + String(HELMET_NUM) + "번 헬멧";

  display.clearDisplay();
  HELMETNUM_display();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(10,24);
  display.println("BLUETOOTH");
  display.display();

  BLEDevice::init(bluetooth_name.c_str());
  
  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  BLEService *pService = pServer->createService(SERVICE_UUID);
  pTxCharacteristic = pService->createCharacteristic(
                        CHARACTERISTIC_READ,
                        BLECharacteristic::PROPERTY_NOTIFY
                      );
  pTxCharacteristic->addDescriptor(new BLE2902());

  pRxCharacteristic = pService->createCharacteristic(
                        CHARACTERISTIC_WRITE,
                        BLECharacteristic::PROPERTY_WRITE
                      );
  pRxCharacteristic->setCallbacks(new MyCallbacks());

  pService->start();
  pServer->getAdvertising()->start();

  // Wait for user ID
  while (!deviceConnected) {
    tone(PIEZO,melody[0],500);
    delay(1000);
  }

  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP SUCCESS");
}

void ID_setup(){
  pTxCharacteristic->setValue(("helmet_num " + String(HELMET_NUM)).c_str());
  pTxCharacteristic->notify();

  pTxCharacteristic->setValue("user_id");
  pTxCharacteristic->notify();

  Serial.println("[SETUP] USER ID: SETUP START");
  display.clearDisplay();
  HELMETNUM_display();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(10,24);
  display.println("USER ID");
  display.display();

  while (user_id == "") {
    pTxCharacteristic->setValue("user_id");
    pTxCharacteristic->notify();
    delay(1000);
    tone(PIEZO,melody[1],200);
  }
  Serial.println("[SETUP] USER ID: SETUP SUCCESS");

  pTxCharacteristic->setValue("work_id");
  pTxCharacteristic->notify();
  Serial.println("[SETUP] WORK ID: SETUP START");
  display.clearDisplay();
  HELMETNUM_display();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(10,24);
  display.println("WORK ID");
  display.display();

  while (work_id == "") {
    pTxCharacteristic->setValue("work_id");
    pTxCharacteristic->notify();
    delay(1000);
    tone(PIEZO,melody[1],200);
  }
  Serial.println("[SETUP] WORK ID: SETUP SUCCESS");
}
/*
############################################################################
                                  WIFI
############################################################################
*/
void WIFI_setup(){
  Serial.println("[SETUP] WIFI: SETUP START");
  display.clearDisplay();
  HELMETNUM_display();
  display.setTextSize(3);
  display.setTextColor(WHITE);
  display.setCursor(10,24);
  display.println("WIFI");
  display.display();
  while (true)
  {
    pTxCharacteristic->setValue("wifi");
    pTxCharacteristic->notify();

    tone(PIEZO,melody[2],500);
    while(wifi_id == ""){
      pTxCharacteristic->setValue("wifi_id");
      pTxCharacteristic->notify();
      delay(1000);
    }
    pTxCharacteristic->setValue("wifi_pw");
    pTxCharacteristic->notify();
    delay(5000);
    Serial.println("WIFI ID = " + wifi_id + " PASSWORD = " + wifi_pw);
    WiFi.begin(wifi_id, wifi_pw);
    delay(10000);
    if(WiFi.status() == WL_CONNECTED){
      break;
    }
    else{
      Serial.println("연결안됨");
    }
    Serial.println("[SETUP] WIFI ID = " + wifi_id + " WIFI PASSWORD = " + wifi_pw);
  }
  
  Serial.println("[SETUP] WIFI: SETUP SUCCESS");
  pTxCharacteristic->setValue("wifi_success");
  pTxCharacteristic->notify();
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
  if(WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin("http://"+server_address + "/");
    int httpResponseCode = http.GET();
    while(httpResponseCode != 200){
      Serial.println("[ERROR] SERVER: CONNECT FAIL");
      tone(PIEZO,251);
      delay(5000);
    }
    Serial.println("[SETUP] SERVER: CONNECT SUCCESS");
  }
  else{
    WIFI_setup();
  }
}

void Emergency(){
  if(WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin("http://" + server_address + "/accident/emergency?work_id=" + work_id + "&user_id="+user_id);
    int httpResponseCode = http.GET();
  }
  else{
    WIFI_setup();
  }
}

void SendingData(String type)
{
  pTxCharacteristic->setValue("GPS");
  pTxCharacteristic->notify();
  if (WiFi.status() == WL_CONNECTED)
  { // WIFI가 연결되어 있으면
    HTTPClient http;
    http.begin("http://" + server_address + "/accident/upload"); // 대상 서버 주소
    http.addHeader("Content-Type", "application/json");          // POST 전송 방식 json 형식으로 전송 multipart/form-data는 이미지 같은 바이너리 데이터

    // 사고 발생 날짜, 시간 설정
    time_t epochTime = timeClient.getEpochTime();
    struct tm *timeInfo;
    timeInfo = localtime(&epochTime);

    String jsonPayload = "{";
    jsonPayload += "\"category\":\"" + type + "\",";
    jsonPayload += "\"date\":[" + String(timeInfo->tm_year + 1900) + "," + String(timeInfo->tm_mon + 1) + "," + String(timeInfo->tm_mday) + "],";
    jsonPayload += "\"time\":[" + String(timeInfo->tm_hour) + "," + String(timeInfo->tm_min) + "," + String(timeInfo->tm_sec) + "],";
    jsonPayload += "\"latitude\":" + String(latitude) + ",";
    jsonPayload += "\"longitude\":"+ String(longitude) + ",";
    jsonPayload += "\"work_id\":\"" + work_id + "\",";
    jsonPayload += "\"victim_id\":\"" + user_id + "\"";
    jsonPayload += "}";

    int httpResponseCode = http.POST(jsonPayload);
    if (httpResponseCode == 200)
    {
      String response = http.getString();
      Serial.println(response);
    }
    else
    { // 반환 값이 올바르지 않다면
      Serial.print("[ERROR] HTTP SENDING ERROR = ");
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
WebsocketsClient client;
void WEBSOCKET_setup() {
  client.close();
  Serial.println("[SETUP] WEBSOCKET: SETUP START");
  client.onMessage(onMessageCallback);
  client.onEvent(onEventsCallback);
  client.connect("ws://"+server_address+"/accident/ws/"+work_id + "/" + user_id);
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
  if (user_id==receive_id) {
    if (action == "카메라") {
      client.send(send_id + ":" + action + "전달");
      capture_and_send_image(send_id);
    }
    else if(action == "소리"){
      client.send(send_id + ":" + action + "전달");
      PLAY_SIREN();
      client.send(send_id+":소리완료");
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
                                  SPEAKER
############################################################################
*/
void PLAY_SIREN(){
  for(int i=0;i<10;i++){
    tone(PIEZO,melody[3],250);
    tone(PIEZO,melody[7],250);
  }
}

void SUCCESS_setup(){
  display.clearDisplay();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0,26);
  display.println("SUCCESS");
  display.display();
  tone(PIEZO,melody[0],500);
  tone(PIEZO,melody[2],500);
  tone(PIEZO,melody[4],500);
  delay(2000);
  display.clearDisplay();
  display.display();
}

void PIEZO_setup(){
  Serial.println("[SETUP] PIEZO: SETUP START");
  ledcSetup(LEDC_CHANNEL_0, 5000, 8); // LEDC 초기화
  ledcAttachPin(PIEZO, LEDC_CHANNEL_0); // PIEZO 핀에 LEDC 채널 연결
  Serial.println("[SETUP] PIEZO: SETUP SUCCESS");
}

/*
############################################################################
                  PIN SETUP(SHOCK, BUTTON, CDS ,LED)
############################################################################
*/
void PIN_setup(){
  Serial.println("[SETUP] PIN: SETUP START");
  pinMode(SHOCK,INPUT); // 충격
  pinMode(BUTTON,INPUT_PULLDOWN); // 긴급 버튼
  pinMode(CDS,INPUT);
  pinMode(LED,OUTPUT);
  digitalWrite(RESET,HIGH);
  pinMode(RESET,OUTPUT);
  Serial.println("[SETUP] PIN: SETUP SUCCESS");
}

/*
############################################################################
                                CAMERA
############################################################################
*/
const int CAM_addr = 0x36;
void CAMERA_setup(){
  Serial.println("[SETUP] CAMERA: SETUP START");
  DFRobot_AXP313A axp;
  while(axp.begin()!=0){
    Serial.println("[SETUP] CAMERA: DFRobot init error");
    delay(100);
  }
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
  config.grab_mode = CAMERA_GRAB_WHEN_EMPTY;
  config.fb_location = CAMERA_FB_IN_PSRAM;
  config.jpeg_quality = 12;
  config.fb_count = 1;

  if(psramFound()){
      config.jpeg_quality = 10;
      config.fb_count = 2;
      config.grab_mode = CAMERA_GRAB_LATEST;
  } else {
      // Limit the frame size when PSRAM is not available
      config.frame_size = FRAMESIZE_SVGA;
      config.fb_location = CAMERA_FB_IN_DRAM;
  }

  while (esp_camera_init(&config) != ESP_OK) {
      Serial.println("[ERROR] CAMERA: SETUP FAIL");
      delay(500);
  }
  Serial.println("[SETUP] CAMERA: SETUP SUCCESS");
}  

void capture_and_send_image(String send_id) {
//  client.close();
  HTTPClient http;
  camera_fb_t * fb = esp_camera_fb_get();
  if (fb != NULL && fb->format == PIXFORMAT_JPEG) {
    http.begin("http://"+server_address+"/accident/upload_image");
    String boundary = "--------------------------";
    for (int i = 0; i < 24; i++) {
      boundary += String(random(0, 10));
    }
    String fileName = user_id+"_"+send_id+".jpg";
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

//  WEBSOCKET_setup();
  client.send(send_id+":카메라완료");
}

/*
############################################################################
                                  OLED
############################################################################
*/

const int OLED_addr = 0x3C;
void OLED_setup(){
  Serial.println("[SETUP] OLED: SETUP START");
  //Wire.begin(0,9);
  display.begin(SSD1306_SWITCHCAPVCC,OLED_addr);
  display.clearDisplay();
  HELMETNUM_display();
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0,26);
  display.println("HEAD BUDDY");
  display.display();
  Serial.println("[SETUP] OLED: SETUP SUCCESS");
}

void HELMETNUM_display(){
  display.setTextSize(1);
  display.setTextColor(WHITE);
  display.setCursor(0,0);
  display.println("NO. " + String(HELMET_NUM));
  display.display();
}

/*
############################################################################
                                  GYRO
############################################################################
*/
/*
MPU6050 mpu;
const int MPU_addr=0x68;
int16_t ax,ay,az,gx,gy,gz;
void GYRO_setup() {
  Serial.println("[SETUP] MPU6050: SETUP START");
  //Wire.begin(GYRO_SDA,GYRO_SCL);
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

void GYRO_check(){
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
*/

/*
############################################################################
                                  Main
############################################################################
*/

void setup(){
  Serial.begin(115200);
  Wire.begin();

  OLED_setup(); // OLED
  delay(1000);
  PIN_setup(); // 핀 셋업
  delay(1000);
  PIEZO_setup(); // 스피커
  delay(1000);
  BT_setup(); // 블루투스
  delay(1000);
  ID_setup(); // ID 설정
  delay(1000);
  WIFI_setup(); // WIFI
  delay(1000);
  TIME_setup(); // 시간
  delay(1000);
  HTTP_setup(); // HTTP
  delay(1000);
  WEBSOCKET_setup(); // WEBSOCKET
  delay(1000);
  /*
  GYRO_setup(); // 자이로스코프
  delay(1000);
  */
  SUCCESS_setup(); // 셋업 완료
  delay(1000);
  CAMERA_setup(); // 카메라
}

const int debounceDelay = 50;  // 디바운싱을 위한 지연 시간 (밀리초)
int lastButtonState = LOW;     // 이전 버튼 상태
int buttonState;               // 현재 버튼 상태
unsigned long lastDebounceTime = 0;  // 마지막 디바운스 시간

void loop(){
  //mpu.getMotion6(&ax,&ay,&az,&gx,&gy,&gz);
  pTxCharacteristic->setValue("GPS");
  pTxCharacteristic->notify();
  delay(1000);
}
