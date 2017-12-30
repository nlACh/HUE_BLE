//baud hc-05 57600
//baud hc-05 AT mode 38400
//MAC <Whatever your module's is>

#include<SoftwareSerial.h>
#define MAX_BUFF 64

SoftwareSerial BLE(10,11); //RX,TX

char inbyte[MAX_BUFF];
int index=0;

void setup() {
  Serial.begin(9600);
  BLE.begin(57600);
  Serial.println("software serial set to 57600");
  pinMode(13,OUTPUT);
  digitalWrite(13,LOW);
}

void loop() {
  if(BLE.available())
  {
    char c = BLE.read();
    Serial.println("recieving...");
    if(c=='0' || index==63)
    {
      Serial.println("DONE!!");
      inbyte[index] = '\0';
      index=0;
      printscr();
      clr();
    }
    else
      inbyte[index++] = c;
  }

}

void printscr()
{
  Serial.println(String(inbyte));
}
void clr()
{
  for(int i=0; i<64; i++)
    inbyte[i]='\0';
}

