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
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    // NavDrawer
    private String[] drawerListViewItems;
    private int[] drawerIcons;
    private ListView drawerListView;
    private DrawerLayout drawerLayout;
    private CharSequence mTitle;
    NavListAdapter mNavAdapter;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    ParseConnection connection;

    private ListView lv;

    Entry[] entrys;
    Bitmap pic;

    String[] titleArray;
    // int size =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connection = new ParseConnection();

        lv = (ListView) findViewById(R.id.listView);
        createNavDrawer();

        loadEntrys();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".DetailsActivity");
                intent.putExtra("index", arg2);
                //gibt title mit
                intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                intent.putExtra("objectIdTest", entrys[arg2].getId());
                startActivity(intent);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


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

                Intent intent = new Intent();

                intent.setClassName(getPackageName(), getPackageName() + ".NewEntryActivity");
                //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                intent.putExtra("index", 0);
                //gibt title mit
                intent.putExtra("selected", "");
                intent.putExtra("mine", false);

                startActivity(intent);

                return true;

            case R.id.menu_load:

                loadEntrys();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    // zum aktualisieren (?)
//    @Override
//    public void onResume() {
//        super.onResume();  // Always call the superclass method first
//
//        loadEntrys();
//        Toast.makeText(MainActivity.this, "es sollte aktualisiert werden", Toast.LENGTH_SHORT).show();
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }


    private void loadEntrys() {

        // 1. get all friends:
        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("Friendship");
        innerQuery.include("user");
        innerQuery.whereEqualTo("fromUser", ParseUser.getCurrentUser());

        innerQuery.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> friendlist, ParseException e) {
                if (e == null) {

                    ParseUser[] names = new ParseUser[friendlist.size() + 1];
                    names[0] = ParseUser.getCurrentUser();


                    if (friendlist.size() > 0) {


                        for (int i = 0; i < friendlist.size(); i++) {
                            names[i + 1] = (ParseUser) friendlist.get(i).get("toUser");

                            ParseQuery<ParseObject> queryl = ParseQuery.getQuery("Entry");
                            queryl.include("user");
                            queryl.orderByDescending("createdAt");
                            queryl.whereContainedIn("user", Arrays.asList(names));

                            queryl.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> scoreList, ParseException e) {

                                    if (e == null) {
                                        if (scoreList.size() > 0) {

                                            Log.d("score", "Retrieved " + scoreList.size() + " scores");
                                            //size = scoreList.size()-1;
                                            entrys = new Entry[scoreList.size()];
                                            titleArray = new String[scoreList.size()];
                                            Log.d("entry array size: ", "size of entry array:" + entrys.length);
                                            Entry entry;
                                            for (int i = 0; i < scoreList.size(); i++) {
                                                // Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){

                                                ParseFile picFile = (ParseFile) scoreList.get(i).getParseFile("picFile");

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


                                                try {
                                                    entry = new Entry(scoreList.get(i).getObjectId(), scoreList.get(i).getString("title"), scoreList.get(i).getBoolean("geschenk"), BitmapFactory.decodeByteArray(scoreList.get(i).getParseFile("picFile").getData(), 0, scoreList.get(i).getParseFile("picFile").getData().length), scoreList.get(i).getDouble("price"), scoreList.get(i).getString("description"), (ParseUser) scoreList.get(i).get("user"));

                                                    Log.d("entry", entry.toString());
                                                    titleArray[i] = scoreList.get(i).getString("title");
                                                    entrys[i] = entry;
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }

                                            ArrayList a = new ArrayList<String>(titleArray.length);
                                            for (String s : titleArray) {
                                                a.add(s);
                                            }
                                            ArrayList b = new ArrayList<Entry>(entrys.length);
                                            for (Entry s : entrys) {
                                                b.add(s);
                                            }
                                            MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), a, b);
                                            lv.setAdapter(adapter);
                                            //    Toast.makeText(MainActivity.this, "liste erzeugt ", Toast.LENGTH_SHORT).show();


                                        } else {
                                            Log.d("fehler in query not parse", "leere Liste d.h. keine einträge von freunden/sich selbst");
                                        }
                                    } else {
                                        Log.d("fehler", "hat nicht geklappt");
                                        //    Toast.makeText(MainActivity.this, "kein internet... ", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }
                    } else {

                        ParseQuery<ParseObject> queryl = ParseQuery.getQuery("Entry");
                        queryl.include("user");
                        queryl.orderByDescending("createdAt");
                        queryl.whereEqualTo("user", names[0]);

                        queryl.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> scoreList, ParseException e) {

                                if (e == null) {
                                    if (scoreList.size() > 0) {

                                        Log.d("score", "Retrieved " + scoreList.size() + " scores");
                                        //size = scoreList.size()-1;
                                        entrys = new Entry[scoreList.size()];
                                        titleArray = new String[scoreList.size()];
                                        Log.d("entry array size: ", "size of entry array:" + entrys.length);
                                        Entry entry;
                                        for (int i = 0; i < scoreList.size(); i++) {
                                            // Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){

                                            ParseFile picFile = (ParseFile) scoreList.get(i).getParseFile("picFile");


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


                                            try {
                                                entry = new Entry(scoreList.get(i).getObjectId(), scoreList.get(i).getString("title"), scoreList.get(i).getBoolean("geschenk"), BitmapFactory.decodeByteArray(scoreList.get(i).getParseFile("picFile").getData(), 0, scoreList.get(i).getParseFile("picFile").getData().length), scoreList.get(i).getDouble("price"), scoreList.get(i).getString("description"), (ParseUser) scoreList.get(i).get("user"));

                                                Log.d("entry", entry.toString());
                                                titleArray[i] = scoreList.get(i).getString("title");
                                                entrys[i] = entry;
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }

                                        ArrayList a = new ArrayList<String>(titleArray.length);
                                        for (String s : titleArray) {
                                            a.add(s);
                                        }
                                        ArrayList b = new ArrayList<Entry>(entrys.length);
                                        for (Entry s : entrys) {
                                            b.add(s);
                                        }
                                        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), a, b);
                                        lv.setAdapter(adapter);
                                        //    Toast.makeText(MainActivity.this, "liste erzeugt ", Toast.LENGTH_SHORT).show();


                                    } else {
                                        Log.d("fehler in query not parse 2", "afafleere Liste d.h. keine einträge von freunden/sich selbst");
                                    }
                                } else {
                                    Log.d("fehler 2", "hat nicht geklappt afs");
                                    //    Toast.makeText(MainActivity.this, "kein internet... ", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    }
                }


            }
        });

    }


    private void createNavDrawer() {

        // get list items from strings.xml
        drawerListViewItems = getResources().getStringArray(R.array.items);

        //drawer list icons
        //drawerIcons = new int[] {R.drawable.ic_action_view_as_list, R.drawable.ic_action_person, R.drawable.ic_action_group, R.drawable.ic_action_discard, R.drawable.ic_action_settings};
        drawerIcons = new int[]{R.drawable.ic_action_view_as_list, R.drawable.ic_action_person, R.drawable.ic_action_group};

        // get ListView defined in activity_main.xml
        drawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        //drawerListView.setAdapter(new ArrayAdapter<String>(this,
        //        R.layout.drawer_listview_item, drawerListViewItems));

        mNavAdapter = new NavListAdapter(this, drawerListViewItems, drawerIcons);
        drawerListView.setAdapter(mNavAdapter);

        // App Icon
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer_white,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // Set actionBarDrawerToggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // just styling option add shadow the right edge of the drawer
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        drawerListView.setOnItemClickListener(new DrawerItemClickListener());


    }


//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView parent, View view, int position, long id) {
//
//            if (((TextView) view).getText().toString().equals("FriendShifts")) {
//                // System.out.println("drawer soll geschlossen werden");
//                drawerLayout.closeDrawer(drawerListView);
//            }
//
//            if (((TextView) view).getText().toString().equals("Meine Shifts")) {
//                // System.out.println(" piraten history sollt jz angezeigt werden miau miau");
//                Intent myIntent = new Intent(view.getContext(), MyEntrys.class);
//                startActivity(myIntent);
//                // drawer schliessen noch rein
//                drawerLayout.closeDrawer(drawerListView);
//
//            }
//            if (((TextView) view).getText().toString().equals("Freunde")) {
//                // System.out.println(" piraten history sollt jz angezeigt werden miau miau");
//                //Intent myIntent = new Intent(view.getContext(), HistoryActivity.class);
//                //EditText editText = (EditText) findViewById(R.id.outputText);
//                //String message = editText.getText().toString();
//                //myIntent.putExtra(EXTRA_MESSAGE, message);
//                //startActivity(myIntent);
//                // drawer schliessen noch rein
//                Intent intent = new Intent();
//                intent.setClassName(getPackageName(), getPackageName() + ".FriendActivity");
//                intent.putExtra("index", "testtesttest");
//                startActivity(intent);
//
//                drawerLayout.closeDrawer(drawerListView);
//
//            }
//
//            Toast.makeText(MainActivity.this, ((TextView) view).getText(), Toast.LENGTH_LONG).show();
//
//        }
//    }

    // Listener für die Navigation Drawer Einträge - für eigenen Adapter
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                //Aktion
            } else if (position == 1) {
                Intent myIntent = new Intent(view.getContext(), MyEntrys.class);
                startActivity(myIntent);
                drawerLayout.closeDrawer(drawerListView);
            } else if (position == 2) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".FriendActivity");
                intent.putExtra("index", "testtesttest");
                startActivity(intent);
                drawerLayout.closeDrawer(drawerListView);
            }
//            else if (position == 3) {
//                // Aktion
//            }
            //else if (position == 4) {
            // Aktion
            //}

            //    Toast.makeText(MainActivity.this, mNavAdapter.getItem(position).toString(), Toast.LENGTH_LONG).show();

            drawerListView.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerListView);
        }
    }
}
