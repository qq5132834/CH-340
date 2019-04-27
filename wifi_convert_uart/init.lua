--require("stringUtil")
require("nodemcuInit")
require("apTcpServer")
--require("httpServer")
--require("socketServer")

nodemcuInit.init()
apTcpServer.createServer()




