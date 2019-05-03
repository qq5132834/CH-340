
--
nodemcuInit = {}

--
cfg={}
cfg.ssid="ESP8266-AP"
cfg.pwd="1234567890"

--
cfo={
    ip="192.168.4.1",
    netmask="255.255.255.0",
    geteway="192.168.4.1"
}


function nodemcuInit.init()
    --The mode of setting the module is AP + station.
    wifi.setmode(wifi.STATIONAP)
    wifi.ap.setip(cfo)
    wifi.ap.config(cfg)
end


return nodemcuInit
