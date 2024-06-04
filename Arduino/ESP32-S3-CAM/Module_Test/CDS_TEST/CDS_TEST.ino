#define ESP32_S3_CAM
#define CDS 8
#define LED 6

void setup(){
	Serial.begin(115200);
	pinMode(CDS,INPUT);
  pinMode(LED,OUTPUT);
}

void loop(){
	Serial.println(analogRead(CDS));
  if(analogRead(CDS)>=1400){
    digitalWrite(LED,1);
  }
  else{
    digitalWrite(LED,0);
  }
}