package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;


public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String SYMBOL_URI = "URI";
    private static final int STOCK_LOADER = 1;
    private Uri mSymbolUri;

    LineChart mChart;
    TextView  mStockName;


    private static final String[] HISTORY_COLUMNS ={
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_SYMBOL_NAME,
            Contract.Quote.COLUMN_HISTORY
    };
    static final int COL_SYMBOL_ID = 0;
    static final int COL_SYMBOL_NAME = 1;
    static final int COL_HISTORY_ID = 2;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        mSymbolUri = args.getParcelable(SYMBOL_URI);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        mStockName = (TextView) rootView.findViewById(R.id.stock_name);

        mChart = (LineChart) rootView.findViewById(R.id.chart);
        mChart.setDescription(getString(R.string.chart_description));
        int color = getResources().getColor(android.R.color.black);
        mChart.setDescriptionColor(color);
        mChart.setDescriptionTextSize(16);
        mChart.setDrawBorders(true);
        mChart.setBorderColor(R.color.colorPrimaryDark);
        //
        mChart.getLegend().setTextColor(color);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAxisLineColor(color);
        xAxis.setTextColor(color);

        YAxis yAxis = mChart.getAxisRight();
        yAxis.setAxisLineColor(color);
        yAxis.setTextColor(color);

        yAxis = mChart.getAxisLeft();
        yAxis.setAxisLineColor(color);
        yAxis.setTextColor(color);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(STOCK_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mSymbolUri != null) {
            return new CursorLoader(getActivity(), mSymbolUri, HISTORY_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            String stockName = data.getString(COL_SYMBOL_NAME);
            mStockName.setText(stockName);
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> xValues;
            String[] xStrings;
            //Entry[] yFloats;
            String history = data.getString(COL_HISTORY_ID);
            String value;
            StringTokenizer tokenizer = new StringTokenizer(history, "\n");
            int x = 0;
            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();
            long day;
            int count = tokenizer.countTokens() - 1;
            xStrings = new String[count+1];
            //yFloats = new Entry[count+1];
            SimpleDateFormat monthDayFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
            while (tokenizer.hasMoreTokens()){
                value = tokenizer.nextToken();
                day = dayTime.setJulianDay(julianStartDay - x);
                //xValues.add(count - x, monthDayFormat.format(day));
                xStrings[count - x] = monthDayFormat.format(day);
                value = value.split(",")[1];
                //yFloats[count - x] = new Entry(Float.parseFloat(value), x);
                entries.add(new Entry(Float.parseFloat(value), count - x));
                x++;

            }
            xValues = new ArrayList<>(Arrays.asList(xStrings));
            //entries = new ArrayList<>()
            LineDataSet dataSet = new LineDataSet(entries, Contract.Quote.getStockFromUri(mSymbolUri));
            dataSet.setColors(new int[]{R.color.colorPrimaryDark}, getContext());
            dataSet.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));


            LineData lineData = new LineData(xValues, dataSet);


            mChart.setData(lineData);



        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
