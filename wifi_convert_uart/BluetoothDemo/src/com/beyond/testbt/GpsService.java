package com.beyond.testbt;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;



/**
 * Created by Administrator on 14-1-17
 */
public class GpsService extends Service {
    private boolean myFlag;
    //private String latLongString;
    private LocationManager locationManager;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }
        public void onProviderEnabled(String provider) {
            Location location=locationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.i("song", "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i("song", "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i("song", "当前GPS状态为暂停服务状态");
                    break;
            }
        }
    };
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            boolean flag=isOPen(this);
            Log.d("song",flag+"...flag...");
            String serviceName = Context.LOCATION_SERVICE;
            locationManager = (LocationManager)getSystemService(serviceName);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String currentProvider = locationManager.getBestProvider(criteria, true);
            currentProvider="gps";
            Location lastKnownLocation = locationManager.getLastKnownLocation(currentProvider);
            if (lastKnownLocation != null) {
                Log.d("song", "LastKnownLocation: "
                        + getLocationInfo(lastKnownLocation));
            } else {
                Log.d("song", "Last Location Unkown!");
            }
            locationManager.requestLocationUpdates(currentProvider, 10*1000, 0,
                    locationListener);
        } catch (Exception e) {
            Log.d("song",e.toString());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.myFlag = false;
        Log.d("song", "GpsService onDestroy");
    }

    /**
     * 判断GPS是否开启的
     * @param context
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("song",gps+"......"+network);
        if (gps || network) {
            return true;
        }

        return false;
    }
    //通过location得到经纬度信息
    private void updateWithNewLocation(Location location) {
        String latLongString;
        Log.d("song","...latLongString...");
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = "lat:" + lat + " &lng:" + lng;
            Log.d("song",latLongString);
            //service通过广播发送信息给activity
            Intent intent=new Intent();
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.setAction("com.song.gps");
            sendBroadcast(intent);
        } else {
            latLongString = "无法获取地理信息";
            Log.d("song",latLongString);
            Intent intent=new Intent();
            intent.putExtra("lat", 00.00);
            intent.putExtra("lng", 00.00);
            intent.setAction("com.song.gps");
            sendBroadcast(intent);
        }
    }
    private String getLocationInfo(Location location) {
        String info = "";
        info += "Longitude:" + location.getLongitude();
        info += ", Latitude:" + location.getLatitude();
        if (location.hasAltitude()) {
            info += ", Altitude:" + location.getAltitude();
        }
        if (location.hasBearing()) {
            info += ", Bearing:" + location.getBearing();
        }
        return info;
    }
}
