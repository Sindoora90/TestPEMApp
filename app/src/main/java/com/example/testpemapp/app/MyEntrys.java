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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


// to do :
// - laenger auf ein listitem druecken -> loeschen option, oder wischen = loeschen?
// - einzelne items in detailview dann bearbeiten koennen

public class MyEntrys extends Activity {

    Bitmap pic;
    private      ListView lv;
    ListAdapter adapter;

    Entry[] entrys;
    String[] titleArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_entrys);

        // test test test 


        lv = (ListView)findViewById(R.id.listView);


        //ParseQuery die nur meine Entrys zurueckgeben:
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
        query.include("user");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
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
                            entry = new Entry(i, scoreList.get(i).getString("title"), scoreList.get(i).getBoolean("geschenk"), BitmapFactory.decodeByteArray(scoreList.get(i).getParseFile("picFile").getData(), 0, scoreList.get(i).getParseFile("picFile").getData().length), scoreList.get(i).getDouble("price"), scoreList.get(i).getString("description"), (ParseUser)scoreList.get(i).get("user"));

                            Log.d("entry", entry.toString());
                            Toast.makeText(MyEntrys.this, "current parseuser: " + ParseUser.getCurrentUser(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MyEntrys.this, "liste erzeugt ", Toast.LENGTH_SHORT).show();


            }
        });


// test 2 test 2 test 2


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // entweder detailview erst aufrufen oder gleich die newEntry mit den fertigen werten.. ?
                // bzw des parseobjekt an die newEntry über intent mitgeben ?
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName()+".DetailsActivity");
                intent.putExtra("index", arg2);
                //gibt title mit
                intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                intent.putExtra("mine", true); // falls es sich um meine eigenen handelt muss detailview bescheid wissen

                //parseobject wenns geht
                intent.putExtra("object", lv.getAdapter().getItem(arg2).toString());
                startActivity(intent);


                //test änderung

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
