package com.restodata.restodata;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.restodata.restodata.adapter.MenuListAdapter;
import com.restodata.restodata.api.WebApiClient;
import com.restodata.webapp.model.ApiResponse;
import com.restodata.webapp.model.MenuResponse;

public class EditActivity extends Activity {

    private TextView textView;
    private WebApiClient.WebApiCallback mMenuListener = new WebApiClient.WebApiCallback() {
        @Override
        public void onSuccess(ApiResponse response) {
            if (response.status.equals("success")) {
                renderMenu(response.menu);
            }
        }

        @Override
        public void onError(String error) {

        }
    };
    private ListView listView;
    private MenuListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        listView = (ListView) findViewById(R.id.listview_menu);

        mAdapter = new MenuListAdapter(this);
        listView.setAdapter(mAdapter);

        WebApiClient.getMenu(mMenuListener);
    }

    private void renderMenu(MenuResponse menu) {
        mAdapter.setList(menu.items);
    }
}
