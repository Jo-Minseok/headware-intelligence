#define LED 47

void setup(){
	Serial.begin(115200);
	pinMode(LED,OUTPUT);
  
}
void loop(){
  digitalWrite(LED,1);
}