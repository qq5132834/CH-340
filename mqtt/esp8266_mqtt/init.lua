configwifissid = "604";
configwifipwd="A13322980216";
MqttUserString = "nodemcu";
MqttPwdString = "123456"; 
MqttIPString = "192.168.0.100";
MqttPort = 1883;

clientid = wifi.sta.getmac()
SubscribeTopic = "/pub"
PublishTopic = "/sub"


wifi.setmode(wifi.STATIONAP)
apcfg={}
apcfg.ssid=configwifissid
apcfg.pwd=configwifipwd
wifi.sta.config(apcfg)

ApConnectTimes=0  
tmr.alarm(0, 1000, tmr.ALARM_AUTO,function()

   if(wifi.sta.getip() == nil) then
       print("wifi_linking...\n")
   else
       print("wifi_linked\n")
       print(wifi.sta.getip())
       tmr.stop(0)
   end

   ApConnectTimes = ApConnectTimes + 1
   if(ApConnectTimes>=10) then
        print("wifi_timeout\n")
        tmr.stop(0)
   end

end)

print("dofile init.lua")

tmr.alarm(0, 3000, 0, function()
   dofile("mqtt.lua");
   print("dofile mqtt.lua")
end)

