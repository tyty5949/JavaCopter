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

const int MPU_addr=0x68;

Adafruit_MotorShield AFMS = Adafruit_MotorShield(); 

Adafruit_DCMotor *m1 = AFMS.getMotor(1);
Adafruit_DCMotor *m2 = AFMS.getMotor(2);
Adafruit_DCMotor *m3 = AFMS.getMotor(3);
Adafruit_DCMotor *m4 = AFMS.getMotor(4);

int16_t AcX,AcY,AcZ,Tmp,GyX,GyY,GyZ;

int m1Pow = 0;
int m2Pow = 0;
int m3Pow = 0;
int m4Pow = 0;

char buf = '\0';
String packet = "";

int gyX_zero = 0;
int gyY_zero = 0;
int gyZ_zero = 0;

int throttleIn = 0;
int rollIn = 0;
int pitchIn = 0;
int yawIn = 0;

void setup() {
  Serial.begin(9600);
  AFMS.begin();
  
  do {
    while(Serial.available() > 0) {
      buf = Serial.read();
      packet.concat(buf);
    }
     delay (10);
  } while(packet != "[conn]");
  packet = "";
  
  Serial.println("[iInitializing...]");
  initMotors();

  Serial.println("[iPLEASE DO NOT TOUCH, zeroing gyro...]");
  initGyro();

  Serial.println("[iDONE]");
}

void loop() {
  // Look for new packets
  while(Serial.available() > 0) {
    buf = Serial.read();
    packet.concat(buf);
  }
  if(contains(packet, "[") && contains(packet, "]")) {
    if(packet.startsWith("[c")) { //[c111222333444]
      throttleIn = 0;
      //Serial.println("[i" + packet.substring(2, 5) + "]");;
      packet.substring(5, 8);
      packet.substring(8, 11);
      packet.substring(11, 14);
    }
    packet = "";
  }

  // Get gyro input
  updateGyro();

  // Calc motor speeds
  stabilize();
  
  // Update motor speeds
  updateMotors();
}

void stabilize() {
  // Throttle set
  m1Pow = (int) (255.0 * ((double) throttleIn / 100.0));
  m2Pow = (int) (255.0 * ((double) throttleIn / 100.0));
  m3Pow = (int) (255.0 * ((double) throttleIn / 100.0));
  m4Pow = (int) (255.0 * ((double) throttleIn / 100.0));

  // Roll target

  // Pitch target

  // Yaw target
}

void initGyro() {
  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);

  delay(1000);

  int16_t GyX_tot,GyY_tot,GyZ_tot;

  // Read 1
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr,14,true);
  GyX_tot+=Wire.read()<<8|Wire.read();
  GyY_tot+=Wire.read()<<8|Wire.read();
  GyZ_tot+=Wire.read()<<8|Wire.read();

  delay(1000);

  // Read 2
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr,14,true);
  GyX_tot+=Wire.read()<<8|Wire.read();
  GyY_tot+=Wire.read()<<8|Wire.read();
  GyZ_tot+=Wire.read()<<8|Wire.read();

  delay(1000);

  // Read 3
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr,14,true);
  GyX_tot=Wire.read()<<8|Wire.read();
  GyY_tot=Wire.read()<<8|Wire.read();
  GyZ_tot=Wire.read()<<8|Wire.read();

  gyX_zero = -GyX_tot;
  gyY_zero = -GyY_tot;
  gyZ_zero = -GyZ_tot;
  Serial.println("[z" + String(gyX_zero) + ";" + String(gyY_zero) + ";" + String(gyZ_zero) + "]");
}

void updateGyro() {
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr,14,true);
  //AcX=Wire.read()<<8|Wire.read();  
  //AcY=Wire.read()<<8|Wire.read();
  //AcZ=Wire.read()<<8|Wire.read();
  //Tmp=Wire.read()<<8|Wire.read();
  GyX=(Wire.read()<<8|Wire.read()) + gyX_zero;
  GyY=(Wire.read()<<8|Wire.read()) + gyY_zero;
  GyZ=(Wire.read()<<8|Wire.read()) + gyZ_zero;
  double x = (((double) GyX-1.0 * 180.0/3.1459)-100.0) / 180.0;
  double y = (((double) GyY-1.0 * 180.0/3.1459)-100.0) / 180.0;
  double z = (((double) GyZ-1.0 * 180.0/3.1459)-100.0) / 180.0;
  Serial.println("[d" + String(x) + ";" + String(y) + ";" + String(z) + "]");
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

void updateMotors() {
  m1->setSpeed(m1Pow);
  m2->setSpeed(m2Pow);
  m3->setSpeed(m3Pow);
  m4->setSpeed(m4Pow);
}

bool contains(String s, String search) {
    int max = s.length() - search.length();

    for (int i = 0; i <= max; i++) {
        if (s.substring(i) == search) return true;
    }

    return false;
}

