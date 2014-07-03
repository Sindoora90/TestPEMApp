package com.example.testpemapp.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


// to do :
// - laenger auf ein listitem druecken -> loeschen option, oder wischen = loeschen?
// - einzelne items in detailview dann bearbeiten koennen

public class MyEntrys extends Activity {

    Bitmap pic;
    private      ListView lv;
//    ListAdapter adapter;
    MySimpleArrayAdapter adapter;

    Entry[] entrys;
    String[] titleArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_entrys);

        // test test test 

//        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
//        PushService.setDefaultPushCallback(this, MainActivity.class);
//        ParseInstallation.getCurrentInstallation().saveInBackground();

        lv = (ListView)findViewById(R.id.listView);

        loadEntrys();

    }

    boolean removed;
    private void loadEntrys() {

        //removed = false;

        //ParseQuery die nur meine Entrys zurueckgeben:
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
        query.include("user");
        query.orderByDescending("createdAt");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> scoreList, ParseException e) {
                if (e == null && scoreList.size()>0) {
                    Log.d("score", "Retrieved " + scoreList.size() + " scores");
                    entrys = new Entry[scoreList.size()];
                    titleArray = new String[scoreList.size()];
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
                            entry = new Entry(scoreList.get(i).getObjectId(), scoreList.get(i).getString("title"), scoreList.get(i).getBoolean("geschenk"), BitmapFactory.decodeByteArray(scoreList.get(i).getParseFile("picFile").getData(), 0, scoreList.get(i).getParseFile("picFile").getData().length), scoreList.get(i).getDouble("price"), scoreList.get(i).getString("description"), (ParseUser)scoreList.get(i).get("user"));

                            Log.d("entry", entry.toString());
                          //  Toast.makeText(MyEntrys.this, "current parseuser: " + ParseUser.getCurrentUser(), Toast.LENGTH_SHORT).show();
                            titleArray[i] = scoreList.get(i).getString("title");
                            entrys[i] = entry;
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    ArrayList a = new ArrayList<String> (titleArray.length);
                    for (String s : titleArray) {
                        a.add(s);
                    }
                    ArrayList b = new ArrayList<Entry>(entrys.length);
                    for (Entry s : entrys) {
                        b.add(s);
                    }
                    adapter = new MySimpleArrayAdapter(getApplicationContext(), a, b);
                    lv.setAdapter(adapter);
                 //   Toast.makeText(MyEntrys.this, "liste erzeugt ", Toast.LENGTH_SHORT).show();

                    // Create a ListView-specific touch listener. ListViews are given special treatment because
                    // by default they handle touches for their list items... i.e. they're in charge of drawing
                    // the pressed state (the list selector), handling list item clicks, etc.
                    SwipeDismissListViewTouchListener touchListener =
                            new SwipeDismissListViewTouchListener(
                                    lv,
                                    new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                        @Override
                                        public boolean canDismiss(int position) {
                                            return true;
                                        }

                                        @Override
                                        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                            for (int position : reverseSortedPositions) {

                                                // TODO: aus parse löschen:

                                                scoreList.get(position).deleteInBackground();

                                                scoreList.get(position).deleteInBackground(new DeleteCallback()
                                                {
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            // myObjectSavedSuccessfully();

                                                        //  removed = true;
                                                        } else {
                                                            // myObjectSaveDidNotSucceed();
                                                            System.out.println("es wurde nicht gelöscht");
                                                        }
                                                    }
                                                });
//
//                                                while(!removed){
//
//                                                }
                                                adapter.remove(adapter.getItem(position));
                                                //removed = false;

                                            }
                                            //loadEntrys();
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                    lv.setOnTouchListener(touchListener);
                    // Setting this scroll listener is required to ensure that during ListView scrolling,
                    // we don't look for swipes.
                    lv.setOnScrollListener(touchListener.makeScrollListener());

//                    // Set up normal ViewGroup example
//                    final ViewGroup dismissableContainer = (ViewGroup) findViewById(R.id.dismissable_container);
//                    for (int i = 0; i < items.length; i++) {
//                        final Button dismissableButton = new Button(this);
//                        dismissableButton.setLayoutParams(new ViewGroup.LayoutParams(
//                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        dismissableButton.setText("Button " + (i + 1));
//                        dismissableButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(MyEntrys.this,
//                                        "Clicked " + ((Button) view).getText(),
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        // Create a generic swipe-to-dismiss touch listener.
//                        dismissableButton.setOnTouchListener(new SwipeDismissTouchListener(
//                                dismissableButton,
//                                null,
//                                new SwipeDismissTouchListener.DismissCallbacks() {
//                                    @Override
//                                    public boolean canDismiss(Object token) {
//                                        return true;
//                                    }
//
//                                    @Override
//                                    public void onDismiss(View view, Object token) {
//                                        dismissableContainer.removeView(dismissableButton);
//                                    }
//                                }));
//                        dismissableContainer.addView(dismissableButton);
//                    }



                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                        {
                            // entweder detailview erst aufrufen oder gleich die newEntry mit den fertigen werten.. ?
                            // bzw des parseobjekt an die newEntry über intent mitgeben ?
                            Intent intent = new Intent();
                            intent.setClassName(getPackageName(), getPackageName()+".NewEntryActivity");
                            intent.putExtra("index", arg2);
                            //gibt title mit
                            intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                            intent.putExtra("mine", true); // falls es sich um meine eigenen handelt muss detailview bescheid wissen

                            //parseobject wenns geht
                            //intent.putExtra("object", lv.getAdapter().getItem(arg2).toString());
                            startActivity(intent);


                            //test änderung

                        }
                    });



                } else {
                    // TODO falls liste leer und man noch keine einträge hat -> aktuell fehler
                    //Log.d("score", "Error: " + e.getMessage());
               //     Toast.makeText(MyEntrys.this, "kein internet... ", Toast.LENGTH_SHORT).show();


                }
                Log.d("allentrys: ", "Entry array dass an main geschickt werden soll: " + entrys);


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_entrys, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
