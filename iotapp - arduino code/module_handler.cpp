/** 
 *  \file module_handler.cpp
 *  \brief Handles all module integration. 
 *  \details See module_handler.h for details.
 *  \author Jake Rye
 *  \author adopted by Dalibor Fonovic and Sinisa Sovilj
 */
#include "module_handler.h"

// Include Module Libraries
//#include "communication.h"
//#include "sensor_dfr0161_0300.h"
#include "sensor_vernier_ph.h"
//#include "sensor_vernier_ec.h"
//#include "sensor_ds18b20.h"
//#include "sensor_tsl2561.h"
#include "sensor_dht22.h"
//#include "sensor_gc0011.h"
#include "actuator_relay.h"
//#include "sensor_contact_switch.h"





// Declare Module Objects
//Communication communication;
//SensorTsl2561 sensor_tsl2561_light_intensity_default("SLIN", 1, "SLPA", 1);
//SensorDfr01610300 sensor_dfr01610300_water_ph_temperature_ec_default(A1, "SWPH", 1, 5, "SWTM", 1, A2, "SWEC", 1, 2, 22);
SensorVernierPh sensor_venier_ph_default(A0, "530", "SWPH", "1"); 
SensorVernierEc sensor_vernier_ec_default(A2, "520", "SWEC", "1"); 
//SensorDs18b20 sensor_ds18b20_water_temperature(4, "540", "SWTM", "1"); 
SensorDht22 sensor_dht22_air_temperature_humidity_default(2, "500", "SATM", "1", "510", "SAHU", "1");
//SensorGc0011 sensor_gc0011_air_co2_temperature_humidity_default(12, 11, "SACO", 1, "SATM", 2, "SAHU", 2);
//SensorContactSwitch sensor_contact_switch_general_shell_open_default(4, "SGSO", 1);
//SensorContactSwitch sensor_contact_switch_general_window_open_default(3, "SGWO", 1);
//ActuatorRelay actuator_relay_air_heater_default(6, "AAHE", 1); // AC port 4
ActuatorRelay actuator_relay_light_panel_default(5, "100", "ALPN", "1"); // AC port 2
ActuatorRelay actuator_relay_air_humidifier_default(6, "111", "AAHU", "1"); // AC port 1 temporary buzzer
ActuatorRelay actuator_relay_air_vent_default(0, "121", "AAVE", "1"); // DC relay
ActuatorRelay actuator_relay_air_circulation_default(1, "131", "AACR", "1"); // DC relay
//ActuatorRelay actuator_relay_light_chamber_illumination_default(53, "ALPN", 2); 
//ActuatorRelay actuator_relay_light_motherboard_illumination_default(52, "ALMI", 1);

void initializeModules(void) { 
  //sensor_dfr01610300_water_ph_temperature_ec_default.begin();
  sensor_venier_ph_default.begin();
  //sensor_vernier_ec_default.begin();
  //sensor_ds18b20_water_temperature.begin();
  //sensor_tsl2561_light_intensity_default.begin();
  sensor_dht22_air_temperature_humidity_default.begin();
  //sensor_gc0011_air_co2_temperature_humidity_default.begin();
  //sensor_contact_switch_general_shell_open_default.begin();
  //sensor_contact_switch_general_window_open_default.begin();
  //actuator_relay_air_heater_default.begin();
  actuator_relay_air_humidifier_default.begin();
  actuator_relay_air_vent_default.begin();
  actuator_relay_air_circulation_default.begin();
  actuator_relay_light_panel_default.begin();
  //actuator_relay_light_chamber_illumination_default.begin();
  //actuator_relay_light_motherboard_illumination_default.begin();

  // Set Default States
  //actuator_relay_air_circulation_default.set("AACR", 1, "0");
  //actuator_relay_light_motherboard_illumination_default.set("ALMI", 1, "0");
  //actuator_relay_air_vent_default.set("AAVE", 1, "0");
}

void actuatorCommand(String actionName, String actionValue) {
  // Check for Message(s) And Handle If Necessary
  //String response_message = "";
  //Serial.println("inside actuator setter");
  actuator_relay_air_humidifier_default.set(actionName, actionValue);
  actuator_relay_air_vent_default.set(actionName, actionValue);
  actuator_relay_air_circulation_default.set(actionName, actionValue);
  actuator_relay_light_panel_default.set(actionName, actionValue);
}

String updatePublishMessage() {
  // Initialize Stream Message
  String stream_message = "";
  

  // Get Stream Message
  //stream_message += sensor_dfr01610300_water_ph_temperature_ec_default.get();
  stream_message += sensor_venier_ph_default.get();
  //stream_message += sensor_vernier_ec_default.get();
  //stream_message += sensor_ds18b20_water_temperature.get();
  //stream_message += sensor_tsl2561_light_intensity_default.get();
  //stream_message += sensor_dht22_air_temperature_humidity_default.get(); // does not work on 1.0 ovaj se ne koristi
  //stream_message += sensor_dht22_air_temperature_default.get(); // does not work on 1.0
  //stream_message += sensor_dht22_air_humidity_default.get();
  //stream_message += sensor_gc0011_air_co2_temperature_humidity_default.get();
  //stream_message += sensor_contact_switch_general_shell_open_default.get();
  //stream_message += sensor_contact_switch_general_window_open_default.get();
  //stream_message += actuator_relay_air_heater_default.get();
  stream_message += actuator_relay_air_humidifier_default.get();
  stream_message += actuator_relay_air_vent_default.get();
  stream_message += actuator_relay_air_circulation_default.get();
  stream_message += actuator_relay_light_panel_default.get();
  //stream_message += actuator_relay_light_chamber_illumination_default.get();
  //stream_message += actuator_relay_light_motherboard_illumination_default.get();
  stream_message += sensor_dht22_air_temperature_humidity_default.get(); //zbog zareza na kraju

  // Return Stream Message
  
  //Serial.println(stream_message);
  return stream_message;

  // Send Stream Message to mqtt and print to serial optional
  //Serial.println(stream_message);
  //serializeJson(doc,stream_message);
  //serializeJson(doc,Serial);
  

}

//String handleIncomingMessage(void) {
  
/*
String parseIncomingMessage(String message) {
  

  // Get Instruction Data
  
      Serial.println("Message valid.");
    }
  }

  // Return Instruction Data
  
}
*/