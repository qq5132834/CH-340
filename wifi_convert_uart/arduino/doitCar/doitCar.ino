/****************************IO引脚定义*****************************/
//电机引脚

#include <Servo.h>

#define PIN_3 3 //蜂鸣器
#define PIN_4 4 //小车避障传感器
#define PIN_5 5 //垃圾桶满传感器

#define PIN_6 6 //右电机转速
#define PIN_7 7 //右电机转向

#define PIN_8 8 //左电机转向
#define PIN_9 9 //左电机转速

#define PIN_10 10 //舵机输出
#define PIN_11 11 //人体传感器


//控制电机运动    宏定义
#define MOTOR_GO_FORWARD  {digitalWrite(PIN_8,HIGH); digitalWrite(PIN_9,LOW);  digitalWrite(PIN_6,HIGH); digitalWrite(PIN_7,LOW);  }   //车体前进                              
#define MOTOR_GO_BACK     {digitalWrite(PIN_8,LOW);  digitalWrite(PIN_9,HIGH); digitalWrite(PIN_6,LOW);  digitalWrite(PIN_7,HIGH);  }   //车体后退
#define MOTOR_GO_LEFT     {digitalWrite(PIN_8,HIGH);  digitalWrite(PIN_9,LOW); digitalWrite(PIN_6,LOW);  digitalWrite(PIN_7,LOW);   }   //车体左转
#define MOTOR_GO_RIGHT    {digitalWrite(PIN_8,LOW); digitalWrite(PIN_9,LOW);  digitalWrite(PIN_6,HIGH);  digitalWrite(PIN_7,LOW); }   //车体右转
#define MOTOR_GO_STOP     {digitalWrite(PIN_8,LOW);  digitalWrite(PIN_9,LOW);  digitalWrite(PIN_7,LOW);  digitalWrite(PIN_6,LOW);  }   //车体静止
//串口接收处理
#define MAX_PACKETSIZE 32  //串口接收缓冲区

//小车转向
enum DN
{ 
  GO_ADVANCE, 
  GO_LEFT, 
  GO_RIGHT,
  GO_BACK,
  STOP_STOP,
  DEF
}Drive_Num=DEF;

 


//小车电机控制
void CAR_Control()
{
  switch (Drive_Num) 
    {
      case GO_ADVANCE:
           MOTOR_GO_FORWARD;
           break;
      case GO_LEFT: 
           MOTOR_GO_LEFT;
           break;
      case GO_RIGHT:
           MOTOR_GO_RIGHT;
           break;
      case GO_BACK:
           MOTOR_GO_BACK;
           break;
      case STOP_STOP: 
           MOTOR_GO_STOP;
           break;
      default:break;
    }
    Drive_Num=DEF;

}

String comdata = "";
unsigned long preUARCmd = 0; //上一次指令获取时间
void UART_Control_hl()
{   
    while (Serial.available() > 0)  
    {
        comdata += char(Serial.read());
        delay(2);
    }
    if (comdata.length() > 0)
    {
        //Serial.println(comdata);        

        if(comdata.startsWith("font")){
          //向前
          //Serial.println("pingan-font");
          preUARCmd = millis();
          Drive_Num=GO_ADVANCE;
          
        }
        else if(comdata.startsWith("back")){
          //向后
          //Serial.println("pingan-back");
          preUARCmd = millis();
          Drive_Num=GO_BACK;
          
        }
        else if(comdata.startsWith("left")){
          //向左转       
          //Serial.println("pingan-left");
          preUARCmd = millis()-800;
          Drive_Num=GO_LEFT;
          
        }
        else if(comdata.startsWith("right")){
          //向右转
          //Serial.println("pingan-right");
          preUARCmd = millis()-800;
          Drive_Num=GO_RIGHT;
          
        }else if(comdata.startsWith("stop")){
          //停止 
          //Serial.println("pingan-stop");
          preUARCmd = millis();
          Drive_Num=STOP_STOP;
          
        }else if(comdata.startsWith("open")){
          //停止 
          //Serial.println("pingan-open");
          turnOpen();
        }
        comdata = "";
    }

    if(millis() - preUARCmd >= 1000)
    {     
           preUARCmd = millis();
          //Serial.println("timeOut-stop");
          Drive_Num=STOP_STOP;
    }
    
}

//IO初始化
Servo myservo ;
void IO_init()
{ 

  pinMode(PIN_3, OUTPUT);
  pinMode(PIN_4, INPUT);
  pinMode(PIN_5, INPUT);
  
  pinMode(PIN_6, OUTPUT);
  pinMode(PIN_7, OUTPUT);
  pinMode(PIN_8, OUTPUT);
  pinMode(PIN_9, OUTPUT);
  
  pinMode(PIN_11, INPUT); 
  MOTOR_GO_STOP;

  //设置舵机引脚输出
  myservo.attach(PIN_10);
}
/////////////////////////////////////////////////////////////////////////////

void setup()
{
  Serial.begin(9600);
  IO_init();

}

int turnStatus = 0;  //0表示舵机关闭，1表示舵机开始中
void turnOpen(){
   turnStatus = 1;
   myservo.write(0);
   delay(2000);
   // 0到120旋转舵机，每次延时15毫秒
   for(int i = 0; i <= 100; i++) 
   { 
     myservo.write(i); 
     delay(15);
   }
   delay(50);
   turnStatus = 0;
}


//人体传感器
void renti(){
    if(digitalRead(PIN_11)==HIGH && turnStatus == 0){
        Serial.println("#########renti#########");  
        turnOpen();
    }  
}

//小车避障
void bizhang(){
   if(digitalRead(PIN_4)==LOW){
        Serial.println("#########bizhang#########");  
        MOTOR_GO_STOP;
   }
}

//垃圾桶满传感器
void lajitong(){
   if(digitalRead(PIN_5)==LOW){
        Serial.println("#########lajitong#########");  
        digitalWrite(PIN_3,HIGH);
        delay(2000);
        digitalWrite(PIN_3,LOW);
   }
}

void loop()
{
  UART_Control_hl();
  CAR_Control();//小车控制

//  renti(); //人体传感器
//  bizhang(); //小车避障
//  lajitong(); //垃圾桶满传感器
  
}


