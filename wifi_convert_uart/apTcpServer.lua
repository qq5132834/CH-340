require("net")
require("uart")

apTcpServer={}


myclient=nil


function apTcpServer.createServer()

--    print("createSocketServer")
    ap_server=net.createServer(net.TCP)
    ap_server:listen(1010,function(connection)

       connection:on("receive", function(client, data)
           if(myclient==nil) then
                myclient=client
           else
                --print("huangliao.")
           end
           print(data)
          
           --client:send("hi,i'm from esp8266.\n")
       end)

       connection:on("connection",function(client,data)
           print("clientConnected")
           myclient=client 

           --uart.setup(0, 115200, 8, uart.PARITY_NONE, uart.STOPBITS_1, 0)
           uart.on("data",'\n', function(data)
                if(myclient~=nil) then
                    myclient:send(data)
                end
           end, 0)
                  
       end)

       connection:on("disconnection",function(client,data)
           print("clientDisconnected")
           myclient=nil
       end)
    end)

   

end
