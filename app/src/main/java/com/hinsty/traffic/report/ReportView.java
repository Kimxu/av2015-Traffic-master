package com.hinsty.traffic.report;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hinsty.traffic.R;
import java.util.List;

/**
 * @author dz
 * @version 2015/6/24.
 */
public class ReportView extends RelativeLayout implements LoaderManager
        .LoaderCallbacks<List<ReportItemData>> {
    public static class ReportInfoHolder{
        public String title;
        public String totalRx;
        public String totalTx;
        public String totalRxTx;
    }
    public static interface LoaderGetter{
        public Loader<List<ReportItemData>> onCreateLoader(int id, Bundle args,ReportInfoHolder info);
    }
    ListView mListView;
    LoaderGetter mLoaderGetter;
    TextView mTotalRxView,mTotalTxView;
    TextView mTotalTraffic;
    TextView mDateView;
    ReportInfoHolder mReportInfoHolder;


    public ReportView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_report, this);
        mListView = (ListView) findViewById(R.id.list_view);
        mTotalRxView = (TextView) findViewById(R.id.total_rx);
        mTotalTxView = (TextView) findViewById(R.id.total_tx);
        mTotalTraffic = (TextView) findViewById(R.id.total_traffic);
        mDateView = (TextView) findViewById(R.id.date);
        mReportInfoHolder = new ReportInfoHolder();
    }

    public void setLoader(LoaderGetter getter){
        mLoaderGetter = getter;
    }

    @Override
    public Loader<List<ReportItemData>> onCreateLoader(int id, Bundle args) {
        return mLoaderGetter.onCreateLoader(id,args,mReportInfoHolder);
    }

    @Override
    public void onLoadFinished(Loader<List<ReportItemData>> loader, List<ReportItemData> data) {
        mListView.setAdapter(new ReportAdapter(data));
        if(mReportInfoHolder!=null){
            mDateView.setText(mReportInfoHolder.title != null ? mReportInfoHolder.title : "");
            mTotalRxView.setText(mReportInfoHolder.totalRx==null?"?":mReportInfoHolder.totalRx);
            mTotalTxView.setText(mReportInfoHolder.totalTx==null?"?":mReportInfoHolder.totalTx);
            mTotalTraffic.setText(mReportInfoHolder.totalRxTx==null?"?":mReportInfoHolder.totalRxTx);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ReportItemData>> loader) {

    }

    public static class ViewHolder {
        public ImageView iconView;
        public TextView nameView;
        public TextView rxView;
        public TextView txView;
        public TextView totalView;
    }

    public class ReportAdapter extends BaseAdapter {
        List<ReportItemData> mDatas;
        LayoutInflater mInflater;

        public ReportAdapter(List<ReportItemData> apps) {
            mDatas = apps;
            mInflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.view_report_item, null);
            }
            if (null == (holder = (ViewHolder) convertView.getTag())) {
                holder = new ViewHolder();
                holder.iconView = (ImageView) convertView.findViewById(R.id.icon);
                holder.nameView = (TextView) convertView.findViewById(R.id.name);
                holder.rxView = (TextView) convertView.findViewById(R.id.rx);
                holder.txView = (TextView) convertView.findViewById(R.id.tx);
                holder.totalView = (TextView) convertView.findViewById(R.id.total);
                convertView.setTag(holder);
            }
            ReportItemData i = mDatas.get(position);
            holder.iconView.setImageDrawable(i.icon);
            holder.txView.setText(i.getTx());
            holder.rxView.setText(i.getRx());
            holder.totalView.setText(i.getTotal());
            return convertView;
        }
    }
}
