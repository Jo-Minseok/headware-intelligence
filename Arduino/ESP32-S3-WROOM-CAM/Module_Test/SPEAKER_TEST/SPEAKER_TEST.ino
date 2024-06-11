#define SPEAKER 12

void PIEZO_setup(){
	Serial.println("[SETUP] PIEZO: SETUP START");
  	ledcSetup(0, 5000, 8);
  	ledcAttachPin(SPEAKER, 0);
	Serial.println("[SETUP] PIEZO: SETUP SUCCESS");
}

void PIEZO_check(){
	tone(SPEAKER,261,1000);
}

void setup(){
	Serial.begin(115200);
	PIEZO_setup();
}

void loop(){
	PIEZO_check();
	delay(3000);
}