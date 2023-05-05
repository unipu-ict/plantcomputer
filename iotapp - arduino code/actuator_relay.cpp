#include "actuator_relay.h"

//--------------------------------------------------PUBLIC-------------------------------------------//
ActuatorRelay::ActuatorRelay(int pin, String id, String instruction_code, String instruction_id) {
 pin_ = pin;
 id_ = id;
 instruction_code_ = instruction_code;
 instruction_id_ = instruction_id;
 //String naredba = instruction_code_ + String(instruction_id_);
 //Serial.println(naredba);
}

void ActuatorRelay::begin(void) {
 pinMode(pin_,OUTPUT);
 turnOff();
}

String ActuatorRelay::get(void) {
  // Initialize Message
  String message = "";

  // Append Actuator State
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
  //message += "\"";
  message += "\":";
  message += "\"";
  message += instruction_code_;
  message += " ";
  message += instruction_id_;
  message += "\"";
  message += ",";
  message += "\"";
  //message += "\":";
  message += "actionValue";
  //message += "\"";
  message += "\":";  
  message += "\"";
  //message += "\"";
  //message += "\"";

  message += value_;
  message += "\"";
  //message += ",";
  message += "}";
  message += ",";
  
  return message;
}
String ActuatorRelay::set(String actionName, String actionValue){
  String command = instruction_code_ + " " + instruction_id_;
  //Serial.print("naredba je:\t");
  //Serial.println(naredba);
  if (actionName == command) {
    //Serial.println("Got ispravna naredba");
    //if (instruction_parameter.toInt() == 1) {
    if (actionValue == "on") { //trebalo bi zamijeniti on i off na AC releju zato jer je normal closed spojeno
      Serial.println("Got actuator on command, turning relay on..");
      turnOn();
      return "";
    }
    else if(actionValue == "off") {
      turnOff();
      return "";
    }
  }
  return "";
}
/*String ActuatorRelay::set(String instruction_code, int instruction_id, String instruction_parameter) {
  if ((actionName == instruction_code_) && (actionValue == instruction_id_)) {
    //if (instruction_parameter.toInt() == 1) {
    if (instruction_parameter == "on") {
      Serial.println("Got on command");
      turnOn();
      return "";
    }
    else if(instruction_parameter == "off") {
      turnOff();
      return "";
    }
  }
  return "";
}*/

//-------------------------------------------------PRIVATE-------------------------------------------//
void ActuatorRelay::turnOn(void){
  digitalWrite(pin_, HIGH); // active low relay
  //value_ = 1;
  value_ = "on";
}

void ActuatorRelay::turnOff(void){
  digitalWrite(pin_, LOW);
  //value_ = 0;
  value_ = "off";
}
