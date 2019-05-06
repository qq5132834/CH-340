require("net")
require("uart")

apTcpServer={}


myclient=nil


function apTcpServer.createServer()

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
           uart.on("data",'\n', function(data)
                if(myclient~=nil) then
                    myclient:send(data)
                end
           end, 0)                
       end)

       connection:on("disconnection",function(client,data)
           myclient=nil
       end)
    end)

   

end
