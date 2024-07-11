#include "esp_camera.h"
#define SHOCK 21
#define PIEZO 42
unsigned long shockStartTime = 0;
bool shockDetected = false;
bool shockConfirmed = false;
unsigned long lastShockTime = 0;

int melody[] = {262, 294, 330, 349, 392, 440, 494, 523};

void setup() {
  Serial.begin(115200);
  pinMode(SHOCK, INPUT);
  ledcAttachPin(PIEZO, LEDC_CHANNEL_0); // PIEZO 핀에 LEDC 채널 연결
}

void loop() {
  int shockState = digitalRead(SHOCK);
  unsigned long currentTime = millis();
  Serial.println(shockState);
  if (shockState == 1 && currentTime - lastShockTime > 300) {
    if (!shockDetected) {
      shockDetected = true;
      shockStartTime = currentTime;
    } else if (currentTime - shockStartTime >= 50 && !shockConfirmed) {
      tone(PIEZO,melody[4],500);
      Serial.println("[SYSTEM] 낙하 감지");
      shockConfirmed = true;  // Mark the shock as confirmed
      lastShockTime = currentTime;  // Update the last shock time
      shockDetected = false;  // Reset the detection state
    }
  } else if (shockState == 0) {
    shockDetected = false;  // Reset the detection state if no shock is detected
    shockConfirmed = false;  // Allow new shocks to be detected after cooldown
  }
}
