/*
############################################################################
                                  WIFI
############################################################################
*/
#include <WiFi.h>
#include <ArduinoWebsockets.h>
String server_address = "bychul0424.kro.kr:8000";
void WIFI_setup(){
  Serial.println("[SETUP] WIFI SETUP START");
  String ssid = "seok3764",password = "seok6317";
 
    WiFi.begin(ssid, password);
    delay(1000);
  }
  
  Serial.println("[SETUP] WIFI SETUP SUCCESS");
}

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

void setup(){
  Serial.begin(115200);
  WIFI_connect(); // 7 WIFI 
  WEBSOCKET_setup(); // 9 Websocket
}

void loop(){
  client.poll(); // 9 Websocket 테스트
  delay(3000);
}