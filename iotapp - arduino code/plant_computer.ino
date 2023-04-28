#include <ArduinoJson.h>
#include <ArduinoJson.hpp>






#include <ArduinoMqttClient.h>
#if defined(ARDUINO_SAMD_MKRWIFI1010) || defined(ARDUINO_SAMD_NANO_33_IOT) || defined(ARDUINO_AVR_UNO_WIFI_REV2)
  #include <WiFiNINA.h>
#elif defined(ARDUINO_SAMD_MKR1000)
  #include <WiFi101.h>
#elif defined(ARDUINO_ESP8266_ESP12)
  #include <ESP8266WiFi.h>
#endif


#include "module_handler.h"

#include "arduino_secrets.h"
///////please enter your sensitive data in the Secret tab/arduino_secrets.h
char ssid[] = SECRET_SSID;        // your network SSID (name)
char pass[] = SECRET_PASS;    // your network password (use for WPA, or use as key for WEP)

// To connect with SSL/TLS:
// 1) Change WiFiClient to WiFiSSLClient.
// 2) Change port value from 1883 to 8883.
// 3) Change broker value to a server with a known SSL/TLS root certificate 
//    flashed in the WiFi module.

WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

const char broker[] = "";
int        port     = 1883;
//const char topic[]  = "plantComputerCommand";
const char outTopic[] = "plantComputerState";
const char c4sensors[] = "plantComputerCommand4Sensors";
const char c4actuators[] = "plantComputerCommand4Actuators";


//String mqtt_out_message;
String incoming_message;

DynamicJsonDocument doc(2048);
//DynamicJsonDocument out(1024);

void setup() { // runs once
  Serial.begin(9600);
  // attempt to connect to Wifi network:
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }

  Serial.println("You're connected to the network");
  Serial.println();

  initializeModules();

  Serial.print("Attempting to connect to the MQTT broker: ");
  Serial.println(broker);

  if (!mqttClient.connect(broker, port)) {
    Serial.print("MQTT connection failed! Error code = ");
    Serial.println(mqttClient.connectError());

    while (1);
  }

  Serial.println("You're connected to the MQTT broker!");
  Serial.println();

  // set the message receive callback
  mqttClient.onMessage(onMqttMessage);

  Serial.print("Subscribing to topic: ");
  Serial.println(c4actuators);
  //Serial.println(c4sensors);
  Serial.println();

  // subscribe to a topics
  mqttClient.subscribe(c4actuators);
  //mqttClient.subscribe(c4sensors);

  // topics can be unsubscribed using:
  // mqttClient.unsubscribe(topic);

  Serial.print("Waiting for messages on topic: ");
  Serial.println(c4actuators);
  //Serial.println(c4sensors);
  
  Serial.println();

}
  
void loop() { 
  mqttClient.poll();
  String msg = "";
    
  msg = "[" + updatePublishMessage() + "]"; //read state of sensors and actuators and return as array 
  Serial.println("Published message:");
  Serial.println(msg);
  mqttClient.beginMessage(outTopic,true); //retain publish message flag
  mqttClient.print(msg);
  mqttClient.endMessage();
  delay(5000);
  
}

void parseincomingJson(){
  
  //deserializeJson(doc, mqttClient);
  JsonArray array = doc.as<JsonArray>();
  serializeJsonPretty(array, Serial);
  for (JsonObject item : array) {
  
    const char* id = item["id"]; // "505", "515", "525", "535", "545", "555"
    const char* actionName = item["actionName"]; // "SATM 1", "SAHU 1", "SWEC 1", "SWPH 1", "SWTM 1", "SLIN ...
    const char* actionValue = item["actionValue"]; // "42.06020251068512", "55.770483279890016", ...
    String s1 = String(actionName);
    String s2 = String(actionValue);
    actuatorCommand(s1,s2);
   
    
  }
  
}

void onMqttMessage(int messageSize) {
  // we received a message, print out the topic and contents
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");
  deserializeJson(doc, mqttClient);
  parseincomingJson();
}
