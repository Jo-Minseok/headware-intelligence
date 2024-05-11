#include <ArduinoWebsockets.h>
#include <BluetoothSerial.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include "esp_camera.h"
#include "camera_pins.h"
#include <base64.hpp>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <Wire.h>
#include <MPU6050.h>
using namespace websockets;
#define SHOCK 2                 // 충격 센서 핀
#define PIEZO 14                // 피에조 소자
#define GPS_1 1                 // GPS
#define GPS_2 3                 // GPS
#define GYRO_1 13               // 자이로스코프 센서
#define GYRO_2 15               // 자이로스코프 센서
#define CAMERA_MODEL_AI_THINKER // 카메라
unsigned int HELMET_NUM = 1;

WebsocketsClient client;
HTTPClient http;
BluetoothSerial SerialBT;

MPU6050 mpu;
int16_t ax, ay, az,gx, gy, gz;

String user_id = "",work_id = "1234",bluetooth_data = "";
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 32400);

void BT_connect()
{
  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP START");
  SerialBT.begin("HEADWARE " + String(HELMET_NUM) + "번 헬멧");
  while (user_id == "")
  {
    bluetooth_data = SerialBT.readStringUntil('\n');
    if (bluetooth_data[0] == 'i')
    {
      int spacePos = bluetooth_data.indexOf(' ');
      user_id = bluetooth_data.substring(spacePos + 1);
      Serial.println("[SYSTEM] BLUETOOTH: ID=" + user_id);
    }
    delay(1000);
  }
  SerialBT.println("id success!");
  Serial.println("[SETUP] BLUETOOTH: " + String(HELMET_NUM) + ".NO HELMET BLUETOOTH SETUP SUCCESS");
}

void WIFI_connect(){
  Serial.println("[SETUP] WIFI: " + String(HELMET_NUM) + ".NO HELMET WIFI SETUP START");
  String ssid = "",password = "";
  SerialBT.println("wifi");
  while ((ssid == "") || (password == "") || (WiFi.status() != WL_CONNECTED))
  {
    bluetooth_data = SerialBT.readStringUntil('\n');
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
  SerialBT.println("wifi success!");
  Serial.println("[SETUP] WIFI: " + String(HELMET_NUM) + ".NO HELMET WIFI SETUP SUCCESS");
}

void CAMERA_setup() {
  Serial.println("[SETUP] CAMERA: SETUP START");
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
  config.jpeg_quality = 20;
  config.fb_count = 1;

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

void PIN_setup() {
  Serial.println("[SETUP] PIN: SETUP START");
  pinMode(SHOCK, INPUT);    // 충격
  pinMode(PIEZO, OUTPUT);   // 피에조
  Serial.println("[SETUP] PIN: SETUP SUCCESS");
}

void TIME_setup() {
  Serial.println("[SETUP] TIME: SETUP START");
  timeClient.begin();
  timeClient.setTimeOffset(32400);
  timeClient.forceUpdate();
  Serial.println("[SETUP] TIME: SETUP SUCCESS");
}

void GYRO_setup() {
  Serial.println("[SETUP] MPU6050: SETUP START");
  //Wire.begin(13,15);
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

void MPU6050_check()
{
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

void setup()
{
  Serial.begin(115200);
  PIN_setup();
  PIEZO_setup();
  GYRO_setup();
  CAMERA_setup();
  BT_connect();
  WIFI_connect();
  WEBSOCKET_setup();
  TIME_setup();
}

void loop()
{
  client.poll();
  mpu.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);
}
