
/** 
 *  \file sensor_dht22.h
 *  \brief Sensor module for air temperature and humidity.
 */


#ifndef SensorDht22_H
#define SensorDht22_H
#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "module_handler.h"


/*
 *  \brief Sensor module for air temperature and humidity.
 */

class SensorDht22 {
  public:
    // Public Functions
    SensorDht22(int pin, String temperature_id, String temperature_instruction_code, String temperature_instruction_id, String humidity_id, String humidity_instruction_code, String humidity_instruction_id);
    void begin(void);
    String get(void);
    
    // Public Variables
    float humidity;
    float temperature;
    
  private:
      
    // Private Variables
    int pin_;
    String temperature_id_;
    String humidity_id_;
    String humidity_instruction_code_;
    String humidity_instruction_id_;
    String temperature_instruction_code_;
    String temperature_instruction_id_;
    
        
};

#endif // SensorDht22_H_
