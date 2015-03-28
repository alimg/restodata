package com.restodata.restodata;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.restodata.restodata.api.WebApiClient;
import com.restodata.webapp.model.ApiResponse;
import com.restodata.webapp.model.MenuItem;
import com.restodata.webapp.model.PredictResult;

import java.util.Calendar;

public class PredictActivity extends Activity{
    private WebApiClient.WebApiCallback mPredictListener = new WebApiClient.WebApiCallback() {
        @Override
        public void onSuccess(ApiResponse response) {
            if (response.status.equals("success")) {
                renderResults(response.predictResult);
            }
        }

        @Override
        public void onError(String error) {

        }
    };

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_predict);
        tableLayout = (TableLayout) findViewById(R.id.tabela);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        WebApiClient.sendPredictRequest(calendar.getTime(), mPredictListener);
    }

    private void renderResults(PredictResult res) {
        tableLayout.removeAllViews();
        //add header row
        TableRow tr = new TableRow(this);

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        tr.addView(new TextView(this));
        for (MenuItem i: res.menuItems.values()) {
            TextView tv = new TextView(this);
            tv.setText(i.name);
            tr.addView(tv);
        }
        tableLayout.addView(tr);
        for (int hour=0; hour<24; hour++) {
            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            for (MenuItem it: res.menuItems.values()) {
                TextView tv = new TextView(this);
                tv.setText(""+res.results.get(it.id).get(hour));
                tr.addView(tv);
            }
            tableLayout.addView(tr);
        }
    }
}
