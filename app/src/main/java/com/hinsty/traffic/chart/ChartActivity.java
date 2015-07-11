package com.hinsty.traffic.chart;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.hinsty.traffic.Application;
import com.hinsty.traffic.BaseActivity;
import com.hinsty.traffic.BuildConfig;
import com.hinsty.traffic.Common;
import com.hinsty.traffic.R;
import com.hinsty.traffic.dao.DaoSession;
import com.hinsty.traffic.dao.Traffic;
import com.hinsty.traffic.dao.TrafficDao;
import com.hinsty.traffic.service.BindHelper;
import com.hinsty.traffic.service.object.TrafficData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import de.greenrobot.dao.query.WhereCondition;

/**
 * @author dz
 * @version 2015/6/24.
 */
public class ChartActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<LineData> {
    LineChart mLineChart;
    static final int LOADER_ID = 0x456;
    boolean mIsLoader = false;
    Calendar mCalendar;
    DaoSession mDaoSession;
    TextView mDateView;
    BindHelper mBindHelper = new BindHelper(){
        public void onConnect() {
            update();
        }
    };
    Bundle mBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        setRootPaddingTop();
        mCalendar = Calendar.getInstance();
        mLineChart = (LineChart) findViewById(R.id.chart);
        mDateView = (TextView) findViewById(R.id.date);
        mDaoSession = ((Application) getApplication()).daoSession();
        mBindHelper.bind(this);
    }

    public void onBack(View view){
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if (mIsLoader) getLoaderManager().destroyLoader(LOADER_ID);
        mBindHelper.unBind(this);
        super.onDestroy();
    }

    protected void update() {
        mBundle = new Bundle();
        mBundle.putInt(Common.YEAR, mCalendar.get(Calendar.YEAR));
        mBundle.putInt(Common.MONTH, mCalendar.get(Calendar.MONTH));
        getLoaderManager().initLoader(LOADER_ID, mBundle, this).forceLoad();
        mIsLoader = true;
    }

    @Override
    public Loader<LineData> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<LineData>(this) {
            @Override
            public LineData loadInBackground() {
                final int today = mCalendar.get(Calendar.DAY_OF_MONTH);
                final int year = args.getInt(Common.YEAR);
                final int month = args.getInt(Common.MONTH);
                final LineData[] lineDateBox = new LineData[1];
                mDaoSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        TrafficData todayTraffic = null;
                        TrafficDao dao = mDaoSession.getTrafficDao();
                        List<Traffic> trafficList;
                        if (BuildConfig.TEST_VIEW) {
                            trafficList = new ArrayList<>();
                            Random random = new Random();
                            for (int i = 5; i <= 25; ++i) {
                                if(i==10||i==12||i==13||i==14 || i==19||i==20||i==23){
                                    continue;
                                }
                                long rx,tx;
                                tx=(1L<<20)  * random.nextInt(20);
                                rx=(1L<<20)  * random.nextInt(25);
                                trafficList.add(new Traffic(2015, 5, i,rx,tx));
                            }
                        } else {
                            WhereCondition condition = dao.queryBuilder().and(TrafficDao
                                    .Properties.Year.eq(year), TrafficDao.Properties.Month.eq
                                    (month));
                            trafficList = dao.queryBuilder().where(condition).list();
                            try {
                                todayTraffic = mBindHelper.binder.getTodayTraffic();
                            } catch (RemoteException e) {
                                todayTraffic = null;
                            }
                        }
                        int size = trafficList.size();
                        if (todayTraffic != null) {
                            boolean replese=false;
                            for (Traffic t : trafficList) {
                                if (t.getDay() == today) {
                                    replese = true;
                                    t.set(todayTraffic.rx, todayTraffic.tx);
                                }
                            }
                            if(!replese){
                                trafficList.add(new Traffic(year,month,today,todayTraffic.rx,todayTraffic.tx));
                            }
                        }

                        Collections.sort(trafficList, new Comparator<Traffic>() {
                            @Override
                            public int compare(Traffic lhs, Traffic rhs) {
                                return lhs.getDay() - rhs.getDay();
                            }
                        });
                        ArrayList<String> xValList = new ArrayList<>(size);

                        //ArrayList<Entry> txEntryList = new ArrayList<>(size);
                        ArrayList<Entry> rxEntryList = new ArrayList<>(size);
                        ArrayList<Entry> totalEntryList = new ArrayList<>(size);



                        int day = 1;
                        int index = 0;
                        int dataIndex = 1;
                        Traffic t = null;
                        boolean start = false;
                        boolean first = true;
                        while (day <= 31) {
                            if (t == null) {
                                if (index >= size) break;
                                t = trafficList.get(index);
                                ++index;
                            }
                            if (day == t.getDay()) {
                                start = true;
                                float txValue = (float) (1.0 * (t.getTx() >> 18) / 4);
                                float rxValue = (float) (1.0 * (t.getRx() >> 18) / 4);
                                t = null;
                                System.out.println("==="+txValue+" "+(txValue+rxValue));
                                rxEntryList.add(new Entry(rxValue, dataIndex));
                                totalEntryList.add(new Entry(txValue + rxValue, dataIndex));
                            }
                            if (start) {
                                if (first) {
                                    first = false;
                                    xValList.add(String.valueOf(day - 1));
                                }
                                xValList.add(String.valueOf(day));
                                ++dataIndex;
                            }
                            ++day;
                        }
                        xValList.add(String.valueOf(day));

                        //LineDataSet txSet = new LineDataSet(txEntryList,"发送流量");
                        LineDataSet rxSet = new LineDataSet(rxEntryList, "接收流量");
                        LineDataSet totalSet = new LineDataSet(totalEntryList, "发送流量");
//                        rxSet.setDrawCubic(true);
//                        totalSet.setDrawCircles(true);
                        ArrayList<LineDataSet> setList = new ArrayList<>(3);
                        //setList.add(txSet);
                        setList.add(rxSet);
                        setList.add(totalSet);
                        //txSet.setDrawValues(false);
                        //txSet.setDrawCircles(false);
                        rxSet.setDrawValues(false);
                        rxSet.setDrawCircles(false);
                        totalSet.setDrawValues(false);
                        totalSet.setDrawCircles(false);
                        rxSet.setDrawFilled(true);
                        totalSet.setDrawFilled(true);
                        totalSet.setFillColor(getResources().getColor(android.R.color
                                .holo_blue_light));
                        rxSet.setFillColor(getResources().getColor(android.R.color.holo_red_dark));


                        rxSet.setColor(getResources().getColor(android.R.color.holo_red_light));
                        totalSet.setColor(getResources().getColor(android.R.color
                                .holo_blue_bright));
                        lineDateBox[0] = new LineData(xValList, setList);
                    }
                });
                return lineDateBox[0];
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<LineData> loader, LineData data) {
        mIsLoader = false;
        mLineChart.setData(data);
        mLineChart.animateY(1200, Easing.EasingOption.EaseInCubic);
        mDateView.setText(String.format("%d年%d月",mBundle.getInt(Common.YEAR),1+mBundle.getInt(Common.MONTH)));
    }

    @Override
    public void onLoaderReset(Loader<LineData> loader) {

    }
}
