#include <Arduino.h>
#include <WiFi.h>
#include <AsyncTCP.h>
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

IPAddress mother_ip(192, 168, 1, 4);
uint16_t mother_port = 9876;
const IPAddress broadcastIP(192, 168, 255, 255);

//const String wifi_ssid = "ASUS";
//const String wifi_password = "PlackiSaSuper2000";

const String wifi_ssid = "NETIASPOT-2.4GHz-723300";
const String wifi_password = "YDHrnXx3b96d";


WiFiUDP udp;
AsyncServer serverTCP(5665);
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

    uint8_t buffer[64];

    
    uint32_t transmission_len = (uint32_t)len;
    uint16_t payload_len = (uint16_t)to_send;

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

    udp.availableForWrite();

    udp.beginPacket(ip, port);
    udp.write(buffer, 64);
    udp.write(data+sent, to_send);
    udp.endPacket();
    //udp.flush();

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
    config.frame_size = FRAMESIZE_HD;
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
  

  serverTCP.begin();
  serverTCP.onClient(
    [](void *arg, AsyncClient *client)
    {
    Serial.println("New client");
    client->onData(
      [](void *arg,
        AsyncClient *client,
        void *data,
        size_t len)
      {
        Serial.printf("Data received: %s\n", (char *)data);

        if(len >=3  && *(uint8_t*)(data + 0x00) == 0x21)
        {
          mother_ip = client->remoteIP();

          uint16_t tmp_port = *(uint16_t*)(data + 0x01);

          mother_port = (uint16_t)ntohs(tmp_port);

          Serial.println("Got new UDP host: ");
          Serial.printf("Mother IP: %s\n", mother_ip.toString().c_str());
          Serial.printf("Mother port: %u\n", mother_port);
        }
        


      }
      );
    }, NULL);

  udp.begin(1234);

  while(true)
  { 
    Serial.println("Camera capture: delay 6000");
    delay(2000);

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
