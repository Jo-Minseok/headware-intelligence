/*
    Video: https://www.youtube.com/watch?v=oCMOYS71NIU
    Based on Neil Kolban example for IDF: https://github.com/nkolban/esp32-snippets/blob/master/cpp_utils/tests/BLE%20Tests/SampleNotify.cpp
    Ported to Arduino ESP32 by Evandro Copercini

   Create a BLE server that, once we receive a connection, will send periodic notifications.
   The service advertises itself as: 6E400001-B5A3-F393-E0A9-E50E24DCCA9E
   Has a characteristic of: 6E400002-B5A3-F393-E0A9-E50E24DCCA9E - used for receiving data with "WRITE" 
   Has a characteristic of: 6E400003-B5A3-F393-E0A9-E50E24DCCA9E - used to send data with  "NOTIFY"

   The design of creating the BLE server is:
   1. Create a BLE Server
   2. Create a BLE Service
   3. Create a BLE Characteristic on the Service
   4. Create a BLE Descriptor on the characteristic
   5. Start the service.
   6. Start advertising.

*/

/* This example domenstrates the Bluetooth data transparent transmission function. Burn the code, open serial monitor, turn on the BLE debugger on the phone, then,
 * 1. you can see the data sent by ESP32-S3--see APP usage image 
 * 2. send data to ESP32-S3 by the input box of BLE debugger--see APP usage image 
 * This example originates from BLE_uart sample 
 */

#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#define SERVICE_UUID "c672da8f-05c6-472f-87d8-34201a97468f"
#define CHARACTERISTIC_READ "01e7eeab-2597-4c54-84e8-2fceb73c645d"
#define CHARACTERISTIC_WRITE "5a9edc71-80cb-4159-b2e6-a2913b761026"

bool deviceConnected = false;
BLEServer *pServer = NULL;
BLECharacteristic * pTxCharacteristic;
BLECharacteristic *pRxCharacteristic;

//Bluetooth connect/disconnect. Auto triggered when connection/disconnection event occurs. 
class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {   //Execute this function when Bluetooth is connected. 
      Serial.println("Bluetooth connected");
      deviceConnected = true;
    };

    void onDisconnect(BLEServer* pServer) {  //Execute this function when Bluetooth is disconnected
      Serial.println("Bluetooth disconnected");
      deviceConnected = false;
      delay(500); // give the bluetooth stack the chance to get things ready
      pServer->startAdvertising(); // restart advertising

    }
};

/****************Data Receiving*************/
/****************************************/
//Process received Bluetooth data. Auto triggered when data received. 
class MyCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      std::string rxValue = pCharacteristic->getValue();//Receive data, and assign it to rxValue

      //if(rxValue == "ON"){Serial.println("Turn light on");}   //Determine whether the received character is "ON"

      if (rxValue.length() > 0) {
        Serial.println("*********");
        Serial.print("Received Value: ");
        for (int i = 0; i < rxValue.length(); i++){
          Serial.print(rxValue[i]);
        }
        Serial.println();
        Serial.println("*********");
      }
    }
};

void BLEBegin(){
  // Create the BLE Device
  BLEDevice::init("HEADWARE TEST");

  // Create the BLE Server
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Create the BLE Service
  BLEService *pService = pServer->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  pTxCharacteristic = pService->createCharacteristic(
                    CHARACTERISTIC_READ,
                    BLECharacteristic::PROPERTY_NOTIFY
                  );

  pTxCharacteristic->addDescriptor(new BLE2902());

  BLECharacteristic * pRxCharacteristic = pService->createCharacteristic(
                      CHARACTERISTIC_WRITE,
                      BLECharacteristic::PROPERTY_WRITE
                    );

  pRxCharacteristic->setCallbacks(new MyCallbacks());

  // Start the service
  pService->start();

  // Start advertising
  pServer->getAdvertising()->start();
  Serial.println("Waiting a client connection to notify...");
}

uint8_t txValue = 0;

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/



/***************************************/
/****************************************/


void setup() {
  Serial.begin(115200);
  BLEBegin();  //Init Bluetooth

}

void loop() {
/****************Data Transmitting*************/
/****************************************/
  if (deviceConnected) {  //Transmit data when the Bluetooth is connected. 
    pTxCharacteristic->setValue("Hello");  //Send char string 
    pTxCharacteristic->notify();
    delay(1000); // bluetooth stack will go into congestion, if too many packets are sent

    pTxCharacteristic->setValue("DFRobot");  //Send char string
    pTxCharacteristic->notify();
    delay(1000); // bluetooth stack will go into congestion, if too many packets are sent
  }
/****************************************/
/****************************************/
}

