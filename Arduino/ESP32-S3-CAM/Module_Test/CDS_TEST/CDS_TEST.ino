#define CDS 14
#define LED 13

void setup(){
	Serial.begin(115200);
	pinMode(CDS,INPUT);
  pinMode(LED,OUTPUT);
}

void loop(){
  Serial.println(analogRead(CDS));
  if(analogRead(CDS) == 4095){
    digitalWrite(LED,1);
  }
  else{
    digitalWrite(LED,0);
  }
  
}