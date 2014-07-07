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
//sdlhfljar
//        // fuer lokalen speicher
//        Parse.enableLocalDatastore(this.getApplicationContext());
//
//        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
//        PushService.setDefaultPushCallback(this, MainActivity.class);
//        ParseInstallation.getCurrentInstallation().saveInBackground();

        connection = new ParseConnection();

        lv = (ListView) findViewById(R.id.listView);
        createNavDrawer();

//        // beim allerersten Start soll die login activity angezeigt werden, nach der registrierung nicht mehr
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        System.out.println("parse user: " + ParseUser.getCurrentUser());
//        if (currentUser != null) {
//            // do stuff with the user
//            Toast.makeText(MainActivity.this, "user!=null", Toast.LENGTH_SHORT).show();
//
//
//            // NavDrawer:
//
//            // createNavDrawer();
//
//
        loadEntrys();
//
//
//        } else {
//            // show the signup or login screen
//
//            Intent intent = new Intent();
//            intent.setClassName(getPackageName(), getPackageName() + ".SignUpActivity");
//            //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
//            startActivity(intent);
//            Toast.makeText(MainActivity.this, "LOGIN ANGEZEIGT", Toast.LENGTH_SHORT).show();
//
//            //TODO nach dem registrieren -> leere Main mit nachricht freunde hinzufügen..
//
//        }


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

                intent.setClassName(getPackageName(), getPackageName() + ".NewEntryActivity");
                //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                intent.putExtra("index", 0);
                //gibt title mit
                intent.putExtra("selected", "");
                intent.putExtra("mine", false);

                startActivity(intent);
                //    Toast.makeText(MainActivity.this, "new clicked", Toast.LENGTH_SHORT).show();
                //System.out.println("test");
                return true;

            case R.id.menu_load:

                loadEntrys();
                //   Toast.makeText(MainActivity.this, "load", Toast.LENGTH_SHORT).show();

            case R.id.action_settings:
                // settings item was selected
                //    Toast.makeText(MainActivity.this, "settings clicked", Toast.LENGTH_SHORT).show();
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


//    protected void onResume(){
//        loadEntrys();
//
//    }


    // TODO fehler raus: wenn keine freunde werden eigene einträge auch nicht angezeigt bzw wenn man was löscht zeigt es die teils trotzdem noch an
    private void loadEntrys() {

        //connection.getAllEntrys();

        // 1. get all friends:
        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("Friendship");
        innerQuery.include("user");
        innerQuery.whereEqualTo("fromUser", ParseUser.getCurrentUser());

        innerQuery.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> friendlist, ParseException e) {
                // comments now contains the comments for posts with images.

                if (e == null ) {

                    //TODO statt array arraylist verwenden (wie bei FriendActivity auch)
                    ParseUser[] names = new ParseUser[friendlist.size() + 1];
                    names[0] = ParseUser.getCurrentUser();

                    // test mit arraylist
                    //ArrayList<ParseUser> friendnames = new ArrayList<ParseUser>();
                    //friendnames.add(ParseUser.getCurrentUser());

                    if (friendlist.size() > 0) {


                        for (int i = 0; i < friendlist.size(); i++) {
                            names[i + 1] = (ParseUser) friendlist.get(i).get("toUser");
                            //test mit arraylist
                            //friendnames.add((ParseUser) friendlist.get(i).get("toUser"));

                            System.out.println("friendsID: " + names[i]);

                            ParseQuery<ParseObject> queryl = ParseQuery.getQuery("Entry");
                            queryl.include("user");
                            queryl.orderByDescending("createdAt");
                            queryl.whereContainedIn("user", Arrays.asList(names));

                            //test mit arrayList
                            //queryl.whereContainedIn("user", friendnames);

                            queryl.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> scoreList, ParseException e) {
                                    // comments now contains the comments for posts with images.

                                    if (e == null) {
                                        if (scoreList.size() > 0) {
//                                        Log.d("score", "Retrieved " + commentList.size() + "entrys");
//
//                                        for (int i = 0; i < commentList.size(); i++) {
//                                            // Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){
//
//                                            System.out.println("title: " + commentList.get(i).getString("title"));
//                                        }

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
                    }
                    else{
                        System.out.println("test wenn keine freunde mehr dann soll des aufgerufen werden: ");



                        //for (int i = 0; i < friendlist.size(); i++) {
                          //  names[i + 1] = (ParseUser) friendlist.get(i).get("toUser");
                            //test mit arraylist
                            //friendnames.add((ParseUser) friendlist.get(i).get("toUser"));

                            //System.out.println("friendsID: " + names[i]);

                            ParseQuery<ParseObject> queryl = ParseQuery.getQuery("Entry");
                            queryl.include("user");
                            queryl.orderByDescending("createdAt");
                            queryl.whereEqualTo("user", names[0]);

                            //test mit arrayList
                            //queryl.whereContainedIn("user", friendnames);

                            queryl.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> scoreList, ParseException e) {
                                    // comments now contains the comments for posts with images.

                                    if (e == null) {
                                        if (scoreList.size() > 0) {
//                                        Log.d("score", "Retrieved " + commentList.size() + "entrys");
//
//                                        for (int i = 0; i < commentList.size(); i++) {
//                                            // Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){
//
//                                            System.out.println("title: " + commentList.get(i).getString("title"));
//                                        }

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

                      //  }

                    }
                }



            }
        });

//        //   innerQuery.whereExists("image");
//        ParseQuery<ParseObject> queryl = ParseQuery.getQuery("Entry");
//        queryl.whereMatchesQuery("user", innerQuery);
//
//        //String[] names = {"Jonathan Walsh", "Dario Wunsch", "Shawn Simon"};
//        //queryl.whereContainedIn("playerName", Arrays.asList(names));
//
//        queryl.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> commentList, ParseException e) {
//                // comments now contains the comments for posts with images.
//
//                if (e == null) {
//                    if (commentList.size() > 0) {
//                        Log.d("score", "Retrieved " + commentList.size() + "entrys");
//
//                        for (int i = 0; i < commentList.size(); i++) {
//                            // Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){
//
//                            System.out.println("title: " + commentList.get(i).getString("title"));
//                        }
//                    } else {
//                        Log.d("fehler in query not parse", "hat wieder nicht geklappt :(");
//                    }
//                } else {
//                    Log.d("fehler", "hat nicht geklappt");
//
//                }
//            }
//        });

//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
//        query.include("user");
//        query.orderByDescending("createdAt");
//// TODO: letzen 10 anzeigen:
////        query.setLimit(10);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> scoreList, ParseException e) {
//                if (e == null && scoreList.size() > 0) {
//                    Log.d("score", "Retrieved " + scoreList.size() + " scores");
//                    //size = scoreList.size()-1;
//                    entrys = new Entry[scoreList.size()];
//                    titleArray = new String[scoreList.size()];
//                    Log.d("entry array size: ", "size of entry array:" + entrys.length);
//                    Entry entry;
//                    for (int i = 0; i < scoreList.size(); i++) {
//                        // Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){
//
//                        ParseFile picFile = (ParseFile) scoreList.get(i).getParseFile("picFile");
//
//
//                        picFile.getDataInBackground(new GetDataCallback() {
//                            public void done(byte[] data,
//                                             ParseException e) {
//                                if (e == null) {
//                                    pic = BitmapFactory.decodeByteArray(data, 0, data.length);
//
//                                } else {
//                                    Log.d("test",
//                                            "There was a problem downloading the data.");
//                                }
//                            }
//                        });
//
//
//                        try {
//                            entry = new Entry(scoreList.get(i).getObjectId(), scoreList.get(i).getString("title"), scoreList.get(i).getBoolean("geschenk"), BitmapFactory.decodeByteArray(scoreList.get(i).getParseFile("picFile").getData(), 0, scoreList.get(i).getParseFile("picFile").getData().length), scoreList.get(i).getDouble("price"), scoreList.get(i).getString("description"), (ParseUser) scoreList.get(i).get("user"));
//
//                            Log.d("entry", entry.toString());
//                            titleArray[i] = scoreList.get(i).getString("title");
//                            entrys[i] = entry;
//                        } catch (ParseException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//
//                    MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), titleArray, entrys);
//                    lv.setAdapter(adapter);
//                    Toast.makeText(MainActivity.this, "liste erzeugt ", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    Log.d("score", "Error: " + e.getMessage());
//                    Toast.makeText(MainActivity.this, "kein internet... ", Toast.LENGTH_SHORT).show();
//
//                }
//                Log.d("allentrys: ", "Entry array dass an main geschickt werden soll: " + entrys);
//
//
//            }
//        });
    }


    private void createNavDrawer() {

        // get list items from strings.xml
        drawerListViewItems = getResources().getStringArray(R.array.items);



        //drawer list icons
        //drawerIcons = new int[] {R.drawable.ic_action_view_as_list, R.drawable.ic_action_person, R.drawable.ic_action_group, R.drawable.ic_action_discard, R.drawable.ic_action_settings};
        drawerIcons = new int[]{R.drawable.ic_action_view_as_list, R.drawable.ic_action_person, R.drawable.ic_action_group, R.drawable.ic_action_settings};

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
            } else if (position == 3) {
                // Aktion
            }
            //else if (position == 4) {
            // Aktion
            //}

            //    Toast.makeText(MainActivity.this, mNavAdapter.getItem(position).toString(), Toast.LENGTH_LONG).show();

            drawerListView.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerListView);
        }
    }
}
