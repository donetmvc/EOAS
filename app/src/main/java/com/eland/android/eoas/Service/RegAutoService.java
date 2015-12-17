package com.eland.android.eoas.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Util.ConsoleUtil;

/**
 * Created by liu.wenbin on 15/12/17.
 */
public class RegAutoService extends Service implements AMapLocationListener, ScheduleService.IScheduleListener{

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    public String TAG = "EOAS";
    public String imei = "";
    public String isAm = "";
    public String type = "";
    public float distance = 0.00f;
    public ILocationListener iLocationListener;
    private NotificationManager notificationManager;
    private Bitmap icon;
    private int regCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        initMap();
//
        if(null != intent) {
            initParams(intent);
        }

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        return super.onStartCommand(intent, flags, startId);
    }

    public void startService(String imei, String isAm, String type) {
        this.imei = imei;
        this.isAm = isAm;
        this.type = type;
        initMap();
    }

    private void initParams(Intent intent) {
        imei = intent.getStringExtra("IMEI");
        isAm = intent.getStringExtra("ISAM");
        type = intent.getStringExtra("TYPE");

        if(!type.isEmpty() && type.equals("AUTO")) {
            startService(imei, isAm, "AUTO");
        }
        else {
            startService(imei, isAm, "NOAUTO");
        }
    }

    private void initMap() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        initOption();
        mLocationClient.setLocationOption(mLocationOption);

        startLocation();
    }

    private void initOption() {
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
    }

    private void startLocation() {
        ConsoleUtil.i(TAG, "----------RegWorkInfoService:" + "Location Start");
        mLocationClient.startLocation();
    }

    public void setOnLocationLinstener(ILocationListener iLocationListener) {
        this.iLocationListener = iLocationListener;
    }

    public interface ILocationListener {
        void onLocationSuccess(float distance);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                double latitude = amapLocation.getLatitude();//获取纬度
                double longitude = amapLocation.getLongitude();//获取经度

                LatLng startLatlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                LatLng endLatlng = new LatLng(39.98023200, 116.49513900);
                LatLng endLatlng1 = new LatLng(39.97986400, 116.49460800);
                float distance1 = AMapUtils.calculateLineDistance(startLatlng, endLatlng);
                float distance2 = AMapUtils.calculateLineDistance(startLatlng, endLatlng1);

                //取最小距离，防止位置偏差
                if(distance1 < distance2) {
                    distance = distance1;
                }
                else {
                    distance = distance2;
                }

                ConsoleUtil.i(TAG, "----------RegWorkInfoService:" + distance);

                if(type.equals("AUTO")) {

                    if(distance < 300.0) {
                        ConsoleUtil.i(TAG, "----------RegWorkInfoService:" + "Location end");
                        mLocationClient.stopLocation();
                        startRegService();
                    }
                }
                else {
                    if(null != iLocationListener) {
                        iLocationListener.onLocationSuccess(distance);
                    }
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                ConsoleUtil.i(TAG, "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

            }
        }
    }

    private void startRegService() {
        ScheduleService scheduleService = new ScheduleService();
        scheduleService.setOnScheduleListener(this);
        ConsoleUtil.i(TAG, "----------RegWorkInfoService:" + "Regist start" + isAm);
        scheduleService.regScheduleAM(imei, isAm);
    }

    @Override
    public void onScheduleSuccess() {
        showNotification("S");
        stopSelf();
    }

    private void showNotification(String regType) {
        String message = "";
        Notification notification;

        if(regType.equals("S")) {
            message = "你已经成功出勤，Happy Day.";
        }
        else {
            message = regType;
        }

        if(Build.VERSION.SDK_INT >= 16) {
            notification = new NotificationCompat.Builder(getApplicationContext())
                    .setLargeIcon(icon)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("出勤通知").setContentInfo("移动OA")
                    .setContentTitle("出勤通知").setContentText(message)
                    .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                    .build();
        }
        else {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("TickerText:" + "出勤通知")
                    .setContentTitle("出勤通知")
                    .setContentText(message)
                    .setNumber(1)
                    .getNotification();
        }
        notificationManager.notify(0, notification);
    }

    @Override
    public void onScheduleFailure(int code, String msg) {

        if(regCount > 10) {
            showNotification(msg);
            stopSelf();
        }
        else {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new myBinder();
    }

    public class myBinder extends Binder {
        public RegAutoService getService() {
            return RegAutoService.this;
        }
    }
}
