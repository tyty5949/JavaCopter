#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_MS_PWMServoDriver.h"

const int MPU_addr = 0x68;

Adafruit_MotorShield AFMS = Adafruit_MotorShield();
Adafruit_DCMotor *m1 = AFMS.getMotor(1);
Adafruit_DCMotor *m2 = AFMS.getMotor(2);
Adafruit_DCMotor *m3 = AFMS.getMotor(3);
Adafruit_DCMotor *m4 = AFMS.getMotor(4);

int16_t AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;
int acX_zero = 0;
int acY_zero = 0;
int acZ_zero = 0;

int m1Pow = 0;
int m2Pow = 0;
int m3Pow = 0;
int m4Pow = 0;

char buf;
String packet = "";
bool readingBluetooth = false;

int throttleIn = 0;
int rollIn = 0;
int pitchIn = 0;
int yawIn = 0;

void setup() {
  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
  Serial.begin(9600);
  AFMS.begin();
  initMotors();

  do {
    while (Serial.available()) {
      buf = Serial.read();
      if (buf == '[' && !contains(packet, "["))  {
        readingBluetooth = true;
      }

      if (readingBluetooth) {
        packet.concat(buf);
        if (buf == ']') {
          if (!contains(packet, "[conn]")) {
            packet = "";
          } else {
            readingBluetooth = false;
            serialFlush();
          }
        }
      }
    }
    delay (10);
  } while (!contains(packet, "[conn]"));

  Serial.println("[iPLEASE DO NOT TOUCH, zeroing gyro...]");
  initGyro();

  packet = "";
  serialFlush();
  Serial.println("[iDONE]");
}

void loop() {
  // Look for new packets
  while (Serial.available()) {
    buf = Serial.read();
    if (buf == '[' && !contains(packet, "["))  {
      readingBluetooth = true;
    }
    if (readingBluetooth) {
      packet.concat(buf);
      if (buf == ']') {
        readingBluetooth = false;
        serialFlush();
      }
    }
  }

  // Decode new packets
  if (contains(packet, "[") && contains(packet, "]")) {
    // Control command packet
    if (packet.startsWith("[c")) {
      throttleIn = packet.substring(2, 5).toInt();
      rollIn = packet.substring(5, 8).toInt() - 50;
      pitchIn = packet.substring(8, 11).toInt() - 50;
      yawIn = packet.substring(11, 14).toInt() - 50;
    }
    packet = "";
    serialFlush();
  }

  // Get gyro input
  updateGyro();

  // Calculate stabilization
  stabilize();

  // Update motor speeds
  updateMotors();

  // Print data
  Serial.println("[d" + String(AcX / 180.0) + ";" + String(AcY / 180.0) + ";" + String(AcZ / 180.0) +  ";" +
                  m1Pow + ";" + m2Pow + ";" + m3Pow + ";" + m4Pow + "]");
}

void stabilize() {
  // Throttle set
  m1Pow = (int) (255.0 * ((double) throttleIn / 100.0)) + 4;
  m2Pow = (int) (255.0 * ((double) throttleIn / 100.0)) + 1;
  m3Pow = (int) (255.0 * ((double) throttleIn / 100.0)) + 1;
  m4Pow = (int) (255.0 * ((double) throttleIn / 100.0));

  // Roll target
  double gyroRoll = AcY / 180.0;
  double rollMult = (rollIn - gyroRoll) / 100.0;
  m1Pow += m1Pow * rollMult;
  m2Pow += m2Pow * -rollMult;
  m3Pow += m3Pow * -rollMult;
  m4Pow += m4Pow * rollMult;

  // Pitch target
  double gyroPitch = AcZ / 180.0;
  double pitchMult = (-pitchIn - gyroPitch) / 100.0;
  m1Pow += m1Pow * pitchMult;
  m2Pow += m2Pow * pitchMult;
  m3Pow += m3Pow * -pitchMult;
  m4Pow += m4Pow * -pitchMult;

  // Yaw target
  double yawMult = yawIn / 100.0;
  m1Pow += m1Pow * -yawMult;
  m2Pow += m2Pow * yawMult;
  m3Pow += m3Pow * -yawMult;
  m4Pow += m4Pow * yawMult;
}

void initGyro() {

  delay(3000);
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true);
  AcX = Wire.read() << 8 | Wire.read();
  AcY = Wire.read() << 8 | Wire.read();
  AcZ = Wire.read() << 8 | Wire.read();
  Tmp = Wire.read() << 8 | Wire.read();
  GyX = Wire.read() << 8 | Wire.read();
  GyY = Wire.read() << 8 | Wire.read();
  GyZ = Wire.read() << 8 | Wire.read();

  acX_zero = -AcX;
  acY_zero = -AcY;
  acZ_zero = -AcZ;
  Serial.println("[z" + String(acX_zero) + ";" + String(acY_zero) + ";" + String(acZ_zero) + "]");
}

void updateGyro() {
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true);
  AcX = Wire.read() << 8 | Wire.read();
  AcY = Wire.read() << 8 | Wire.read();
  AcZ = Wire.read() << 8 | Wire.read();
  Tmp = Wire.read() << 8 | Wire.read();
  GyX = Wire.read() << 8 | Wire.read();
  GyY = Wire.read() << 8 | Wire.read();
  GyZ = Wire.read() << 8 | Wire.read();
  AcX = AcX + acX_zero;
  AcY = AcY + acY_zero;
  AcZ = AcZ + acZ_zero;
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
  return s.indexOf(search) >= 0;
}

char t;
void serialFlush() {
  while (Serial.available() > 0) {
    t = Serial.read();
  }
}

