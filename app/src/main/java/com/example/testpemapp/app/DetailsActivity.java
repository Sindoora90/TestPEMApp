package com.example.testpemapp.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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


    Person entryUser;

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
    ImageView geschenkView;

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
        geschenkView = (ImageView)findViewById(R.id.imageView2);
        telButton = (ImageButton) findViewById(R.id.imageButton);
        mailButton = (ImageButton)findViewById(R.id.imageButton2);
        mapsButton = (ImageButton)findViewById(R.id.imageButton3);

        loadDetails();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailsActivity.this, "button abbrechen clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName()+".ImageFullscreenActivity");
                intent.putExtra("index", "testtesttest");
                intent.putExtra("objectIdTest", objectId);
                startActivity(intent);
            }
        });

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

                ParseUser user = (ParseUser) parseObject.get("user");
                //String id, String name, String telnr, String email, String adresse
                //TODO keys überprüfen...
                entryUser = new Person(user.getObjectId(), user.getUsername(), user.getString("phone"),user.getEmail(), user.getString("adr"));
                System.out.println("entryUser email: " + entryUser.getEmail());
                System.out.println("entryUser telnr: " + entryUser.getTelnr());

                if(price == 0.0){
                    priceTextView.setVisibility(View.GONE);
                }else{
                    priceTextView.setText( price + "€");
                    geschenkView.setVisibility(View.GONE);
                }
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
         Log.i("Make call", "");

         Intent phoneIntent = new Intent(Intent.ACTION_CALL);
         String tel = entryUser.getTelnr();
         phoneIntent.setData(Uri.parse("tel:"+tel));

         try {
             startActivity(phoneIntent);
            // finish();
             Log.i("Finished making a call...", "");
         } catch (android.content.ActivityNotFoundException ex) {
             Toast.makeText(DetailsActivity.this,
                     "Call faild, please try again later.", Toast.LENGTH_SHORT).show();
         }

    }

    public void sendMail(View view){
        Toast.makeText(DetailsActivity.this, "sendMail aufgerufen: ", Toast.LENGTH_SHORT).show();
        Log.i("Send email", "");

        String[] TO = {entryUser.getEmail()};
       // String[] TO = {"badri90@gmx.de"};
       // String[] CC = {"mcmohd@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FriendShift");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi ich hätte Interesse an dein Angebot: " + title);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(DetailsActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }


    }

    public void findLoc(View view){
        Toast.makeText(DetailsActivity.this, "findLoc aufgerufen: ", Toast.LENGTH_SHORT).show();

       // mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
       //        .getMap();

    }


}
