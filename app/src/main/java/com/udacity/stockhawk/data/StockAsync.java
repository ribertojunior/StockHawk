package com.udacity.stockhawk.data;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Add stock Async class
 */

public class StockAsync extends AsyncTask<String, Void, String> {
    private Context mContext;

    public StockAsync(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String[] params) {
        String symbol = params[0];
        String ret = mContext.getString(R.string.error_stock_not_found, symbol);
        if (symbol != null && !symbol.isEmpty()) {

            try {
                Stock stock = YahooFinance.get(symbol);
                if (stock != null) {

                    if (!stock.toString().split(":")[1].trim().equals("null")) {
                        ret = mContext.getString(R.string.stock_added, symbol);
                        PrefUtils.addStock(mContext, symbol);
                        QuoteSyncJob.syncImmediately(mContext);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Timber.d(ret);

        }
        return ret;
    }

    @Override
    protected void onPostExecute(String ret) {
        Toast.makeText(mContext, ret, Toast.LENGTH_SHORT).show();
    }
}
