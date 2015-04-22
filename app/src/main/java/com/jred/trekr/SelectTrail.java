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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SelectTrail extends ListActivity {

    TrailDataSource dbLink;
    ListView trailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_trail);

        dbLink = new TrailDataSource(this);
        dbLink.open();

        ArrayList<String> values = dbLink.getAllTrails();

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

        trailList = (ListView)findViewById(android.R.id.list);
        createOnClickListener(adapter);
    }

    private void createOnClickListener(ArrayAdapter adapter) {
        trailList.setAdapter(adapter);
        trailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        getMenuInflater().inflate(R.menu.menu_select_trail, menu);
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
