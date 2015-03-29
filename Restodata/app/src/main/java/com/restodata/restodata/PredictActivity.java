package com.restodata.restodata;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.restodata.restodata.api.WebApiClient;
import com.restodata.webapp.model.ApiResponse;
import com.restodata.webapp.model.MenuItem;
import com.restodata.webapp.model.PredictResult;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

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
    private SwitchCompat modeSwitch;
    private ColumnChartView barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_predict);
        tableLayout = (TableLayout) findViewById(R.id.tabela);
        modeSwitch = (SwitchCompat) findViewById(R.id.switch1);
        barChart = (ColumnChartView) findViewById(R.id.chart);
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    modeSwitch.setText("Graph");
                    tableLayout.setVisibility(View.GONE);
                    barChart.setVisibility(View.VISIBLE);
                } else {
                    modeSwitch.setText("Table");
                    tableLayout.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        WebApiClient.sendPredictRequest(calendar.getTime(), mPredictListener);
    }

    private void renderResults(PredictResult res) {
        LayoutInflater inflater = LayoutInflater.from(this);
        tableLayout.removeAllViews();
        //add header row
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, null);
        TextView tv = (TextView) inflater.inflate(R.layout.cell_table_header, null);
        tv.setText("Saat");
        tr.addView(tv);
        for (MenuItem i: res.menuItems.values()) {
            tv = (TextView) inflater.inflate(R.layout.cell_table_header, null);
            tv.setText(i.name);
            tr.addView(tv);
        }
        tableLayout.addView(tr);
        for (int hour=0; hour<24; hour++) {
            tr = (TableRow) inflater.inflate(R.layout.table_row, null);
            if (hour%2==0) {
                tr.setBackgroundColor(0x22000000);
            }
            tv = (TextView) inflater.inflate(R.layout.cell_table_header, null);
            tv.setText("" + hour);
            tr.addView(tv);

            for (MenuItem it: res.menuItems.values()) {
                tv = (TextView) inflater.inflate(R.layout.cell_table_item, null);
                tv.setText(String.format("%.0f", res.results.get(it.id).get(hour)));
                tr.addView(tv);
            }
            tableLayout.addView(tr);
        }

        List<Integer> colors = new ArrayList<>();
        for (MenuItem it: res.menuItems.values()) {
            colors.add(ColorList.next());
        }
        List<Column> columns = new ArrayList<Column>();
        for (int hour=0; hour<24; hour++) {
            List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
            int j=0;
            for (MenuItem it: res.menuItems.values()) {
                values.add(new SubcolumnValue(res.results.get(it.id).get(hour).floatValue(), colors.get(j++)));
            }
            Column column = new Column(values);
            column.setHasLabels(false);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);
        data.setStacked(true);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Saat");

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        barChart.setColumnChartData(data);
    }
}
