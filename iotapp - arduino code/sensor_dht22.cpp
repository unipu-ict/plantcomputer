//#include <iterator>
#include <DHT.h>
#include <DHT_U.h>
/** 
 *  \file sensor_dht22.cpp
 *  \brief Sensor module for air temperature and humidity.
 *  \details See sensor_dht22.h for details.
 */
#include "sensor_dht22.h"

#define DHTPIN 2
#define DHTTYPE DHT22
DHT dht(DHTPIN, DHTTYPE);


SensorDht22::SensorDht22(int pin, String temperature_id, String temperature_instruction_code, String temperature_instruction_id, String humidity_id, String humidity_instruction_code, String humidity_instruction_id){
  pin_ = pin;
  temperature_id_ = temperature_id; 
  temperature_instruction_code_ = temperature_instruction_code;
  temperature_instruction_id_ = temperature_instruction_id;
  humidity_id_ = humidity_id;
  humidity_instruction_code_ = humidity_instruction_code;
  humidity_instruction_id_ = humidity_instruction_id;
  
}

void SensorDht22::begin(void) {
  dht.begin();
  
}

String SensorDht22::get(void) {
  // Get Sensor Data
  temperature = dht.readTemperature();
  humidity = dht.readHumidity();      
  
  // Initialize Message
  String message = "";

  // Append Temperature
  message += "{";
  message += "\"";
  message += "id";
  message += "\":";
  message += "\"";
  message += temperature_id_;
  message += "\"";
  message += ",";
  message += "\"";
  message += "actionName";
  message += "\":";
  message += "\"";
  message += temperature_instruction_code_;
  message += " ";
  message += temperature_instruction_id_;
  message += "\"";
  message += ",";
  message += "\"";
  message += "actionValue";
  message += "\":";
  message += String(temperature,1);
  message += "}";
  message += ",";

  // Append Humidity
  message += "{";
  message += "\"";
  message += "id";
  message += "\":";
  message += "\"";
  message += humidity_id_;
  message += "\"";
  message += ",";
  message += "\"";
  message += "actionName";
  message += "\":";
  message += "\"";
  message += humidity_instruction_code_;
  message += " ";
  message += humidity_instruction_id_;
  message += "\"";
  message += ",";
  message += "\"";
  message += "actionValue";
  message += "\":";
  message += String(humidity,1);
  message += "}";
  return message;
}

