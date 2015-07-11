package com.hinsty.traffic.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.SystemClock;

import com.hinsty.traffic.Common;
import com.hinsty.traffic.service.object.TrafficData;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class TrafficHelper {
    ConnectivityManager mCM;
    TrafficService mService;
    TrafficDataHolder mTrafficData;
    PendingIntent mCheckPendingIntent;
    PendingIntent mTwelvePendingIntent;
    CheckReceiver mReceiver = new CheckReceiver();
    AlarmManager mAM;
    static final String ACTION_CHECK = "com.hinsty.traffic.action.check";
    static final String ACTION_TWELVE_O_CLOCK = "com.hinsty.traffic.action.twelve.o'clock";
    int mDay;
    int mCheckTime = SAVE_LOOP;
    static final int SAVE_LOOP = 5;//检查多少次就保存一次数据
    TrafficData mobileTraffic = new TrafficData();
    Boolean mLastMobileStatue;

    public TrafficHelper(TrafficService service) {
        mCM = (ConnectivityManager) service.getSystemService(service.CONNECTIVITY_SERVICE);
        mService = service;
        mAM = (AlarmManager) service.getSystemService(Context.ALARM_SERVICE);
        mTrafficData = service.TDH;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHECK);
        filter.addAction(ACTION_TWELVE_O_CLOCK);
        mService.registerReceiver(mReceiver, filter);
        mTrafficData.monthTraffic.loadFromSP(mService.mSP, Common.SP.KEY_SUB);
        mLastMobileStatue = isMobileConnect();
    }

    public void registerDayWatcher() {
        if (mTwelvePendingIntent != null) {
            mAM.cancel(mTwelvePendingIntent);
        }
        mDay = mTrafficData.day();
        mTwelvePendingIntent = PendingIntent.getBroadcast(mService, 0,
                new Intent(ACTION_TWELVE_O_CLOCK), 0);
        Calendar calendar = new GregorianCalendar(mTrafficData.year(), mTrafficData.month(),
                mTrafficData.day() + 1);
        mAM.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mTwelvePendingIntent);
    }

    public void registerNormalWatcher(int time) {
        if (mCheckPendingIntent != null) {
            mAM.cancel(mCheckPendingIntent);
        }
        mCheckPendingIntent = PendingIntent.getBroadcast(mService, 0, new Intent(ACTION_CHECK), 0);
        mAM.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 3000,
                time, mCheckPendingIntent);
    }

    public void unregister() {
        if (mCheckPendingIntent != null) {
            mAM.cancel(mCheckPendingIntent);
            mCheckPendingIntent = null;
        }
        if (mReceiver != null) {
            mService.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mTwelvePendingIntent != null) {
            mAM.cancel(mTwelvePendingIntent);
        }
    }

    public boolean isMobileConnect() {
        return mCM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo
                .State.CONNECTED;
    }

    protected void check() {
        boolean connect = isMobileConnect();
        if (mLastMobileStatue == null) mLastMobileStatue = connect;
        if (connect != mLastMobileStatue) {
            if (connect) {
                mobileTraffic.set(TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes());
                mTrafficData.subTraffic.set(mobileTraffic);
                mTrafficData.initAppMobileTraffic();
            } else {
                mTrafficData.saveTrafficDataToDB();
            }
        } else {
            if (connect) {
                mobileTraffic.set(TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes());
                if (mobileTraffic.sum() != 0) {
                    mTrafficData.currentTraffic.set(mobileTraffic).sub(mTrafficData.subTraffic);
                    mService.updateUI();
                    alert();
                    mTrafficData.saveTrafficDataToDB();
                }
            }
        }
        mLastMobileStatue = connect;
    }

    protected void alert() {
        try {
            if (!mTrafficData.isDayAlertWorked && mService.SD.dayAlert != 0 && mTrafficData
                    .getTodayTraffic().sum() > ((long) mService.SD.dayAlert) << 20) {
                mService.TN.showAlertNotification(TrafficNotification.AlertType.DAY,
                        stopNetConnect());
                mTrafficData.isDayAlertWorked = true;
                mService.mSP.edit().putBoolean(Common.SP.KEY_DAY_ALERT_WORKED, true);
            }
            long monthTraffic = mTrafficData.getMonthTraffic().sum();
            if (!mTrafficData.isMonthAlertWorked && mService.SD.monthAlert != 0 && monthTraffic >
                    ((long) mService.SD.monthAlert) << 20) {
                mService.TN.showAlertNotification(TrafficNotification.AlertType.MONTH,
                        stopNetConnect());
                mTrafficData.isMonthAlertWorked = true;
                mService.mSP.edit().putBoolean(Common.SP.KEY_MONTH_ALERT_WORkED, true);
            }
            if (!mTrafficData.isDataPlanAlertWorked && mService.SD.monthAlert != 0 &&
                    monthTraffic > ((long) mService.SD.dataPlan) << 20) {
                mService.TN.showAlertNotification(TrafficNotification.AlertType.DATA_PLAN,
                        stopNetConnect());
                mTrafficData.isDataPlanAlertWorked = true;
                mService.mSP.edit().putBoolean(Common.SP.KEY_DATA_PLAN_ALERT_WORKED, true);
            }
        } catch (InterruptedException e) {
            //do nothing
        }
    }

    protected void twelveOClock() {
        check();
        //getAppTraffic();
        mTrafficData.saveTrafficDataToDB();
        //mTrafficData.saveAppInfoToDB();
        mTrafficData.newDay();
        if (mDay != mTrafficData.day()) {
            registerDayWatcher();
        }
    }

    class CheckReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_CHECK:
                    check();
                    break;
                case ACTION_TWELVE_O_CLOCK:
                    twelveOClock();
                    break;
            }
        }
    }

    protected boolean stopNetConnect() {
        return false;
    }
}
