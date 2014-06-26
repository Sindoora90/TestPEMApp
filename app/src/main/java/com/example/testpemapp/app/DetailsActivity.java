package com.example.testpemapp.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


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
    ImageButton imageView;

    String selected;

    boolean mine;

    ImageButton telButton;
    ImageButton mailButton;
    ImageButton mapsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


//        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
//        PushService.setDefaultPushCallback(this, MainActivity.class);
//        ParseInstallation.getCurrentInstallation().saveInBackground();

        if (savedInstanceState == null) {

            //String bla = getIntent().getExtras().toString();

            Bundle bundle = getIntent().getExtras();
            int index = bundle.getInt("index");
            selected = bundle.getString("selected");
            objectId = bundle.getString("objectIdTest");
            mine = bundle.getBoolean("mine");

            Toast.makeText(DetailsActivity.this, "objectid: " + objectId, Toast.LENGTH_SHORT).show();

        }

        nameTextView = (TextView) findViewById(R.id.textView);
        descTextView = (TextView) findViewById(R.id.textView2);
        imageView = (ImageButton) findViewById(R.id.imageView);
        priceTextView = (TextView) findViewById(R.id.textView4);
        titleTextView = (TextView) findViewById(R.id.textView3);

        telButton = (ImageButton) findViewById(R.id.imageButton);
        mailButton = (ImageButton)findViewById(R.id.imageButton2);
        mapsButton = (ImageButton)findViewById(R.id.imageButton3);

        loadDetails();


    }

    private void loadDetails() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
        //query.whereEqualTo("title", selected);
        query.whereEqualTo("objectId", objectId);
        query.include("user");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                //TODO if parseObject!=null einfügen sonst fehler..
                title = parseObject.getString("title");
                description = parseObject.getString("description");
                name = ((ParseUser) parseObject.get("user")).getUsername();
                price = parseObject.getDouble("price");

                picFile = (ParseFile) parseObject.getParseFile("picFile");

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
                priceTextView.setText(price + "€");
            }
        });


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
        if (id == R.id.menu_load){
            loadDetails();
            Toast.makeText(DetailsActivity.this, "loadDetails(): ", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: Methoden fehlen noch...
     public void callFriend(View view){
        Toast.makeText(DetailsActivity.this, "callFriend aufgerufen: ", Toast.LENGTH_SHORT).show();

    }

    public void sendMail(View view){
        Toast.makeText(DetailsActivity.this, "sendMail aufgerufen: ", Toast.LENGTH_SHORT).show();

    }

    public void findLoc(View view){
        Toast.makeText(DetailsActivity.this, "findLoc aufgerufen: ", Toast.LENGTH_SHORT).show();

    }


}
