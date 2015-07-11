package com.hinsty.traffic.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.Settings;

import com.hinsty.traffic.MainActivity;
import com.hinsty.traffic.R;
import com.hinsty.traffic.service.object.TrafficData;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class TrafficNotification {
    static final int FOREGROUD = 0x123;
    static final int ALERT = 0x122;
    TrafficService mService;
    Resources mResource;
    NotificationManager mNM;

    public TrafficNotification(TrafficService context) {
        mService = context;
        mResource = context.getResources();
        mNM = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void startForeground() {
        mService.startForeground(FOREGROUD, getTrafficNotification());
    }

    public void updateTrafficNotification() {
        mNM.notify(FOREGROUD, getTrafficNotification());
    }

    public static enum AlertType {
        DAY, MONTH, DATA_PLAN
    }

    @TargetApi(18)
    public void showAlertNotification(AlertType type, boolean stopNetConnect) {
        Notification.Builder builder = new Notification.Builder(mService).setSmallIcon(R.mipmap.icon);
        switch (type) {
            case DATA_PLAN:
                if (0 != mService.SD.dataPlan) {
                    builder.setContentTitle("使用流量已超过套餐");
                } else {
                    builder.setContentTitle("使用流量过多");
                }
                break;
            case DAY:
                builder.setContentTitle("本日使用流量已超过警戒值");
                break;
            case MONTH:
                builder.setContentTitle("本月使用流量已超过警戒值");
                break;
        }
        if (stopNetConnect) {
            builder.setContentText("已停止流量连接，触摸开启");
        } else {
            builder.setContentText("请注意流量使用,触摸关闭流量");
        }
        builder.setContentIntent(PendingIntent.getActivity(mService, 0, new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0));
        builder.setAutoCancel(true);
        mNM.notify(ALERT, builder.build());
    }

    @TargetApi(18)
    Notification getTrafficNotification() {
        String title;
        String text;
        try {
            title = String.format(mService.SD.dataPlan <= 0 ? "本月流量%s" : "已使用%s/%dM", TrafficData.formatByte(mService.TDH.getMonthTraffic().sum()), mService.SD.dataPlan);
        } catch (InterruptedException e) {
            title = "Traffic";
        }
        try {
            text = String.format("今日使用%s", TrafficData.formatByte(mService.TDH.getTodayTraffic().sum()));
        } catch (InterruptedException e) {
            text = "";
        }
        return new Notification.Builder(mService).setContentTitle(title).setContentText(text).setSmallIcon(R.mipmap.icon).setContentIntent(PendingIntent.getActivity(mService, 0, new Intent(mService, MainActivity

                .class), 0)).build();
    }
}
