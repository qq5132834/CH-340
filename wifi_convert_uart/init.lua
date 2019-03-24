--require("stringUtil")
require("nodemcuInit")
require("apTcpServer")
--require("socketServer")

nodemcuInit.init()
apTcpServer.createServer()

--创建一个socketServer服务器
--socketServer.createServer()

--local list = stringUtil.split("hello=world","=")
--print(list[1])
--print(list[2])

--print("\n")
--print("ESP8266 Started")

--




---将lua对象转成字符串
--Sharp = { id=1,name="huangliao",age=31} 

--ok, json = pcall(sjson.encode, Sharp)
--if ok then
--  print(json)
--else
--  print("failed to encode!")
--end

--将字符串转成lua table
--str='{"name":"huangliaossssss","id":1,"age":30}'
--t = sjson.decode(str)
--for k,v in pairs(t) do 
--    print(k,v) 
--end

--print(t.name)









