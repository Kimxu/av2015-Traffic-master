package com.hinsty.traffic.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.hinsty.traffic.Common;
import com.hinsty.traffic.IServiceBinder;
import com.hinsty.traffic.Utils;
import com.hinsty.traffic.dao.DaoMaster;
import com.hinsty.traffic.dao.DaoSession;
import com.hinsty.traffic.service.object.AppTrafficMap;
import com.hinsty.traffic.service.object.MainActivityData;
import com.hinsty.traffic.service.object.SettingData;
import com.hinsty.traffic.service.object.TrafficData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class TrafficService extends Service {
    public IServiceBinder.Stub binder;
    public DaoSession daoSession;
    protected SharedPreferences mSP;
    public SettingData SD;
    public TrafficHelper TH;
    public TrafficDataHolder TDH;
    public TrafficNotification TN;
    protected Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        binder = new MyBinder();
        mSP = getSharedPreferences(Common.SP.FILE_NAME, MODE_MULTI_PROCESS);
        daoSession = new DaoMaster(new DaoMaster.DevOpenHelper(this, Common.DB_FILE_NAME,
                null).getWritableDatabase()).newSession();
        SD = new SettingData();
        TDH = new TrafficDataHolder(this);
        TH = new TrafficHelper(this);
        TN = new TrafficNotification(this);
        mHandler = new Handler();
        registerReceiver(mReceiver,new IntentFilter(Intent.ACTION_SHUTDOWN));
        initTask();
    }

    @Override
    public void onDestroy() {
        TH.unregister();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    void initTask() {
        Runnable runnable = new Runnable() {
            public void run() {
                SD.load(mSP);
                TDH.initBackground();
                post(new Runnable() {
                    public void run() {
                        TN.startForeground();
                        TH.registerNormalWatcher(1000);//SD.collectFrequency.value());
                        TH.registerDayWatcher();
                    }
                });
            }
        };
        runnable.run();
    }

    void updateUI() {
        Intent intent = new Intent(Common.ACTION_UI_NEED_UPDATE);
        sendBroadcast(intent);
        TN.updateTrafficNotification();
    }


    class MyBinder extends IServiceBinder.Stub {
        public TrafficData getTodayTraffic() throws RemoteException {
            try {
                return TDH.getTodayTraffic();
            } catch (InterruptedException e) {
                return new TrafficData();
            }
        }

        public AppTrafficMap getTodayAppTraffic() throws RemoteException {
            Map<String, TrafficData> map;
            try {
                map = TDH.getAppTraffic();
            } catch (InterruptedException e) {
                map = new HashMap<>();
            }
            return new AppTrafficMap(map);
        }

        @Override
        public TrafficData getMonthTraffic() throws RemoteException {
            try {
                return TDH.getMonthTraffic();
            } catch (InterruptedException e) {
                return new TrafficData();
            }
        }

        public MainActivityData getMainData() throws RemoteException {
            MainActivityData data = new MainActivityData();
            try {
                long monthTraffic = TDH.getMonthTraffic().sum();
                int dataPlan = SD.dataPlan;
                data.monthTraffic = TrafficData.formatByte(monthTraffic);
                data.todayTraffic = TrafficData.formatByte(TDH.getTodayTraffic().sum());
                data.dataPlan = SD.dataPlan;
                int year = TDH.year();
                int month = TDH.month();
                int day = TDH.day();
                int startDay = SD.startDay;
                int monthLen = Utils.getMonthDay(year,month);
                if (startDay > day) {
                    data.balanceDay = Math.min(startDay,monthLen)-day;
                } else {
                    int nextMonthLen = Utils.getMonthDay(year,month+1);
                    data.balanceDay = monthLen- day + Math.min(startDay,nextMonthLen);
                }
                if (dataPlan == 0) {
                    data.userPercent = -1;
                } else {
                    data.userPercent = (int) (1000 / dataPlan * (monthTraffic >> 20));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        public SettingData getSettingData() throws RemoteException {
            return SD;
        }

        @Override
        public void useFastCheck() throws RemoteException {
            TH.registerNormalWatcher(2000);
        }

        @Override
        public void cancelFastCheck() throws RemoteException {
            TH.registerNormalWatcher(SD.collectFrequency.value());
        }

        @Override
        public void updateSetting(SettingData data) throws RemoteException {
            if (SD.collectFrequency != data.collectFrequency) {
                TH.registerNormalWatcher(data.collectFrequency.value());
            }
            if (SD.startDay != data.startDay) {
                //todo
            }
            if (SD.dataPlan != data.dataPlan) {
                TN.updateTrafficNotification();
            }
            SD.dayAlertEnable = data.dayAlertEnable && mSP.getBoolean(Common.SP.KEY_DAY_ALERT_WORKED,false);
            SD.monthAlertEnable = data.monthAlertEnable && mSP.getBoolean(Common.SP.KEY_MONTH_ALERT_WORkED,false);
            SD.save(mSP, data);
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
                TDH.saveTrafficDataToDB();
                TDH.saveAppInfoToDB();
            }
        }
    };

    public static boolean isRun(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = manager.getRunningServices(Integer.MAX_VALUE);
        for (int i = serviceList.size() - 1; i >= 0; --i) {
            if (serviceList.get(i).service.getClassName().equals(TrafficService.class.getName())) {
                return true;
            }
        }
        return false;
    }
}
