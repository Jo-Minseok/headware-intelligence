#include <ArduinoWebsockets.h>
#include <WiFi.h>

const char* ssid = "seok3764"; // Enter SSID
const char* password = "seok6317"; // Enter Password
const char* websockets_server_host = "minseok821lab.kro.kr"; // Enter server address
const uint16_t websockets_server_port = 8000; // Enter server port

using namespace websockets;

WebsocketsClient client;
unsigned long lastPingTime = 0;
const unsigned long pingInterval = 30000; // 30 seconds

void connectToWiFi() {
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.println("Connecting to WiFi...");
    }
    Serial.println("Connected to WiFi");
}

void connectToWebSocket() {
    while (!client.connect("ws://minseok821lab.kro.kr:8000/accident/ws/101/test")) {
        Serial.println("Trying to connect to WebSocket server...");
        delay(1000);
    }
    Serial.println("Connected to WebSocket server");
    client.send("Hello Server");
}

void setup() {
    Serial.begin(115200);
    connectToWiFi();
    connectToWebSocket();

    client.onMessage([&](WebsocketsMessage message) {
        Serial.print("Got Message: ");
        Serial.println(message.data());
    });

    client.onEvent([&](WebsocketsEvent event, String data) {
        if (event == WebsocketsEvent::ConnectionClosed) {
            Serial.println("WebSocket connection closed, reconnecting...");
            connectToWebSocket();
        }
    });

    lastPingTime = millis();
}

void loop() {
    if (WiFi.status() != WL_CONNECTED) {
        connectToWiFi();
    }

    if (!client.available()) {
        connectToWebSocket();
    }

    client.poll();

    unsigned long currentTime = millis();
    if (currentTime - lastPingTime >= pingInterval) {
        client.ping();
        lastPingTime = currentTime;
        Serial.println("Sent ping to server");
    }
}
