package com.jred.trekr;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class SelectPOI extends ListActivity {

    TrailDataSource dbLink;
    ListView poiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_poi);

        dbLink = new TrailDataSource(this);
        dbLink.open();

        ArrayList<String> values = dbLink.getAllPOIs();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

        // For loop through the list of trails
//        for (String trailName : values) {
//            adapter.add(trailName);
//        }

        adapter.notifyDataSetChanged();

        poiList = (ListView)findViewById(android.R.id.list);
        createOnClickListener(adapter);
    }

    private void createOnClickListener(ArrayAdapter adapter) {
        poiList.setAdapter(adapter);
        poiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Clicks on the nav menu are handled here
                // On click, an integer position value will be set
                Intent intent = new Intent();
                intent.setData(Uri.parse(String.valueOf(position + 1)));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_poi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
