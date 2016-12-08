package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

import static com.udacity.stockhawk.ui.HistoryFragment.SYMBOL_URI;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable(SYMBOL_URI, getIntent().getData());
            HistoryFragment historyFragment = new HistoryFragment();
            historyFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.hystory_fragment, historyFragment)
                    .commit();

        }
    }

}
