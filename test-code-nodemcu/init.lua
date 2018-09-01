require("stringUtil")
require("nodemcuInit")
require("socketServer")

--初始化nodemcu模块
nodemcuInit.init()
--创建一个socketServer服务器
socketServer.createServer()

--local list = stringUtil.split("hello=world","=")
--print(list[1])
--print(list[2])

print("\n")
print("ESP8266 Started")


    





