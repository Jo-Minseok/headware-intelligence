#define RESET 9

void setup(){
  Serial.begin(115200);
  digitalWrite(RESET,HIGH);
  Serial.println("Setup Start");
  delay(1000);
  pinMode(RESET,OUTPUT);
}

void loop(){
  Serial.println("Loop Start");
  Serial.print("Time: ");
  delay(1000);
  digitalWrite(RESET,LOW);
  Serial.println("HERE");
}