#include<SoftwareSerial.h>
SoftwareSerial BT(10,11); //RX,TX
void setup() {
  // put your setup code here, to run once:
Serial.begin(9600);
BT.begin(38400);
Serial.println("Enter commands");
}

void loop() {
  // put your main code here, to run repeatedly:
if(BT.available())
  Serial.write(BT.read());
if(Serial.available())
  BT.write(Serial.read());
}
