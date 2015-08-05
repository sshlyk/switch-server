# Switch Server
Small server that runs on RaspberryPi and interacts with GPIOs and control external relays. It makes itself discoverable and can be operated remotely using [switch-android](https://github.com/sshlyk/switch-android) client

## How to build

```
./gradlew
```
will generate self-contained jar file in build/libs you can immediately execute.

To execute it locally (RaspberryPI GPIO interactions are mocked)

```
java -jar build/libs/switch-server-1.0.jar --flavor dev
```

To execute it on your raspberry device

```
java -jar build/libs/switch-server-1.0.jar --config /path/to/your/config.json 
```

were config.json is in following format:
```
{
  "common": {
    "port": 61235,
    "password": "helloSwitch",

    "devices": {
      "Garage Door": {
        "switchPinNumber": 0,
        "deviceType": "garage"
      },
      "House Ceiling Fan": {
        "switchPinNumber": 2,
        "deviceType": "switch"
      }
    }
  },

  "development": {
    "mockDevices": true
  }
}
```
* port - device port number to listen for incoming request
* password - to authorize incoming requests
* switchPinNumber - RaspberryPi gpio-pi4j pin number. You can find gpio to pinNumber mapping for your RapberryPi revision on [pi4j website](http://pi4j.com) 
* deviceType - device type connected to pin. Currently only supports "switch" and "garage" device types.

## RaspberryPi
### Setup
* Download [Rasbian](http://downloads.raspberrypi.org/raspbian_latest) linux distro image, and copy it to your SD card.
* Install [Pi4J](http://pi4j.com/install.html)
* Connect RaspeberryPi to your local network (wifi or hardwired)
* Copy server jar file on RaspeberryPi ```scp build/libs/switch-server-1.0.jar pi@192.168.1.5:/home/pi```
*  Run  ```sudo java -jar switch-server-1.0.jar ``` (to provide your own config file pass "--config /path/to/your/file.json" argument)
* You can start service automatically when Raspberry Pi is rebooted
```sudo crontab -e``` 
and add following line 
```@reboot java -jar /home/pi/switch-server-1.0.jar```
