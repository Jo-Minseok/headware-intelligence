#ifdef ESP32_S3_CAM
  #define SHOCK 21
  #define PIEZO 
  #define BUTTON
  #define GPS_1
  #define GPS_2
  #define GYRO_1 
  #define GYRO_2
  #define OLED_1
  #define OLED_2
#elif ESP32_CAM
  #define SHOCK 2                 // 충격 센서 핀
  #define PIEZO 14                // 피에조 소자
  #define BUTTON
  #define GPS_1 1                 // GPS
  #define GPS_2 3                 // GPS
  #define GYRO_1 13               // 자이로스코프 센서
  #define GYRO_2 15               // 자이로스코프 센서
  #define OLED_1
  #define OLED_2
#endif