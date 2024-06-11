#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <MPU6050.h>

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
#define OLED_RESET -1

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);
MPU6050 mpu;

void setup() {
  Serial.begin(115200);

  // 시작 메시지 출력
  Serial.println("[SETUP] OLED: SETUP START");

  // I2C 통신 초기화
  Wire.begin();

  // OLED 디스플레이 초기화
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("[SETUP] OLED: SSD1306 allocation failed"));
    for(;;);
  }

  // MPU6050 초기화
  mpu.initialize();

  // 성공 메시지 출력
  Serial.println("[SETUP] OLED: SETUP SUCCESS");
}

void loop() {
  // 가속도 및 자이로 스코프 값을 읽기
  int16_t ax, ay, az;
  int16_t gx, gy, gz;
  mpu.getAcceleration(&ax, &ay, &az);
  mpu.getRotation(&gx, &gy, &gz);

  // OLED 화면 지우기
  display.clearDisplay();

  // 값을 OLED 화면에 출력
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.print("Accel (m/s^2):");
  display.setCursor(0, 10);
  display.print("X: "); display.print(ax / 16384.0);
  display.setCursor(50, 10);
  display.print("Y: "); display.print(ay / 16384.0);
  display.setCursor(100, 10);
  display.print("Z: "); display.println(az / 16384.0);

  display.setCursor(0, 30);
  display.print("Gyro (deg/s):");
  display.setCursor(0, 40);
  display.print("X: "); display.print(gx / 131.0);
  display.setCursor(50, 40);
  display.print("Y: "); display.print(gy / 131.0);
  display.setCursor(100, 40);
  display.print("Z: "); display.println(gz / 131.0);

  // 디스플레이 업데이트
  display.display();

  // 잠시 대기
  delay(500);
}