#include "sensor_vernier_ph.h"


SensorVernierPh::SensorVernierPh(int ph_pin, String id, String ph_instruction_code, String ph_instruction_id) {
  ph_pin_ = ph_pin;
  id_ = id;
  ph_instruction_code_ = ph_instruction_code;
  ph_instruction_id_ = ph_instruction_id;
  ph_calibration_coefficient_ = -3.838;
  ph_calibration_offset_ = 13.720;
  ph_decimal_points_ = 1;
}



void SensorVernierPh::begin(void) {
  
}

String SensorVernierPh::get(void) {
  // Get Sensor Data
  ph = getPh();

  // Initialize Message
  String message = "";

  // Append Ph
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
  message += ph_instruction_code_;
  message += " ";
  message += ph_instruction_id_;
  message += "\",";
  message += "\"";
  message += "actionValue";
  message += "\":";
  message += String(ph,1);
  message += "}";
  message += ",";

  // Return
  return message;
}

//String SensorVernierPh::set(String instruction_code, int instruction_id, String instruction_parameter) {
//  return "";
//}

float SensorVernierPh::getPh(void) {
  return ph_calibration_offset_ + (float)analogRead(ph_pin_)/1023*5*ph_calibration_coefficient_;
}

