require("net")
require("uart")
require("gpio")


apTcpServer={}



myclient=nil

-- led pin
ledPIN=4



function apTcpServer.createServer()

    gpio.mode(ledPIN, gpio.OUTPUT)    
    
    ap_server=net.createServer(net.TCP)
    ap_server:listen(1010,function(connection)

       connection:on("receive", function(client, data)
           if(myclient==nil) then
                myclient=client
           else
                
           end

           if(myclient~=nil) then
               -- print("pingan")
                uart.write(0, data)
           else
                
           end
           
                   
       end)

       connection:on("connection",function(client,data)
           myclient=client

           -- open led
           gpio.write(ledPIN,gpio.LOW)
           
           uart.on("data",'\n', function(data)
                if(myclient~=nil) then
                    myclient:send(data)
                end
           end, 0)                
       end)

       connection:on("disconnection",function(client,data)
           myclient=nil

           -- open led
           gpio.write(ledPIN,gpio.HIGH)
           
       end)
    end)

   

end
