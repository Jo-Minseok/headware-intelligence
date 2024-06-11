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

#define SERVICE_UUID "c672da8f-05c6-472f-87d8-34201a97468f"
#define CHARACTERISTIC_READ "01e7eeab-2597-4c54-84e8-2fceb73c645d"
#define CHARACTERISTIC_WRITE "5a9edc71-80cb-4159-b2e6-a2913b761026"
using namespace websockets;
unsigned int HELMET_NUM = 1;

String user_id = "", work_id = "", bluetooth_data="",wifi_id = "", wifi_pw = "", server_address = "minseok821lab.kro.kr:8000";
int melody[] = {262, 294, 330, 349, 392, 440, 494, 523};
String latitude;
String longitude;

const int buttonDebounceDelay = 50;  // 디바운싱을 위한 지연 시간 (밀리초)
int lastButtonState = LOW;     // 이전 버튼 상태
int buttonState;               // 현재 버튼 상태
unsigned long lastDebounceTime = 0;  // 마지막 디바운스 시간

const int shockDebounceDelay = 1000;  // 디바운싱을 위한 지연 시간 (밀리초)
unsigned long lastShockTime = 0; // 마지막 충격 감지 시간
unsigned long lastReadTime = 0;  // 마지막 센서 읽기 시간
const int readInterval = 50;     // 센서 읽기 간격 (밀리초)

const int numReadings = 10;      // 평균값을 계산할 때 사용할 읽기 횟수
int readings[numReadings];       // 읽은 값을 저장할 배열
int readIndex = 0;               // 현재 읽기 인덱스
int total = 0;                   // 읽은 값의 총합
int average = 0;                 // 평균값

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
    pTxCharacteristic->setValue(("helmet_num " + String(HELMET_NUM)).c_str());
    pTxCharacteristic->notify();
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
        
        latitude = bluetooth_data.substring(latStart, latEnd);
        longitude = bluetooth_data.substring(lonStart);
      }
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
    delay(1000);
  }
  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP SUCCESS");
  pTxCharacteristic->setValue(("helmet_num " + String(HELMET_NUM)).c_str());
  pTxCharacteristic->notify();
}

void ID_setup(){
  pTxCharacteristic->setValue("user_id");
  pTxCharacteristic->notify();

  Serial.println("[SETUP] USER ID: SETUP START");

  while (user_id == "") {
    pTxCharacteristic->setValue("user_id");
    pTxCharacteristic->notify();
    delay(1000);
  }
  Serial.println("[SETUP] USER ID: SETUP SUCCESS");

  pTxCharacteristic->setValue("work_id");
  pTxCharacteristic->notify();
  Serial.println("[SETUP] WORK ID: SETUP START");

  while (work_id == "") {
    pTxCharacteristic->setValue("work_id");
    pTxCharacteristic->notify();
    delay(1000);
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
  while (true)
  {
    pTxCharacteristic->setValue("wifi");
    pTxCharacteristic->notify();

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
  delay(300);
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
    jsonPayload += "\"latitude\":" + latitude + ",";
    jsonPayload += "\"longitude\":"+ longitude + ",";
    jsonPayload += "\"work_id\":\"" + work_id + "\",";
    jsonPayload += "\"victim_id\":\"" + user_id + "\"";
    jsonPayload += "}";

    Serial.println(jsonPayload);
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
    }
    else if(action == "소리"){
      client.send(send_id + ":" + action + "전달");
    }
  }
}

void onEventsCallback(WebsocketsEvent event, String data) {
  if (event == WebsocketsEvent::ConnectionOpened) {
    Serial.println("[SYSTEM] WEBSOCKET: CONNECT");
  } else if (event == WebsocketsEvent::ConnectionClosed) {
    Serial.println("[SYSTEM] WEBSOCKET: CLOSE");
    client.connect("ws://"+server_address+"/accident/ws/"+work_id + "/" + user_id);
  }
}

/*
############################################################################
                                  Main
############################################################################
*/

void setup(){
  Serial.begin(115200);
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
}

void loop(){
  if(Serial.available()){
    char input = Serial.read();
    if(input == '1'){
      SendingData("낙하");
    }
  }
}
