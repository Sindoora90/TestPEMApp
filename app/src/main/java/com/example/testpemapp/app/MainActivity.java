package com.example.testpemapp.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

//navigationdrawer machen...



    // NavDrawer
    private String[] drawerListViewItems;
    private ListView drawerListView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    ParseConnection connection;

    private      ListView lv;
    ListAdapter adapter;
    boolean firstLogin;

    Entry[] entrys;
    Bitmap pic;
    String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
            "Blackberry", "WebOS", "Ubuntu", "Windows7" };

    String[] titleArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // fuer lokalen speicher
        Parse.enableLocalDatastore(this.getApplicationContext());

        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        connection = new ParseConnection();

        // beim allerersten Start soll die login activity angezeigt werden, nach der registrierung nicht mehr
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
        } else {
            // show the signup or login screen

        Intent intent = new Intent();
        intent.setClassName(getPackageName(), getPackageName() + ".LoginActivity");
        //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
        startActivity(intent);
        Toast.makeText(MainActivity.this, "LOGIN ANGEZEIGT", Toast.LENGTH_SHORT).show();

        //firstLogin = true;
        //System.out.println("test");

    }


        // NavDrawer:

        // get list items from strings.xml
        drawerListViewItems = getResources().getStringArray(R.array.items);

        // get ListView defined in activity_main.xml
        drawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, drawerListViewItems));

        // App Icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // Set actionBarDrawerToggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // just styling option add shadow the right edge of the drawer
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        drawerListView.setOnItemClickListener(new DrawerItemClickListener());


        lv = (ListView)findViewById(R.id.listView);
       // List valueList = new ArrayList<Entry>();

        List valueList = new ArrayList<String>();
        for (int i = 0; i < 7; i++)
        {
            valueList.add("value"+i);
        }

        // adapter kriegt passendes layout also z.b. mit bilder und mehrzeilig... -> xml erstellen und hier ohne android. angeben
       //  adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, valueList);

        System.out.println("valueList: " + valueList);


       // lv.setAdapter(adapter);
       // String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
         //       "Blackberry", "WebOS", "Ubuntu", "Windows7" };

        // Versuch es auf Entry Objekte umzuschreiben:

        // hier connection aufrufen und alle entrys aus parse holen:



        Entry eins = new Entry(0,"Teddy", true, BitmapFactory.decodeResource(getResources(), R.drawable.bri)
        ,0.00, "description blablabla", ParseUser.getCurrentUser());

        Entry[] values2 = new Entry[]{eins, eins, eins, eins, eins, eins, eins};

        //connection.getAllEntrys();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
        query.include("user");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + scoreList.size() + " scores");
                    entrys = new Entry[scoreList.size()];
                    titleArray = new String[scoreList.size()];
                    Log.d("entry array size: ", "size of entry array:" + entrys.length);
                    Entry entry;
                    for(int i = 0; i < scoreList.size(); i++){
                        // Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){

                        ParseFile picFile = (ParseFile)scoreList.get(i).getParseFile("picFile");


                        picFile.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data,
                                             ParseException e) {
                                if (e == null) {
                                    pic = BitmapFactory.decodeByteArray(data, 0, data.length);

                                } else {
                                    Log.d("test",
                                            "There was a problem downloading the data.");
                                }
                            }
                        });


                        //Entry eins = new Entry(0,"Teddy", true, BitmapFactory.decodeResource(getResources(), R.drawable.bri)
                        //        ,0.00, "description blablabla", ParseUser.getCurrentUser());

                        try {
                            entry = new Entry(i, scoreList.get(i).getString("title"), scoreList.get(i).getBoolean("geschenk"), BitmapFactory.decodeByteArray(scoreList.get(i).getParseFile("picFile").getData(), 0, scoreList.get(i).getParseFile("picFile").getData().length), scoreList.get(i).getDouble("price"), scoreList.get(i).getString("description"), (ParseUser)scoreList.get(i).get("user"));

                        Log.d("entry", entry.toString());
                        titleArray[i] = scoreList.get(i).getString("title");
                        entrys[i] = entry;
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }


                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
                Log.d("allentrys: ", "Entry array dass an main geschickt werden soll: " + entrys);
                MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), titleArray, entrys);
                lv.setAdapter(adapter);
                Toast.makeText(MainActivity.this, "liste erzeugt ", Toast.LENGTH_SHORT).show();


            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName()+".DetailsActivity");
                intent.putExtra("index", arg2);
                //gibt title mit
                intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                startActivity(intent);

                //public void onListItemClick(ListView l, View v, int position, long id) {
                //showDetails(position)
            //void showDetails(int index) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), DetailsActivity.class);
//                intent.putExtra("index", index);
//                intent.putExtra("input", adapter.getItem(index).toGanzString());
//                startActivity(intent);

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // statt switch gehts auch mit if:

//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }


        switch (item.getItemId()) {
            case R.id.menu_add:
                // Red item was selected
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName()+".NewEntryActivity");
                //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                startActivity(intent);
                Toast.makeText(MainActivity.this, "new clicked", Toast.LENGTH_SHORT).show();
                //System.out.println("test");
                return true;

            case R.id.action_settings:
                // settings item was selected
                Toast.makeText(MainActivity.this, "settings clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    // NavDrawer

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            if(((TextView) view).getText().toString().equals("FriendShifts")){
                // System.out.println("drawer soll geschlossen werden");
                drawerLayout.closeDrawer(drawerListView);
            }

            if(((TextView) view).getText().toString().equals("Meine Shifts")){
                // System.out.println(" piraten history sollt jz angezeigt werden miau miau");
                Intent myIntent = new Intent(view.getContext(), MyEntrys.class);
                startActivity(myIntent);
                // drawer schliessen noch rein
                drawerLayout.closeDrawer(drawerListView);

            }
            if(((TextView) view).getText().toString().equals("Freunde")){
                // System.out.println(" piraten history sollt jz angezeigt werden miau miau");
                //Intent myIntent = new Intent(view.getContext(), HistoryActivity.class);
                //EditText editText = (EditText) findViewById(R.id.outputText);
                //String message = editText.getText().toString();
                //myIntent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(myIntent);
                // drawer schliessen noch rein
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName()+".FriendActivity");
                intent.putExtra("index", "testtesttest");
                startActivity(intent);

                drawerLayout.closeDrawer(drawerListView);

            }

            Toast.makeText(MainActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();

        }
    }
}
