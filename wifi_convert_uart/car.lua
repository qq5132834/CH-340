require("gpio")

car={}

function car.init()
   gpio.mode(GPIO14, gpio.OUTPUT)
   gpio.mode(GPIO12, gpio.OUTPUT)
   gpio.mode(7, gpio.OUTPUT)
   gpio.mode(8, gpio.OUTPUT)
end

function car.font()
    gpio.write(GPIO14, gpio.HIGH)
    gpio.write(GPIO12, gpio.LOW)
    print("xixi.font")
end