package com.hinsty.traffic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hinsty.traffic.service.TrafficService;

import java.util.Calendar;

/**
 * 监听开机，启动服务
 *
 * @author dz
 * @version 2015/6/15.
 */
public class ShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("xxx", Context.MODE_MULTI_PROCESS);
        if (TrafficService.isRun(context)) {
            sp.edit().putString("is_run", Calendar.getInstance().getTime().toString()).commit();
        }
    }
}
