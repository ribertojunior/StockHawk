package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.io.IOException;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Add stock Async class
 */

public class StockAsync extends AsyncTask<String, Void, String> {
    private Context mContext;
    private String mSymbol;
    public StockAsync(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String[] params) {
        mSymbol = params[0];
        String ret = mContext.getString(R.string.error_stock_not_found, mSymbol);
        if (mSymbol != null && !mSymbol.isEmpty()) {

            try {
                Stock stock = YahooFinance.get(mSymbol);
                if (stock != null) {

                    if (!stock.toString().split(":")[1].trim().equals("null")) {
                        ret = mContext.getString(R.string.stock_added, mSymbol);
                        PrefUtils.addStock(mContext, mSymbol);
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
        if (ret.equals(mContext.getString(R.string.stock_added, mSymbol))) {
            QuoteSyncJob.syncImmediately(mContext);
            Intent dataUpdatedIntent = new Intent(QuoteSyncJob.ACTION_DATA_UPDATED).setPackage(mContext.getPackageName());
            mContext.sendBroadcast(dataUpdatedIntent);
        }
    }
}
