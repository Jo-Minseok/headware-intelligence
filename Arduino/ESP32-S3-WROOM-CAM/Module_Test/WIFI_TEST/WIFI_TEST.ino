#include <WiFi.h>

void WIFI_connect(){
  String ssid = "LAB821(AI&ASIC)",password = "deucomputer821";
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

void setup(){
	Serial.begin(115200);
	WIFI_connect();
}

void loop(){
	WIFI_check();
  delay(1000);
}
