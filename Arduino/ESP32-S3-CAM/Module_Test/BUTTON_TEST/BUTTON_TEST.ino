/*
#define BUTTON 21

void setup(){
  Serial.begin(115200);
	pinMode(BUTTON,INPUT_PULLDOWN);
}

void loop(){
	int push = digitalRead(BUTTON);
  Serial.println("PUSH = " + String(push));
}
*/
#define BUTTON 21

const int debounceDelay = 50;  // 디바운싱을 위한 지연 시간 (밀리초)
int lastButtonState = LOW;     // 이전 버튼 상태
int buttonState;               // 현재 버튼 상태
unsigned long lastDebounceTime = 0;  // 마지막 디바운스 시간

void setup(){
  Serial.begin(115200);
  pinMode(BUTTON, INPUT_PULLDOWN);
}

void loop(){
  int reading = digitalRead(BUTTON); // 버튼 읽기

  // 버튼 상태가 바뀌었는지 확인
  if (reading != lastButtonState) {
    // 상태가 바뀌었으므로 디바운싱 타이머를 리셋
    lastDebounceTime = millis();
  }

  // 디바운싱 지연 시간을 넘었으면 상태를 업데이트
  if ((millis() - lastDebounceTime) > debounceDelay) {
    // 상태가 변하고 일정 시간 유지된 경우에만 버튼 상태 업데이트
    if (reading != buttonState) {
      buttonState = reading;

      // 버튼이 눌렸는지 확인 (버튼이 눌린 상태는 HIGH)
      if (buttonState == HIGH) {
        Serial.println("PUSH = 1");
      } else {
        Serial.println("PUSH = 0");
      }
    }
  }

  lastButtonState = reading; // 이전 버튼 상태 업데이트
  Serial.println("눕");
}