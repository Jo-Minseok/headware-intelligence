#include <MPU6050.h>
MPU6050 mpu;
int16_t ax, ay,az,gx,gy,gz;

void GYRO_setup() {
  Serial.println("[SETUP] MPU6050: SETUP START");
  Wire.begin();
  mpu.initialize();
  while (!mpu.testConnection())
  {
    mpu.initialize();
    Serial.println("[ERROR] MPU6050: SETUP FAIL");
    delay(500);
  }
  mpu.setXAccelOffset(-3597);
  mpu.setYAccelOffset(-5201);
  mpu.setZAccelOffset(1188);
  mpu.setXGyroOffset(-371);
  mpu.setYGyroOffset(-27);
  mpu.setZGyroOffset(-12);
  Serial.println("[SETUP] MPU6050: SETUP SUCCESS");
}

void GYRO_check(){
  Serial.print("[MPU6050 CHECK] ");
  Serial.print("기울기:");
  Serial.print("    X=");
  Serial.print(ax / 8192);
  Serial.print("    |    Y=");
  Serial.print(ay / 8192);
  Serial.print("    |    Z=");
  Serial.print(az / 8192);
  Serial.print("    |    가속도:");
  Serial.print("    |    X=");
  Serial.print(gx / 1310);
  Serial.print("    |    Y=");
  Serial.print(gy / 1310);
  Serial.print("    |    Z=");
  Serial.println(gz / 1310);
}

void setup(){
  Serial.begin(115200);
  GYRO_setup();
}

void loop(){
  mpu.getMotion6(&ax,&ay,&az,&gx,&gy,&gz);
  GYRO_check();
  delay(1000);
}
