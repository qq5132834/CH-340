-----字符串分割方法
function Split(szFullString, szSeparator)  
    local nFindStartIndex = 1  
    local nSplitIndex = 1  
    local nSplitArray = {}  
    while true do  
       local nFindLastIndex = string.find(szFullString, szSeparator, nFindStartIndex)  
       if not nFindLastIndex then  
        nSplitArray[nSplitIndex] = string.sub(szFullString, nFindStartIndex, string.len(szFullString))  
        break  
       end  
       nSplitArray[nSplitIndex] = string.sub(szFullString, nFindStartIndex, nFindLastIndex - 1)  
       nFindStartIndex = nFindLastIndex + string.len(szSeparator)  
       nSplitIndex = nSplitIndex + 1  
    end  
    return nSplitArray  
end 

 


-----根据上次AP热点配置自动连接AP热点

--wifi.sta.getconfig()
--autoConnectAPNumber=0
--tmr.alarm(0, 1000, tmr.ALARM_AUTO,function()
--   autoConnectAPNumber = autoConnectAPNumber + 1
--   if(autoConnectAPNumber>=5) then
--        tmr.stop(0)
--   end
--   if(wifi.sta.getip() == nil) then
--        print("ap-wait")
--   else  
--        tmr.stop(0)
--        print(wifi.sta.getmac())
--        print(wifi.sta.getip())                                        
--   end
--end)



-----从新选择AP的ssid、pwd接入
function connectAP(c,ssid,pwd)
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

--连接P2P服务器
P2P_CLIENT_NAME="nodemcu000001" --nodemcu客户端名称
function connectP2P(c, p2pIp, p2pPort)
    --print(p2pIp)
    --print(p2pPort)
    p2pServerPort=p2pPort
    p2pServerAddress=p2pIp
    
    cl = net.createConnection(net.TCP,0)
    cl:connect(p2pServerPort,p2pServerAddress)
    cl:on("receive", function(sck, data)
        if(string.sub(data,1,3)=="P2P") then
            startIndex,endIndex = string.find(data, ":", 1)         
            strLen=string.len(data)
            print(string.sub(data,startIndex+1,strLen))
        end            
        --print(data)
    end) 
    cl:on("connection",function(sck,data)
        --连接成功，则用来想P2P服务器进行注册
        sck:send("nodemcu000001==========android000001==========1==========0\n")
        --print("p2p-connected")
        if( c ~= nil ) then
            c:send("p2p-connected\n")
        end           
    end)
    cl:on("disconnection",function(sck,data)
        --print("p2p-disconnected")
        if( c ~= nil ) then
            c:send("p2p-disconnected\n")
        end 
                
    end)
end

--支持ap热点、和station两种wifi模式
wifi.setmode(wifi.STATIONAP)

cfg={}
cfg.ssid="ESP8266-AP"
cfg.pwd="1234567890"

cfo={
    ip="192.168.4.1",
    netmask="255.255.255.0",
    geteway="192.168.4.1"
}

wifi.ap.setip(cfo)
wifi.ap.config(cfg)

sv=net.createServer(net.TCP, 30)  -- 30s 超时
-- server侦听端口80，如果收到数据将数据打印至控制台，并向远端发送‘hello world’
sv:listen(80,function(c)
  c:on("receive", function(c, data)
        --print(data) 
        local list = Split(data, "=")

        if(list[1]=="AP") then
           --连接路由器
           -- print(list[1])
           -- print(list[2])
           -- print(list[3])
            ssid=list[2] --路由器连接账号
            pwd=list[3]  --路由器连接密码

            connectAP(c,ssid,pwd)
            
        elseif(list[1]=="P2P") then
           --P2P连接
           -- print(list[1])
           -- print(list[2])
           -- print(list[3])
          --  print(list[4])

            nodemcu=list[2] --NodeMCU账号id
            p2pIp=list[3]   --P2P服务器地址
            p2pPort=list[4] --P2P服务器端口
            
            connectP2P(c, p2pIp, p2pPort)
            
        elseif(list[1]=="ST0") then  --AP直接向串口发送数据
            print(list[2]) 
            c:send("OK\n")         
        elseif(list[1]=="ST1") then  --获取路由器的连接状态
            sta_config=wifi.sta.getconfig(true)
            --print(sta_config.ssid)
            --print(sta_config.pwd)
            c:send(sta_config.ssid.."/"..wifi.sta.status().."\n")
        elseif(list[1]=="ST2") then --查询可选取周边可用的路由器账号，wifi搜索
            
            wifi.sta.getap(function(table)
                  apstr=""
                  for k,v in pairs(table) do
                    apstr=apstr..k..";"
                  end
                  c:send(apstr)                
            end)
             --获取连接的AP信息
        elseif(list[1]=="ST3") then
            c:send(wifi.sta.getip().."\n") --获取连接路由器之后的IP地址
        elseif(list[1]=="ST4") then        --返回nodemcu名称 
            c:send(P2P_CLIENT_NAME.."\n")
        else
            print(data)
        
        end
            
        
        
--        if(data=="left") then
--            c:send("left")
--            print("left")
--        elseif(data=="right") then
--            c:send(data)
--            print(data)
--        end
        
    end)
    --连接监听连接成功
    c:on("connection",function(c,data)
        if(c~=nil) then
            c:send("esp-connected\n")
        end
        
    end)
    c:on("disconnection",function(c,data)
        print("disconnect")
    end)
  
  end)

--print("esp8266 End")
