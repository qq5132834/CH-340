require("net")
--create socket server
socketServer={}

--set socketPort number is 9996
socketServerPort=9996
p2pNodemcuClient=nil

--the function is create socketServer
function socketServer.createServer()

    print("createSocketServer")
    server=net.createServer(net.TCP, 30)

    --listner 9996端口
    server:listen(socketServerPort,function(client)
       client:on("receive", function(client, data)
           handleReceiveData(client,data)
       end)
       client:on("connection",function(client,data)
           print("socketServerConnected")                
       end)
       client:on("disconnection",function(client,data)
           print("socketServerDisconnected")
       end)
    end)

    autoLinkWifi()
   
 
end

-- 处理sockeServer接收到的数据
function handleReceiveData(socketClient,data)
    jsonData = sjson.decode(data)
    
    if(jsonData.command==1) then
        --连接wifi命令
        ssid = jsonData.ssid
        pwd = jsonData.pwd
        print(ssid,pwd)
        linkWifi(socketClient,ssid,pwd)
        
    elseif(jsonData.command==2) then
        --连接P2P服务器
        
        
    end
end

--自动连接wifi
function autoLinkWifi()
    print("autoLinkWifi")
    wifi.sta.getconfig()
    autoConnectAPNumber=0
    tmr.alarm(0, 1000, tmr.ALARM_AUTO,function()
       autoConnectAPNumber = autoConnectAPNumber + 1
       if(autoConnectAPNumber>=5) then
            tmr.stop(0)
       end
       if(wifi.sta.getip() == nil) then
            print("ap-wait")
       else  
            tmr.stop(0)
            print("ap-success")
            print(wifi.sta.getmac())
            print(wifi.sta.getip()) 

            autoLinkP2PServer()                                                        
       end
    end)
end

--连接wifi
function linkWifi(c,ssid,pwd)
    --print(ssid.."/"..pwd)
    
    cfg={}
    cfg.ssid=ssid
    cfg.pwd=pwd
    wifi.sta.config(cfg)
    ApConnectTimes=0  
    tmr.alarm(0, 1000, tmr.ALARM_AUTO,function()
       
       if(wifi.sta.getip() == nil) then
            --print("ap-wait")
            c:send("ap-wait\n")
       else
            c:send("ap-success\n")
            tmr.stop(0)
            --print(wifi.sta.getmac())
            --print(wifi.sta.getip())                     
       end

       ApConnectTimes = ApConnectTimes + 1
       if(ApConnectTimes>=10) then
            c:send("ap-failure\n")
            tmr.stop(0)
       end
       
    end)   
end


--自动连接P2P服务器
function autoLinkP2PServer()
    serverIP = "192.168.0.115"
    port = 8090
    if(p2pNodemcuClient == nil) then

        p2pNodemcuClient = net.createConnection(net.TCP,0)
        p2pNodemcuClient:connect(port,serverIP)
        p2pNodemcuClient:on("receive", function(sck, data)          
            jsonData=sjson.decode(data)

            command=jsonData.command
            if(command==3) then
                msgFrom=jsonData.msgFrom
                msgTo=jsonData.msgTo
                msg=jsonData.msg
                print(msg)
                
            end
            

        end) 
        p2pNodemcuClient:on("connection",function(sck,data)
            --register='{"command":2,"msgFrom":"nodemcu000001","port":8090,"serverIP":"127.0.0.1"}'
            sck:send('{"command":2,"msgFrom":"nodemcu000001","port":8090,"serverIP":"127.0.0.1"}\n')
            print("p2p-connected")              
        end)
        p2pNodemcuClient:on("disconnection",function(sck,data)
            print("p2p-disconnected")
            p2pNodemcuClient=nil                    
        end)  
        
    end
   
end
 
--连接P2P服务器
function socketServer.linkP2PServer(socketClient,jsonData)

    serverIP = "192.168.0.115"
    port = 9997
    msgTo = ""
    msgFrom = "nodemcu000001"
    msg = ""
        
    
    p2pNodemcuClient = net.createConnection(net.TCP,0)
    p2pNodemcuClient:connect(port,serverIP)
    p2pNodemcuClient:on("receive", function(sck, data)          
        print(data)
    end) 
    p2pNodemcuClient:on("connection",function(sck,data)
        --连接成功，则用来想P2P服务器进行注册
        sck:send("nodemcu000001==========android000001==========1==========0\n")
        --print("p2p-connected")
        if( socketClient ~= nil ) then
            socketClient:send("p2p-connected\n")
        end           
    end)
    p2pNodemcuClient:on("disconnection",function(sck,data)
        --print("p2p-disconnected")
        if( socketClient ~= nil ) then
            socketClient:send("p2p-disconnected\n")
        end 
                
    end)
    
    
end


return socketServer
