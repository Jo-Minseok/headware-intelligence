#ifdef ESP32_S3_CAM
  #define PWDN_GPIO_NUM     -1
  #define RESET_GPIO_NUM    -1
  #define XCLK_GPIO_NUM     45
  #define SIOD_GPIO_NUM     1
  #define SIOC_GPIO_NUM     2

  #define Y9_GPIO_NUM       48
  #define Y8_GPIO_NUM       46
  #define Y7_GPIO_NUM       8
  #define Y6_GPIO_NUM       7
  #define Y5_GPIO_NUM       4
  #define Y4_GPIO_NUM       41
  #define Y3_GPIO_NUM       40
  #define Y2_GPIO_NUM       39
  #define VSYNC_GPIO_NUM    6
  #define HREF_GPIO_NUM     42
  #define PCLK_GPIO_NUM     5
#elif ESP32_CAM
  #define PWDN_GPIO_NUM     32
  #define RESET_GPIO_NUM    -1
  #define XCLK_GPIO_NUM      0
  #define SIOD_GPIO_NUM     26
  #define SIOC_GPIO_NUM     27

  #define Y9_GPIO_NUM       35
  #define Y8_GPIO_NUM       34
  #define Y7_GPIO_NUM       39
  #define Y6_GPIO_NUM       36
  #define Y5_GPIO_NUM       21
  #define Y4_GPIO_NUM       19
  #define Y3_GPIO_NUM       18
  #define Y2_GPIO_NUM        5
  #define VSYNC_GPIO_NUM    25
  #define HREF_GPIO_NUM     23
  #define PCLK_GPIO_NUM     22
#endif