package com.example.testpemapp.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;


public class DetailsActivity extends Activity {


    String objectId;
    String title;
    String description;
    String name;
    double price;


    ParseFile picFile;
    TextView titleTextView;
    TextView nameTextView;
     TextView descTextView;
    TextView priceTextView;
    ImageView imageView;

    String selected;

    boolean mine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        if (savedInstanceState == null) {

            //String bla = getIntent().getExtras().toString();

            Bundle bundle = getIntent().getExtras();
            int index = bundle.getInt("index");
             selected = bundle.getString("selected");
            objectId = bundle.getString("objectIdTest");
            mine = bundle.getBoolean("mine");

            //Toast.makeText(DetailsActivity.this, "selected: " + selected, Toast.LENGTH_SHORT).show();
            Toast.makeText(DetailsActivity.this, "objectid: " + objectId, Toast.LENGTH_SHORT).show();

        }


        nameTextView = (TextView)findViewById(R.id.textView);
        descTextView = (TextView)findViewById(R.id.textView2);
        imageView = (ImageView)findViewById(R.id.imageView);
        priceTextView = (TextView)findViewById(R.id.textView4);
        titleTextView = (TextView)findViewById(R.id.textView3);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
        //query.whereEqualTo("title", selected);
        query.whereEqualTo("objectId", objectId);
        query.include("user");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                title = parseObject.getString("title");
                description = parseObject.getString("description");
                name = ((ParseUser)parseObject.get("user")).getUsername();
                price = parseObject.getDouble("price");

                picFile = (ParseFile)parseObject.getParseFile("picFile");

                picFile.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data,
                                     ParseException e) {
                        if (e == null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            imageView.setImageBitmap(bmp);
                        } else {
                            Log.d("test", "There was a problem downloading the data.");
                        }
                    }
                });
                nameTextView.setText(name);
                titleTextView.setText(title);
                descTextView.setText(description);
                priceTextView.setText("Preis: " + price + "€");
            }
        });


        // local query
//        ParseQuery<ParseObject> queryloc = ParseQuery.getQuery("GameScore");
//        queryloc.fromLocalDatastore();
//        queryloc.getInBackground("u8hFD8wrv0", new GetCallback<ParseObject>() {
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    // object will be your game score
//                    title = object.getString("title");
//                } else {
//                    // something went wrong
//                    Toast.makeText(DetailsActivity.this, "something went wrong 2", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });


        //descTextView.setText(description);

        Toast.makeText(DetailsActivity.this, "parse titel angepasst", Toast.LENGTH_SHORT).show();





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
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
