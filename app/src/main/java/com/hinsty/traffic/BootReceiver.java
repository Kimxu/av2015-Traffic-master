package com.hinsty.traffic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hinsty.traffic.service.TrafficService;

/**
 * @author dz
 * @version 2015/6/29.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,TrafficService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
