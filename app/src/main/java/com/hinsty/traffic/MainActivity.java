package com.hinsty.traffic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.hinsty.traffic.service.BindHelper;
import com.hinsty.traffic.service.TrafficService;
import com.hinsty.traffic.service.object.MainActivityData;

import java.util.Calendar;

/**
 * @author dz
 * @version 2015/6/23.
 */
public class MainActivity extends BaseActivity {
    TextView mDataPlan, mMonthTraffic, mTodayTraffic, mMonthPercent, mBalanceDay,mPercentStatue;
    ViewStub mViewStub;
    Handler mHandler;
    BindHelper mBinder = new BindHelper() {
        @Override
        public void onConnect() {
            update();
            try {
                mBinder.binder.useFastCheck();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    };
    int RED,BLUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintColor(getResources().getColor(R.color.main_fore));
        mHandler = new Handler();
        setContentView(R.layout.activity_main);
        mViewStub = (ViewStub) findViewById(R.id.welcome_stub);
        View view = findViewById(R.id.root);
       setRootPaddingTop();
        mDataPlan = (TextView) findViewById(R.id.data_plan);
        mMonthTraffic = (TextView) findViewById(R.id.month_traffic);
        mTodayTraffic = (TextView) findViewById(R.id.today_traffic);
        mMonthPercent = (TextView) findViewById(R.id.month_percent);
        mBalanceDay = (TextView) findViewById(R.id.balance_day);
        mPercentStatue = (TextView) findViewById(R.id.percent_statue);
        startService(new Intent(this,TrafficService.class));
        mBinder.bind(this);
        showWelcomeView();
        RED = getResources().getColor(android.R.color.holo_red_light);
        BLUE = getResources().getColor(android.R.color.holo_blue_light);
    }

    void showWelcomeView(){
//        Calendar calendar = Calendar.getInstance();
//        SharedPreferences sp = getSharedPreferences(Common.SP.FILE_NAME, MODE_PRIVATE);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        if (sp.getInt(Common.SP.KEY_DAY, 0) == day) {
//            mViewStub.setVisibility(View.GONE);
//        } else {
//            sp.edit().putInt(Common.SP.KEY_DAY, day).apply();
            mViewStub.setLayoutResource(R.layout.view_welcom);
            final View welcomeView = mViewStub.inflate();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = new AlphaAnimation(1f, 0f);
                    animation.setDuration(2000);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            welcomeView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    welcomeView.startAnimation(animation);
                }
            }, 2000);
//        }
    }

    void update(){
        MainActivityData data;
        if(mBinder.binder!=null) try {
            data = mBinder.binder.getMainData();
            if(data.dataPlan==-1){
                mDataPlan.setText("未设置套餐");
            }else{
                mDataPlan.setText(String.format("%dM",data.dataPlan));
            }
            mBalanceDay.setText(""+data.balanceDay);
            mTodayTraffic.setText(data.todayTraffic);
            mMonthTraffic.setText(data.monthTraffic);
            int usePercent = data.userPercent;
            if(usePercent==-1){
                mMonthPercent.setText("请设置套餐");
                mPercentStatue.setText("");
            }else{
                if(usePercent>1000){
                    usePercent-=1000;
                    mPercentStatue.setText("超出");
                    mPercentStatue.setTextColor(RED);
                    mMonthPercent.setTextColor(RED);
                }else{
                    mPercentStatue.setText("已用");
                    mMonthPercent.setTextColor(BLUE);
                    mPercentStatue.setTextColor(BLUE);
                }
                if(usePercent%10==0){
                    mMonthPercent.setText((usePercent/10)+"%");
                }else{
                    mMonthPercent.setText((1.0*usePercent/10)+"%");
                }
            }
        } catch (RemoteException e) {
            //do nothing
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            update();
        }
    };


    @Override
    protected void onStart() {
        registerReceiver(receiver, new IntentFilter(Common.ACTION_UI_NEED_UPDATE));
        if(mBinder.binder!=null){
            try {
                mBinder.binder.useFastCheck();
            } catch (RemoteException e) {
                //do nothing
            }
            update();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        if(mBinder.binder!=null){
            try {
                mBinder.binder.cancelFastCheck();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mBinder.unBind(this);
        super.onDestroy();
    }

    public void onSet(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }
}
