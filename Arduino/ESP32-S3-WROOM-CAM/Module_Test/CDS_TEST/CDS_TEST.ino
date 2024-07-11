#define CDS 20

void setup(){
	Serial.begin(115200);
	pinMode(CDS,INPUT);
}
int cds_value;
void loop(){
  cds_value = analogRead(CDS);
  Serial.println(cds_value);
}