require("httpServer")
-------------
-- define
-------------
IO_LED = 1
IO_LED_AP = 2
IO_BTN_CFG = 3
IO_BLINK = 4

TMR_WIFI = 4
TMR_BLINK = 5
TMR_BTN = 6

gpio.mode(IO_LED, gpio.OUTPUT)
gpio.mode(IO_LED_AP, gpio.OUTPUT)
gpio.mode(IO_BTN_CFG, gpio.INT)
gpio.mode(IO_BLINK, gpio.OUTPUT)

gpio.write(IO_LED, gpio.LOW)
gpio.write(IO_LED_AP, gpio.LOW)
gpio.write(IO_BLINK, gpio.LOW)

-------------

cfg={}
cfg.ssid="ESP8266-AP"
cfg.pwd="1234567890"

--
cfo={
    ip="192.168.4.1",
    netmask="255.255.255.0",
    geteway="192.168.4.1"
}

wifi.setmode(wifi.STATIONAP)
wifi.ap.setip(cfo)
wifi.ap.config(cfg)

httpServer:listen(80)


httpServer:use('/createClient', function(req,res)
        
    
end)

-- get router info
httpServer:use( '/getRouterInfo',function(req,res)
    print(wifi.sta.getconfig())
    ssid, pwd, autoConnect, mac=wifi.sta.getconfig()
    ipAddress, netmask, gateway = wifi.sta.getip()
    if(ipAddress==nil) then
        ipAddress = ""
    end

    if(netmask==nil) then
        netmask = ""
    end

    if(gateway==nil) then
        gateway = ""
    end
    
-- 0 wifi.STA_IDLE
-- 1 wifi.STA_CONNECTING
-- 2 wifi.STA_WRONGPWD
-- 3 wifi.STA_APNOTFOUND
-- 4 wifi.STA_FAIL
-- 5 wifi.STA_GOTIP
    status = wifi.sta.status()
    
    res:type('application/json')
    res:send('{'..
            '"status":"' .. status .. '",'..
            '"ssid":"'.. ssid ..'",'..
            '"pwd":"'.. pwd ..'",'..
            '"autoConnect":"'.. autoConnect ..'",'..
            '"mac":"'.. mac ..'",'..
            '"ipAddress":"'.. ipAddress ..'",'..
            '"netmask":"'.. netmask ..'",'..
            '"gateway":"'.. gateway ..'"' ..
            '}')
end) 



-------------
-- http link internet wifi
-------------
wifi.sta.autoconnect(0)
httpServer:use('/config', function(req, res)

	if req.query.ssid ~= nil and req.query.pwd ~= nil then

--        print(req.query.ssid)
--        print(req.query.pwd)

        cfg={}
        cfg.ssid=req.query.ssid
        cfg.pwd=req.query.pwd
        wifi.sta.config(cfg)
        ApConnectTimes=0  
        tmr.alarm(0, 1000, tmr.ALARM_AUTO,function()
       
           if(wifi.sta.getip() == nil) then
--                print("ap-wait\n")
           else
--                print("ap-success\n")
--                print(wifi.sta.getip())
                status = 'STA_GOTIP'
                res:type('application/json')
                res:send('{"status":"' .. status .. '"}')
                gpio.write(IO_LED_AP, gpio.HIGH)
                tmr.stop(0)
           end

           ApConnectTimes = ApConnectTimes + 1
           if(ApConnectTimes>=10) then
--                print("ap-failure\n")
                status = 'STA_WRONGPWD'
                res:type('application/json')
                res:send('{"status":"' .. status .. '"}')
                tmr.stop(0)
           end
       
    end)

	end
end)

httpServer:use('/scanap', function(req, res)
	wifi.sta.getap(function(table)
		local aptable = {}
		for ssid,v in pairs(table) do
			local authmode, rssi, bssid, channel = string.match(v, "([^,]+),([^,]+),([^,]+),([^,]+)")
			aptable[ssid] = {
				authmode = authmode,
				rssi = rssi,
				bssid = bssid,
				channel = channel
			}
		end
		res:type('application/json')
		res:send(cjson.encode(aptable))
	end)
end)
