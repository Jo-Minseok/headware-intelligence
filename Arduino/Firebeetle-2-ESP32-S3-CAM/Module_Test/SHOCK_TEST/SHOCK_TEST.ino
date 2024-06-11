#define ESP32_S3_CAM
#define SHOCK 10

const int debounceDelay = 1000;  // 디바운싱을 위한 지연 시간 (밀리초)
unsigned long lastShockTime = 0; // 마지막 충격 감지 시간
unsigned long lastReadTime = 0;  // 마지막 센서 읽기 시간
const int readInterval = 50;     // 센서 읽기 간격 (밀리초)

const int numReadings = 10;      // 평균값을 계산할 때 사용할 읽기 횟수
int readings[numReadings];       // 읽은 값을 저장할 배열
int readIndex = 0;               // 현재 읽기 인덱스
int total = 0;                   // 읽은 값의 총합
int average = 0;                 // 평균값

void setup() {
  Serial.begin(115200);
  pinMode(SHOCK, INPUT);

  // 배열을 초기화
  for (int i = 0; i < numReadings; i++) {
    readings[i] = 0;
  }
}

void loop() {
  unsigned long currentTime = millis();

  // 센서 값을 주기적으로 읽기 위한 타이밍 체크
  if (currentTime - lastReadTime >= readInterval) {
    lastReadTime = currentTime;

    // 현재 읽기에서 이전 총합에서 해당 읽기 값을 뺌
    total = total - readings[readIndex];

    // 새로운 센서 값을 읽고 배열에 저장
    readings[readIndex] = analogRead(SHOCK);

    // 새로운 읽기 값을 총합에 추가
    total = total + readings[readIndex];

    // 다음 읽기 인덱스로 이동
    readIndex = readIndex + 1;

    // 배열의 끝에 도달하면 다시 시작
    if (readIndex >= numReadings) {
      readIndex = 0;
    }

    // 평균값 계산
    average = total / numReadings;

    if (average > 1500 && (currentTime - lastShockTime) > debounceDelay) { // 충격 감지 값과 디바운스 시간 조정
      Serial.println("Shock detected!");
      lastShockTime = currentTime; // 마지막 충격 감지 시간 업데이트
      average = 0;
    }
  }
}