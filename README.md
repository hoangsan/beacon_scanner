# Beacon Scanner
A beacon scanner via iBeacon protocol.

In this library. I used same class name, method name,... as Apple's iBeacon library [CLLocationManager](https://developer.apple.com/documentation/corelocation/cllocationmanager), so any Apple developer can use this one easily.
`Also I added more useful feature to hanlde monitoring interval that was fixed 30s as default in Apple's one.`

## Getting Started
iBeacon is a protocol developed by Apple and introduced at the Apple Worldwide Developers Conference in 2013. Various vendors have since made iBeacon-compatible hardware transmitters – typically called beacons – a class of Bluetooth low energy (BLE) devices that broadcast their identifier to nearby portable electronic devices. The technology enables smartphones, tablets and other devices to perform actions when in close proximity to an iBeacon.

### Installing

- Pull source to your local machine.
- Copy beacon directory to your project directory.
- Change settings.gradle
```gradle
include ':app', ':beacon'

```
- Change build.gradle
```gradle
dependencies {
  compile project(path: ':beacon')
}
```
### Feature
- Set interval for scanning exiting beacon region.
- Start/Stop monitoring the specified region.
- Start/Stop the delivery of notifications for the specified beacon region.

```java
public interface LocationManagerDelegate {
  void didRangeBeacons(List<Beacon> beacons, BeaconRegion region);
  void rangingBeaconsDidFailFor(BeaconRegion region, Error error);

  void didEnterRegion(Region region);
  void didExitRegion(Region region);
  void monitoringDidFailFor(Region region, Error error);

  void didFailWithError(Error error);
}

public class LocationManager {
  //Set interval for scanning exiting beacon region.
  public void setMonitoringInterval(int monitoringInterval)
  
  public void startMonitoring(Region region);
  public void stopMonitoring(Region region);
  public void startRangingBeacons(BeaconRegion region);
  public void stopRangingBeacons(BeaconRegion region);
}
```

### Example
```java
LocationManager lm;
lm = LocationManager.getInstance(getApplicationContext());
lm.setLocationManagerDelegate(this);
lm.setMonitoringInterval(5000);
        
UUID towerUUID = UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d");
int stage1Major = 1;
int stage2Major = 2;

int door1Stage1Minor = 1;   //bc1
int door2Stage1Minor = 2;   //bc2
int door1Stage2Minor = 1;   //bc3

BeaconRegion stage1Region = new BeaconRegion(towerUUID,stage1Major,"stage1");
BeaconRegion stage2Region = new BeaconRegion(towerUUID,stage2Major,"stage2");

BeaconRegion beaconRegion1 = new BeaconRegion(towerUUID,stage1Major,door1Stage1Minor,"beacon1");
BeaconRegion beaconRegion2 = new BeaconRegion(towerUUID,stage1Major,door2Stage1Minor,"beacon2");
BeaconRegion beaconRegion3 = new BeaconRegion(towerUUID,stage2Major,door1Stage2Minor,"beacon3");

lm.startRangingBeacons(stage1Region);
lm.startRangingBeacons(stage2Region);

lm.startMonitoring(beaconRegion1);
lm.startMonitoring(beaconRegion2);
lm.startMonitoring(beaconRegion3);
```

## Versioning

For the versions available, see the [tags on this repository](https://github.com/hoangsan/beacon_scanner/tags). 

## Authors

* **San Vo** - Github: [hoangsan](https://github.com/hoangsan)

## License

No license.
