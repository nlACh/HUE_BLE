//baud hc-05 57600
//baud hc-05 AT mode 38400
//MAC <Whatever your module's is...>

#include<SoftwareSerial.h>
#define MAX_BUFF 64
#define RED 3 //red pin
#define GREEN 5 //green pin
#define BLUE 6 //blue pin

int isSetupOnce=0;

SoftwareSerial BLE(10,11); //RX,TX

char inbyte[MAX_BUFF]; //char array to store the input
int index=0;

int r=0, g=0, b=0; //red, green and blue values, each going from 0 to 255

void setup() {
  
  Serial.begin(9600);//debug
  BLE.begin(57600); //communicating with BT module
  
  Serial.println("software serial set to 57600");
  
  pinMode(RED,OUTPUT);
  pinMode(GREEN,OUTPUT);
  pinMode(BLUE,OUTPUT);
}

void loop() {
  if(BLE.available())
  {
    char c = BLE.read();
    Serial.println("recieving...");
    if(c=='0' || index==63) //if the termination character has been passed or max length of array reached
    {
      //Serial.println("DONE!!");//debug
      inbyte[index] = '\0';
      index=0;
      processCMD(String(inbyte)); //process the string command
      //printscr();//debug
      
      clr();
    }
    else
      inbyte[index++] = c;
  }

}

//real magic happens here as the command is processed
void processCMD(String msg)
{
  /*The commands are processed depending on the first character
   * #  means a color value(R,G,B) is being sent and that is going to be
   * parsed and set via setColor() function
   * 
   * @  means a special command and the second character determines which command it is
   * 
   * x  means initiating a connection
   */
  if(msg[0] == 'x')
  {
    if(isSetupOnce==0)
    { //for the first connection, LED will turn green!
      digitalWrite(GREEN,LOW);
      digitalWrite(RED,HIGH);
      digitalWrite(BLUE,HIGH);
      isSetupOnce=1;
    }
  }
  if(msg[0] == '#')
    setColor(1,2,3);
  if(msg[0] == '@')
  {
    switch(msg[1])
    {
      case 'O': digitalWrite(RED,HIGH);//'O' stands for OFF i,e, turning off the LED
                digitalWrite(GREEN,HIGH);
                digitalWrite(BLUE,HIGH);
                break;

      case 'W': digitalWrite(RED,LOW);//'W' means LED will glow white
                digitalWrite(GREEN,LOW);
                digitalWrite(BLUE,LOW);
                break;

      case 'C': cycle();///Cycle through the whole RGB spectrum
                //function down below
    }
  }
}

void printscr()
{
  Serial.println(String(inbyte));
}

void clr()
{ /*
  this guy resets the char array inbyte 
  */
  for(int i=0; i<64; i++)
    inbyte[i]='\0';
}

void cycle()
{
  while(1)
  {//its an infinite cycle! Haven't yet figured out a meaningful
    //way to stop it when a command is entered.
    //IF YOU KNOW A WAY, TELL ME!
    setColor(255,0,0);
    setColor(255,255,0);
    setColor(255,255,255);
    setColor(255,255,0);
    setColor(255,0,0);
  }
}

/*
 * This function is used to set the color of the LED
 * Whatever color is passed, this function will smoothly transition
 * to that color. The term smooth is relative though :D
 */
void setColor(int red, int green, int blue)
{
  while(r!=red || g!=green || b!=blue)
  {
    if(r<red)r+=1;
    if(r>red)r-=1;

    if(g<green)g+=1;
    if(g<green)g-=1;

    if(b<blue)b+=1;
    if(b>blue)b-=1;

    _disp();//display those colors...
    delay(20);
  }
}

void _disp()//set the rgb values through PWM pins....AND DONE!
{
  analogWrite(RED, 255-r); //since when pin is LOW, then LED turns on...our calculated intensities
  analogWrite(GREEN, 255-g); //need to be reversed for each color. Thats why subtracted from 255
  analogWrite(BLUE, 255-b);
}

