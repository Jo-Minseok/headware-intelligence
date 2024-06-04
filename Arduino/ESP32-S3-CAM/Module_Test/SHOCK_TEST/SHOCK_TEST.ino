#define ESP32_S3_CAM
#define SHOCK 10

int i=0;
bool pri = false;
void setup() {
  Serial.begin(115200);
  pinMode(SHOCK, INPUT);
}

void loop() {
  if(analogRead(SHOCK)>4000){
    i++;
    Serial.println(i);
  }
  else{
    i = 0;
  }
}