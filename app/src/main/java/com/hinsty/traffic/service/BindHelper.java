package com.hinsty.traffic.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.hinsty.traffic.IServiceBinder;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class BindHelper {
    public IServiceBinder binder;

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder=IServiceBinder.Stub.asInterface(service);
            onConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder=null;
            onDisconnect();
        }
    };

    public void onDisconnect(){

    }

    public final void bind(Context context){
        context.bindService(new Intent(context,TrafficService.class),mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    public final void unBind(Context context){
        context.unbindService(mServiceConnection);
    }

    public void onConnect(){}
}
