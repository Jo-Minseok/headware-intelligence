#include <SocketIOclient.h>
#include <WiFi.h>

SocketIOclient socketIO;

void socketIOEvent(socketIOmessageType_t type, uint8_t * payload, size_t length) {
    switch(type) {
        case sIOtype_DISCONNECT:
            Serial.println("DISCONNECT");
            break;
        case sIOtype_CONNECT:
            Serial.println("CONNECT");
            socketIO.send(sIOtype_CONNECT, "/");
            break;
        case sIOtype_EVENT:
            Serial.printf("EVENT: %s\n", payload);
            break;
        case sIOtype_ACK:
            Serial.println("ACK");
            break;
        case sIOtype_ERROR:
            Serial.println("ERROR");
            break;
        case sIOtype_BINARY_EVENT:
            Serial.println("BINARY_EVENT");
            break;
        case sIOtype_BINARY_ACK:
            Serial.println("BINARY_ACK");
            break;
    }
}

void setup() {
  Serial.begin(115200);
  WiFi.begin("seok3764", "seok6317");

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  
  Serial.println("Connected to WiFi");

  // server address, port and URL
  socketIO.begin("minseok821lab.kro.kr", 8000, "/accident/ws/101/test");

  // event handler
  socketIO.onEvent(socketIOEvent);
}

void loop() {
  socketIO.loop();
}