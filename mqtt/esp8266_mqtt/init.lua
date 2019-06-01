configwifissid = "604";
configwifipwd="A13322980216";
MqttUserString = "nodemcu";
MqttPwdString = "123456"; 
MqttIPString = "192.168.0.112";
MqttPort = 1883;

clientid = wifi.sta.getmac()
SubscribeTopic = "/pub"
PublishTopic = "/sub"


wifi.setmode(wifi.STATIONAP)
apcfg={}
apcfg.ssid=configwifissid
apcfg.pwd=configwifipwd
wifi.sta.config(apcfg)
print("dofile init.lua")

tmr.alarm(0, 3000, 0, function()
   dofile("mqtt.lua");
   print("dofile mqtt.lua")
end)

