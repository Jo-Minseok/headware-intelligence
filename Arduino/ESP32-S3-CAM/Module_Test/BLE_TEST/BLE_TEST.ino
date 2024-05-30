#include <WiFi.h>
#include <WiFiUdp.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <BLEServer.h>
#include <BLE2902.h>

#include "module_pins.h"

#include <Adafruit_SSD1306.h>
#include <Adafruit_GFX.h>
#include <Wire.h>

#define SERVICE_UUID "c672da8f-05c6-472f-87d8-34201a97468f"
#define CHARACTERISTIC_READ "01e7eeab-2597-4c54-84e8-2fceb73c645d"
#define CHARACTERISTIC_WRITE "5a9edc71-80cb-4159-b2e6-a2913b761026"

unsigned int HELMET_NUM = 1;

Adafruit_SSD1306 display(128,64,&Wire,-1);

String user_id = "", work_id = "", bluetooth_data="",wifi_id = "", wifi_pw = "", server_address = "minseok821lab.kro.kr:8000";
int melody[] = {262, 294, 330, 349, 392, 440, 494, 523};
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
      Serial.println("[BLE] RECEIVED DATA " + bluetooth_data);
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
                        BLECharacteristic::PROPERTY_READ
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
    delay(100);
  }

  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP SUCCESS");
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
                                  Main
############################################################################
*/

void setup(){
  Serial.begin(115200);
  Wire.begin();

  OLED_setup(); // OLED
  delay(1000);
  BT_setup(); // 블루투스 
}

void loop(){
  if (Serial.available()) { // 시리얼로부터 데이터가 수신되었는지 확인
    String input = Serial.readStringUntil('\n'); // 문자열을 읽음 (줄 바꿈 문자까지)
    Serial.print("Received from serial: ");
    Serial.println(input); // 입력된 문자열을 시리얼 모니터에 출력

    // BLE로 데이터 전송
    if (deviceConnected) { // BLE 장치가 연결되어 있는지 확인
      pTxCharacteristic->setValue(input.c_str()); // BLE 특성에 입력된 문자열 설정
      pTxCharacteristic->notify(); // 연결된 BLE 장치로 데이터를 전송
      Serial.println("Sent over BLE: " + input);
    } else {
      Serial.println("No BLE device connected.");
    }
  }
}
