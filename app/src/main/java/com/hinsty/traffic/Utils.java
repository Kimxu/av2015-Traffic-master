package com.hinsty.traffic;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * @author dz
 * @version 2015/6/17.
 */
public class Utils {
    public static boolean isRun(String className,Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> serviceInfos = (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(30);
        for(int i=serviceInfos.size()-1;i>=0;--i){
            if(serviceInfos.get(i).service.getClassName().equals(className)){
                return true;
            }
        }
        return false;
    }
    public static void d(Object obj){
        Log.d("Traffic",String.valueOf(obj));
    }


    static final byte[] MONTH_DAY = new byte[]{31,28,31,30,31,30,31,31,30,31,30,31};
    public static int getMonthDay(int year,int month){
        month=month%12;
        year+=month/12;
        if(month==1){
            if(year%400==0 || (year%4==0 && year%100!=0)){
                return 29;
            }else{
                return 28;
            }
        }else{
            return MONTH_DAY[month];
        }
    }
}
