/*
 * Copyright (c) 2016 Tyler Hunt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_MS_PWMServoDriver.h"

Adafruit_MotorShield AFMS = Adafruit_MotorShield(); 

Adafruit_DCMotor *m1 = AFMS.getMotor(1);
Adafruit_DCMotor *m2 = AFMS.getMotor(2);
Adafruit_DCMotor *m3 = AFMS.getMotor(3);
Adafruit_DCMotor *m4 = AFMS.getMotor(4);

int m1Pow = 0;
int m2Pow = 0;
int m3Pow = 0;
int m4Pow = 0;

char buf = '\0';
String packet = "";

void setup() {
  Serial.begin(9600);
  AFMS.begin();

  Serial.println("Please connect bluetooth & javacopter program...");
  /*
  do {
    while(Serial.available() > 0) {
      buf = Serial.read();
      packet.concat(buf);
    }
     delay (10);
  } while(packet != "[conn]");
  packet = "";
*/
  
  Serial.println("Initializing...");
  initMotors();

  Serial.println("PLEASE DO NOT TOUCH, zeroing gyro...");
  //initGyro();

}

void loop() {
  // Look for new packets
  while(Serial.available() > 0) {
    buf = Serial.read();
    packet.concat(buf);
  }
  /*
  if(contains(packet, "[") && contains(packet, "]")) {
    if(packet.startsWith("[c")) { //[c111222333444]
      packet.substring(2, 5);
      packet.substring(5, 8);
      packet.substring(8, 11);
      packet.substring(11, 14);
    }
    packet = "";
  }
  */

  // Get gyro input
  updateGyro();

  // Calc motor speeds
  
  // Update motor speeds
  m1->setSpeed(m1Pow);
  m2->setSpeed(m1Pow);
  m3->setSpeed(m1Pow);
  m4->setSpeed(m1Pow);

  Serial.println("Can you see me mac?");
  delay(100);
}

void updateGyro() {
  
}

void initMotors() {
  m1->setSpeed(0);
  m2->setSpeed(0);
  m3->setSpeed(0);
  m4->setSpeed(0);
  
  m1->run(FORWARD);
  m2->run(FORWARD);
  m3->run(FORWARD);
  m4->run(FORWARD);
}

bool Contains(String s, String search) {
    int max = s.length() - search.length();

    for (int i = 0; i <= max; i++) {
        if (s.substring(i) == search) return true;
    }

    return false;
}

