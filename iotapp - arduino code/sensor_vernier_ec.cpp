#include "sensor_vernier_ec.h"

SensorVernierEc::SensorVernierEc(int ec_pin, String id, String ec_instruction_code, String ec_instruction_id) {
  ec_pin_ = ec_pin;
  id_ = id;
  ec_instruction_code_ = ec_instruction_code;
  ec_instruction_id_ = ec_instruction_id;
  ec_calibration_coefficient_ = 9000;
  ec_calibration_offset_ = 0;
  ec_decimal_points_ = 1;
}



void SensorVernierEc::begin(void) {
  
}

String SensorVernierEc::get(void) {
  // Get Sensor Data
  ec = getEc();

  // Initialize Message
  String message = "";

  // Append ec
  message += "{";
  message += "\"";
  message += "id";
  //message += "\"";
  message += "\":";
  message += "\"";
  message += id_;
  message += "\"";
  message += ",";
  message += "\"";
  message += "actionName";
  message += "\":";
  message += "\"";
  message += ec_instruction_code_;
  message += " ";
  message += ec_instruction_id_;
  message += "\":";
  message += "\"";
  message += "actionValue";
  message += "\":";
  message += String(ec,1);
  message += "}";
  message += ",";

  // Return
  return message;
}

//String SensorVernierEc::set(String instruction_code, int instruction_id, String instruction_parameter) {
//  return "";
//}

float SensorVernierEc::getEc(void) {
  return (ec_calibration_offset_ + (float)analogRead(ec_pin_)/1023*5*ec_calibration_coefficient_)/1000;
}

