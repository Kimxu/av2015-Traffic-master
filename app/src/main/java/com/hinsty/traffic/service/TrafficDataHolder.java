package com.hinsty.traffic.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.SystemClock;

import com.hinsty.traffic.Common;
import com.hinsty.traffic.Utils;
import com.hinsty.traffic.dao.AppDayTraffic;
import com.hinsty.traffic.dao.AppDayTrafficDao;
import com.hinsty.traffic.dao.Traffic;
import com.hinsty.traffic.dao.TrafficDao;
import com.hinsty.traffic.service.object.AppTrafficData;
import com.hinsty.traffic.service.object.TrafficData;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class TrafficDataHolder {
    static final String SP_APP = "app";
    CountDownLatch mFlag = new CountDownLatch(1);
    TrafficService mService;
    SharedPreferences mSP;
    public SharedPreferences appDataSP;
    PackageManager mPM;
    public Calendar calendar;
    public Map<String, AppTrafficData> apps;
    public TrafficData todayTraffic;
    public TrafficData monthTraffic;
    public TrafficData subTraffic;
    TrafficData currentTraffic;
    public boolean isDayAlertWorked = false;
    public boolean isMonthAlertWorked = false;
    public boolean isDataPlanAlertWorked = false;

    public TrafficDataHolder(TrafficService service) {
        mService = service;
        mSP = service.getSharedPreferences(Common.SP.FILE_NAME, Context.MODE_PRIVATE);
        appDataSP = service.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
        mPM = service.getPackageManager();
        todayTraffic = new TrafficData();
        monthTraffic = new TrafficData();
        currentTraffic = new TrafficData();
        subTraffic = new TrafficData();
        apps = new HashMap<>();
    }

    public void initBackground() {
        long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
//        long recordTime = mSP.getLong(Common.SP.KEY_BOOT_TIME, 0);
//        SharedPreferences.Editor editor = mSP.edit();
//        editor.putLong(Common.SP.KEY_BOOT_TIME, bootTime);
//        if (Math.abs(bootTime - recordTime) < 1000) {
//            //not first time to load service after device boot
//            subTraffic.loadFromSP(mSP, Common.SP.KEY_SUB);
//        } else {
//            if (mSP.getBoolean(Common.SP.KEY_FIRST_BOOT, true)) {
//                editor.putBoolean(Common.SP.KEY_FIRST_BOOT, false);
//                subTraffic.set(TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes());
//            } else {
//                subTraffic.set(0L, 0L);
//            }
//            subTraffic.saveToSP(editor, Common.SP.KEY_SUB);
//            appDataSP.edit().clear().apply();
//        }



        subTraffic.set(TrafficStats.getMobileRxBytes(),TrafficStats.getMobileTxBytes());

        // get month traffic
        TrafficDao trafficDao = mService.daoSession.getTrafficDao();
        QueryBuilder<Traffic> queryBuilder = trafficDao.queryBuilder();
        calendar = Calendar.getInstance();
        WhereCondition whereCondition = queryBuilder.and(TrafficDao.Properties.Year.eq(calendar
                .get(Calendar.YEAR)), TrafficDao.Properties.Month.eq(calendar.get(Calendar.MONTH)));
        List<Traffic> list = queryBuilder.where(whereCondition).list();
        monthTraffic.set(0L, 0L);
        for (Traffic traffic : list) {
            monthTraffic.add(traffic);
        }

        //get today traffic
        whereCondition = queryBuilder.and(TrafficDao.Properties.Year.eq(calendar.get(Calendar
                .YEAR)), TrafficDao.Properties.Month.eq(calendar.get(Calendar.MONTH)),
                TrafficDao.Properties.Day.eq(calendar.get(Calendar.DAY_OF_MONTH)));
        Traffic traffic = queryBuilder.where(whereCondition).unique();
        if (traffic == null) {
            traffic = new Traffic(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), 0L, 0L);
        }
        todayTraffic.add(traffic);
        currentTraffic.set(0L, 0L);
        mFlag.countDown();
        loadAppInfoFromDB();
        getHasNetPermissionApp();
        initAppMobileTraffic();
        isDayAlertWorked = mService.SD.dayAlertEnable && mSP.getBoolean(Common.SP
                .KEY_DAY_ALERT_WORKED, false);
        isMonthAlertWorked = mService.SD.monthAlertEnable && mSP.getBoolean(Common.SP
                .KEY_MONTH_ALERT_WORkED, false);
        isDataPlanAlertWorked = mSP.getBoolean(Common.SP.KEY_DATA_PLAN_ALERT_WORKED, false);
    }

    public void saveTrafficDataToDB() {
        subTraffic.set(mService.TH.mobileTraffic);
        SharedPreferences.Editor editor = mSP.edit();
        subTraffic.saveToSP(editor, Common.SP.KEY_SUB);
        TrafficDao dao = mService.daoSession.getTrafficDao();
        WhereCondition whereCondition = dao.queryBuilder().and(TrafficDao.Properties.Year.eq(year
                ()), TrafficDao.Properties.Month.eq(month()), TrafficDao.Properties.Day.eq(day()));
        Traffic traffic = dao.queryBuilder().where(whereCondition).unique();
        if (traffic == null) {
            traffic = new Traffic(year(), month(), day(), 0L, 0L);
        }
        todayTraffic.add(currentTraffic);
        monthTraffic.add(currentTraffic);
        traffic.add(currentTraffic);
        dao.insertOrReplace(traffic);
        currentTraffic.set(0L, 0L);
        editor.apply();
        //saveAppInfoToDB();
    }

    public void saveAppInfoToDB() {
        SharedPreferences.Editor editor = appDataSP.edit();
        final int year = year(), month = month(), day = day();
        final AppDayTrafficDao dao = mService.daoSession.getAppDayTrafficDao();
        QueryBuilder<AppDayTraffic> queryBuilder = dao.queryBuilder();
        WhereCondition whereCondition = queryBuilder.and(AppDayTrafficDao.Properties.Year.eq
                (year), AppDayTrafficDao.Properties.Month.eq(month),
                AppDayTrafficDao.Properties.Day.eq(day));
        final List<AppDayTraffic> trafficList = queryBuilder.where(whereCondition).list();
        final Map<String, AppDayTraffic> trafficMap = new HashMap<>(trafficList.size());
        for (AppDayTraffic t : trafficList) trafficMap.put(t.getPackageName(), t);
        updateAppTraffic();
        for (Map.Entry<String, AppTrafficData> appData : apps.entrySet()) {
            AppDayTraffic traffic = trafficMap.get(appData.getKey());
            AppTrafficData data = appData.getValue();
            if (traffic == null) {
                String name;
                try {
                    name = mPM.getApplicationInfo(appData.getKey(), 0).loadLabel(mPM).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    name = appData.getKey();
                }
                traffic = new AppDayTraffic(appData.getKey(), year, month, day, name,
                        data.realTraffic);
                trafficMap.put(appData.getKey(), traffic);
            } else {
                traffic.add(appData.getValue().realTraffic);
            }
            data.realTraffic.set(0L, 0L);
            data.saveSubTraffic(editor);
        }
        mService.daoSession.runInTx(new Runnable() {
            public void run() {
                for (AppDayTraffic traffic : trafficMap.values()) {
                    dao.insertOrReplace(traffic);
                }
            }
        });
        editor.apply();
    }

    void loadAppInfoFromDB() {
        setWriteFlag();
        int year = year();
        int month = month();
        int day = day();
        AppDayTrafficDao dao = mService.daoSession.getAppDayTrafficDao();
        QueryBuilder<AppDayTraffic> queryBuilder = dao.queryBuilder();
        WhereCondition whereCondition = queryBuilder.and(AppDayTrafficDao.Properties.Year.eq
                (year), AppDayTrafficDao.Properties.Month.eq(month),
                AppDayTrafficDao.Properties.Day.eq(day));
        for (AppDayTraffic traffic : queryBuilder.where(whereCondition).list()) {
            String packageName = traffic.getPackageName();
            if (apps.get(packageName) == null) {
                try {
                    apps.put(packageName, new AppTrafficData(mPM.getApplicationInfo(packageName,
                            0).uid, 0L, 0L));
                } catch (PackageManager.NameNotFoundException e) {
                    //do nothing
                }
            }
        }
        releaseWriteFlag();
    }

    public boolean updateAppTraffic() {
        if (mService.TH.isMobileConnect()) {
            for (Map.Entry<String, AppTrafficData> entry : apps.entrySet()) {
                AppTrafficData data = entry.getValue();
                TrafficData uidTraffic = new TrafficData(TrafficStats.getUidRxBytes(data.uid),
                        TrafficStats.getUidTxBytes(data.uid));
                TrafficData tmp = new TrafficData().set(uidTraffic);
                data.realTraffic.add(uidTraffic.sub(data.subTraffic));
                data.subTraffic.set(tmp);
            }
            return true;
        } else {
            return false;
        }
    }

    public Map<String, TrafficData> getAppTraffic() throws InterruptedException {
        tryRead();
        Map<String, TrafficData> map = new HashMap<>(apps.size());
        updateAppTraffic();
        for (Map.Entry<String, AppTrafficData> entry : apps.entrySet()) {
            map.put(entry.getKey(), new TrafficData().set(entry.getValue().realTraffic));
        }
        return map;
    }

    public void initAppMobileTraffic(){
        for(AppTrafficData app:apps.values()){
            app.subTraffic.set(TrafficStats.getUidRxBytes(app.uid),TrafficStats.getUidTxBytes(app.uid));
        }
    }

    public void getHasNetPermissionApp() {
        setWriteFlag();
        List<PackageInfo> packageInfoList = mPM.getInstalledPackages(PackageManager
                .GET_PERMISSIONS | PackageManager.GET_PROVIDERS);
        for (PackageInfo info : packageInfoList) {
            boolean hasInternetPermission = false;
            if (null != info.requestedPermissions) {
                for (String permission : info.requestedPermissions) {
                    if ("android.permission.INTERNET".equals(permission)) {
                        hasInternetPermission = true;
                        break;
                    }
                }
            }
            if (hasInternetPermission) {
                String packageName = info.packageName;
                AppTrafficData app = apps.get(packageName);
                if (null == app) {
                    app = new AppTrafficData(info.applicationInfo.uid, 0L, 0L);
                    apps.put(info.packageName, app);
                }
            }
        }
        releaseWriteFlag();
    }


    //contain day traffic and current traffic
    public TrafficData getTodayTraffic() throws InterruptedException {
        tryRead();
        return new TrafficData().set(todayTraffic).add(currentTraffic);
    }

    public TrafficData getMonthTraffic() throws InterruptedException {
        tryRead();
        return new TrafficData().set(monthTraffic).add(currentTraffic);
    }

    public void newDay() {
        int monthLen = Utils.getMonthDay(year(),month());
        int month = month();
        int day = day();
        SharedPreferences.Editor editor = mSP.edit();
        calendar = Calendar.getInstance();
        todayTraffic.set(0L, 0L);
        isDataPlanAlertWorked = false;
        editor.putBoolean(Common.SP.KEY_DAY_ALERT_WORKED,false);
        if (month != month()) {
            isMonthAlertWorked = false;
            editor.putBoolean(Common.SP.KEY_MONTH_ALERT_WORkED, false);
        }
        if((mService.SD.startDay>=monthLen&&day==monthLen)||(mService.SD.startDay<monthLen&&day()==mService.SD.startDay)) {
            isDataPlanAlertWorked=false;
            editor.putBoolean(Common.SP.KEY_DATA_PLAN_ALERT_WORKED, false).putBoolean(Common.SP.KEY_DAY_ALERT_WORKED, false).apply();
        }
        editor.apply();
    }


    int year() {
        return calendar.get(Calendar.YEAR);
    }

    int month() {
        return calendar.get(Calendar.MONTH);
    }

    int day() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


    public void setWriteFlag() {
        mFlag = new CountDownLatch(1);
    }

    public void releaseWriteFlag() {
        mFlag.countDown();
    }

    public void tryRead() throws InterruptedException {
        mFlag.await();
    }
}
