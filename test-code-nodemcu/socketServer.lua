require("net")
--create socket server
socketServer={}

--set port number is 9996
port=9996

--the function is create socketServer
function socketServer.createServer()

    server=net.createServer(net.TCP, 30)

    --listner 9996端口
    server:listen(port,function(client)
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
 
end

-- 处理sockeServer接收到的数据
function handleReceiveData(client,data)
    print(data)
end

return socketServer
