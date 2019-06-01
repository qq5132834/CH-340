Mymqtt = mqtt.Client(clientid, 120,MqttUserString, MqttPwdString);

tmr.alarm(3, 1000, 1, function()
    Mymqtt:connect(MqttIPString, MqttPort, 0,ConnectSuccess,ConnectFailed)
end)

function ConnectSuccess(client)
     client:subscribe(SubscribeTopic, 0, subscribeSuccess)
     print("connected")
     mqttClient = client;
     tmr.stop(3);
     mqttConnectedFlage = 1;
end
function ConnectFailed(client,reason)
   mqttConnectedFlage = 0;
   print("failed reason: " .. reason)
   tmr.start(3)
end
function subscribeSuccess(client)
    print("subscribe success") 
end
Mymqtt:on("message", function(client, topic, data) 
    if data == "off" then
       print("I am off") 
    elseif data == "on"  then    
      print("I am on") 
    mqttClient:publish(PublishTopic,10086, 0, 0, function(client)  
    end)   
    end
end)

