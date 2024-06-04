#define BUTTON 21

void setup(){
  Serial.begin(115200);
	pinMode(BUTTON,INPUT_PULLDOWN);
}

void loop(){
	int push = digitalRead(BUTTON);
  Serial.println("PUSH = " + String(push));
}