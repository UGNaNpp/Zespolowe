#include <Arduino.h>
#include <WiFi.h>
#include <AsyncUDP.h>
#include <WiFiUdp.h>
#include "esp_camera.h"

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

const IPAddress mother_ip(192, 168, 1, 4);
const int mother_port = 9876;
const IPAddress broadcastIP(192, 168, 255, 255);

const String wifi_ssid = "SSID";
const String wifi_password = "PASSWORD";

WiFiUDP udp;
uint32_t transmission_number = 0;


void udp_fragmenter_sender(
  const uint8_t *data,
  size_t len,
  size_t fragment_size,
  const IPAddress &ip,
  uint16_t port,
  uint32_t transmission_number = 0
) {

  uint16_t num_fragments =  len / fragment_size + 1;

  uint16_t fragment_number = 0;
  size_t sent = 0;
  while (sent < len) {
    size_t to_send = std::min(fragment_size, len - sent);

    uint8_t buffer[1472];

    
    uint32_t transmission_len = (uint32_t)len;
    uint16_t payload_len = (uint16_t)to_send;

    // universal header
    /*memcpy(buffer+0, &transmission_number, 4);
    memcpy(buffer+4, &fragment_number, 2);
    memcpy(buffer+6, &num_fragments, 2);
    memcpy(buffer+8, &transmission_len, 4);
    memcpy(buffer+12, &payload_len, 2);
*/

    uint8_t *vp = (uint8_t *)&transmission_number;
    buffer[0] = vp[3];
    buffer[1] = vp[2];
    buffer[2] = vp[1];
    buffer[3] = vp[0];

    vp = (uint8_t *)&fragment_number;
    buffer[4] = vp[1];
    buffer[5] = vp[0];

    vp = (uint8_t *)&num_fragments;
    buffer[6] = vp[1];
    buffer[7] = vp[0];

    vp = (uint8_t *)&transmission_len;
    buffer[8] = vp[3];
    buffer[9] = vp[2];
    buffer[10] = vp[1];
    buffer[11] = vp[0];

    vp = (uint8_t *)&payload_len;
    buffer[12] = vp[1];
    buffer[13] = vp[0];

    /*Serial.printf("transmission_number: %lu\n", (uint32_t)buffer[0]);
    Serial.printf("fragment_number: %u\n", (uint16_t)buffer[4]);
    Serial.printf("num_fragments: %u\n", (uint16_t)buffer[6]);
    Serial.printf("len: %lu\n", (uint32_t)buffer[8]);
    Serial.printf("to_send: %u\n", (uint16_t)buffer[12]);*/

    Serial.println("BYTES in hex:");
    for(int i = 0; i < 14; i++)
    {
      Serial.printf("%02X ", buffer[i]);
    }
    // specific header


    //////////////////////////
    //

    memcpy(buffer+64, data+sent, to_send);

    udp.beginPacket(ip, port);
    udp.write(buffer, to_send+64);
    udp.endPacket();
    fragment_number++;
    sent += to_send;
  }
}

void setup() {
  Serial.begin(74880);

  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sccb_sda = SIOD_GPIO_NUM;
  config.pin_sccb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG; 
  
  if(psramFound()){
    config.frame_size = FRAMESIZE_UXGA;
    config.jpeg_quality = 6;
    config.fb_count = 2;
  } else {
    config.frame_size = FRAMESIZE_SVGA;
    config.jpeg_quality = 12;
    config.fb_count = 1;
  }
  
  // Camera init
  esp_err_t err = esp_camera_init(&config);
  if (err != ESP_OK) {
    Serial.printf("Camera init failed with error 0x%x", err);
    return;
  }

  WiFi.begin(wifi_ssid, wifi_password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  Serial.println("WiFi connected");
  Serial.println(WiFi.localIP());
  
  udp.begin(1234);

  // Start listening for UDP messages
  /*if (udp.listen(1234)) {
    Serial.println("Listening on UDP port 1234");
    
    // When data is received
    udp.onPacket([](AsyncUDPPacket packet) {
      Serial.print("Received: ");
      Serial.write(packet.data(), packet.length());
      Serial.println();
      
      // Optional: Send response back to sender
      packet.printf("Received %u bytes", packet.length());
    });
  } else {
    Serial.println("Failed to start UDP listener");
  }

  // Send an initial broadcast packet
  sendUnicast();
*/

  while(true)
  { 
    Serial.println("Camera capture: delay 6000");
    delay(6000);

    esp_err_t res = ESP_OK;
    size_t _jpg_buf_len = 0;
    uint8_t * _jpg_buf = NULL;
    char * part_buf[64];

    camera_fb_t * fb = esp_camera_fb_get();

    while(!fb)
    {
      Serial.println("Camera capture failed");
      delay(300);
      fb = esp_camera_fb_get();
    }

    Serial.println("Camera capture: frame captured");
    if(fb->width > 400){
      if(fb->format != PIXFORMAT_JPEG){
        bool jpeg_converted = frame2jpg(fb, 80, &_jpg_buf, &_jpg_buf_len);
        esp_camera_fb_return(fb);
        fb = NULL;
        if(!jpeg_converted){
          Serial.println("JPEG compression failed");
          res = ESP_FAIL;
        }
      } else {
        _jpg_buf_len = fb->len;
        _jpg_buf = fb->buf;
      }
    }
    if(res == ESP_OK){
       Serial.println("Camera capture: sending frame to UDP host");
      udp_fragmenter_sender(_jpg_buf, _jpg_buf_len, 1024, mother_ip, mother_port, transmission_number++);
    }
    if(fb){
      esp_camera_fb_return(fb);
      fb = NULL;
      _jpg_buf = NULL;
    } else if(_jpg_buf){
      free(_jpg_buf);
      _jpg_buf = NULL;
    }
    if(res != ESP_OK){
      break;
    }
  }
}

void loop() {
  
}
