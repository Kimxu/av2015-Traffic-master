package com.hinsty.traffic.report;

import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import com.hinsty.traffic.BaseActivity;
import com.hinsty.traffic.Common;
import com.hinsty.traffic.R;
import com.hinsty.traffic.dao.AppDayTraffic;
import com.hinsty.traffic.dao.AppDayTrafficDao;
import com.hinsty.traffic.dao.DaoMaster;
import com.hinsty.traffic.dao.DaoSession;
import com.hinsty.traffic.service.BindHelper;
import com.hinsty.traffic.service.object.AppTrafficMap;
import com.hinsty.traffic.service.object.TrafficData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * @author dz
 * @version 2015/6/25.
 */
public abstract class ReportBaseActivity extends BaseActivity implements ReportView.LoaderGetter, View.OnClickListener {
    static final int LOADER_ID = 0x123;
    protected ReportView mReportView;
    protected boolean isLoaderWord = false;
    protected PackageManager mPM;
    protected Calendar mCalendar;
    protected static DaoSession daoSession;
    protected BindHelper mBindHelper = new BindHelper() {
        public void onConnect() {
            Bundle bundle = new Bundle();
            bundle.putInt(Common.YEAR, mCalendar.get(Calendar.YEAR));
            bundle.putInt(Common.MONTH, mCalendar.get(Calendar.MONTH));
            bundle.putInt(Common.DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
            getLoaderManager().initLoader(LOADER_ID, bundle, mReportView).forceLoad();
            isLoaderWord = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mPM = getPackageManager();
        setRootPaddingTop();
        mCalendar = GregorianCalendar.getInstance();
        ((TextView) findViewById(R.id.title)).setText(getTitle());
        mReportView = (ReportView) findViewById(R.id.report);
        mReportView.findViewById(R.id.date).setOnClickListener(this);
        mReportView.setLoader(this);
        mBindHelper.bind(this);
        if (daoSession == null) {
            daoSession = new DaoMaster(new DaoMaster.DevOpenHelper(this, Common.DB_FILE_NAME,
                    null).getWritableDatabase()).newSession();
        }
    }

    public void onBack(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        if (isLoaderWord) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
        mBindHelper.unBind(this);
        super.onDestroy();
    }

    protected abstract WhereCondition getCondition(QueryBuilder<AppDayTraffic> queryBuilder,
                                                   int year, int month, int day);

    @Override
    public Loader<List<ReportItemData>> onCreateLoader(int id, final Bundle args,final ReportView.ReportInfoHolder info) {
        return new AsyncTaskLoader<List<ReportItemData>>(this) {
            public List<ReportItemData> loadInBackground() {
                int year = args.getInt(Common.YEAR);
                int month = args.getInt(Common.MONTH);
                int day = args.getInt(Common.DAY);
                boolean isDayTraffic =false;
                if(isDayTraffic = ReportBaseActivity.this instanceof DayReportActivity){
                    info.title = (month+1)+"月"+day+"日";
                }else{
                    info.title=year+"年"+(month + 1) + "月";
                }
                AppTrafficMap appMap = new AppTrafficMap();

                TrafficData totalTraffic = null ;
                if(mCalendar.get(Calendar.DAY_OF_MONTH)==day && mCalendar.get(Calendar.MONTH)==month && mCalendar.get(Calendar.YEAR)==year){
                    try {
                        appMap = mBindHelper.binder.getTodayAppTraffic();
                        if(isDayTraffic){
                            totalTraffic = mBindHelper.binder.getTodayTraffic();
                        }else{
                            totalTraffic = mBindHelper.binder.getMonthTraffic();
                        }
                    } catch (RemoteException e) {
                        //do nothing
                    }
                }
                if(null!=totalTraffic){
                    info.totalRx = TrafficData.formatByte(totalTraffic.rx);
                    info.totalTx = TrafficData.formatByte(totalTraffic.tx);
                    info.totalRxTx = TrafficData.formatByte(totalTraffic.rx+totalTraffic.tx);
                }else{
                    info.totalRx="?";
                    info.totalTx="?";
                    info.totalRxTx="?";
                }
                AppDayTrafficDao dao = daoSession.getAppDayTrafficDao();
                QueryBuilder<AppDayTraffic> queryBuilder = dao.queryBuilder();
                WhereCondition condition = getCondition(queryBuilder, year, month, day);
                List<AppDayTraffic> appList = queryBuilder.where(condition).list();
                Map<String,ReportItemData> retDatas = new HashMap<>();
                for(AppDayTraffic traffic:appList){
                    ReportItemData d = retDatas.get(traffic.getPackageName());
                    if(d==null){
                        d = new ReportItemData();
                        try {
                            d.icon=mPM.getApplicationInfo(traffic.getPackageName(),0).loadIcon(mPM);
                        } catch (PackageManager.NameNotFoundException e) {
                            d.icon = getResources().getDrawable(R.mipmap.ic_launcher);
                        }
                        retDatas.put(traffic.getPackageName(),d);
                    }
                    d.rx += traffic.getRx();
                    d.tx += traffic.getTx();
                }
                for(Map.Entry<String,TrafficData> entry:appMap.map.entrySet()){
                    ReportItemData d = retDatas.get(entry.getKey());
                    if(null==d){
                        d = new ReportItemData();
                        try {
                            d.icon=mPM.getApplicationInfo(entry.getKey(),0).loadIcon(mPM);
                        } catch (PackageManager.NameNotFoundException e) {
                            d.icon = getResources().getDrawable(R.mipmap.ic_launcher);
                        }
                        retDatas.put(entry.getKey(),d);
                    }
                    d.tx+=entry.getValue().tx;
                    d.rx+=entry.getValue().rx;
                }
                ArrayList<ReportItemData> ret = new ArrayList<>(retDatas.size());
                for(ReportItemData d:retDatas.values()){
                    if(d.total()<1000)
                        continue;
                    ret.add(d);
                }
                Collections.sort(ret,new Comparator<ReportItemData>() {
                    public int compare(ReportItemData lhs, ReportItemData rhs) {
                        return (int) ((rhs.total()-lhs.total())>>17);
                    }
                });
                return ret;
            }
        };
    }
}
