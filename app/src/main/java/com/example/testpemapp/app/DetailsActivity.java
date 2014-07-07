package com.example.testpemapp.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Locale;


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

    MapFragment mMapFragment;
    private GoogleMap mMap;
    // Create a LatLngBounds that includes Australia.
    private LatLngBounds AUSTRALIA = new LatLngBounds(
            new LatLng(-44, 113), new LatLng(-10, 154));
    static final LatLng TutorialsPoint = new LatLng(21 , 57);
    private static final LatLng MUNICH = new LatLng(48.1, 11.6);
    RelativeLayout mapLayout;
    private static LatLng test;
    private String adress;

    boolean mine;

    ImageButton telButton;
    ImageButton mailButton;
    ImageButton mapsButton;
    private Location myLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMapFragment = (MapFragment) getFragmentManager().
                findFragmentById(R.id.map);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(mMapFragment);
        ft.commit();


        if (savedInstanceState == null) {

            Bundle bundle = getIntent().getExtras();
            int index = bundle.getInt("index");
            selected = bundle.getString("selected");
            objectId = bundle.getString("objectIdTest");
            mine = bundle.getBoolean("mine");

        }

        nameTextView = (TextView) findViewById(R.id.textView);
        descTextView = (TextView) findViewById(R.id.textView2);
        imageView = (ImageButton) findViewById(R.id.imageView);
        priceTextView = (TextView) findViewById(R.id.textView4);
        titleTextView = (TextView) findViewById(R.id.textView3);
        geschenkView = (ImageView)findViewById(R.id.imageView2);
        telButton = (ImageButton) findViewById(R.id.imageButton);
        telButton.setEnabled(false);
        mailButton = (ImageButton)findViewById(R.id.imageButton2);
        mailButton.setEnabled(false);
        mapsButton = (ImageButton)findViewById(R.id.imageButton3);
        mapsButton.setEnabled(false);

        loadDetails();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                adress = ((ParseUser) parseObject.get("user")).getString("adr");
                System.out.println(adress);

                price = parseObject.getDouble("price");

                picFile = (ParseFile) parseObject.getParseFile("picFile");

                picFile.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data,
                                     ParseException e) {
                        if (e == null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            // zwei zeilen drunter neu
                            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bmp, 700, 300);
                            bmp = ThumbImage;
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
                telButton.setEnabled(true);
                mailButton.setEnabled(true);
                mapsButton.setEnabled(true);

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
          //  Toast.makeText(DetailsActivity.this, "loadDetails(): ", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: Methoden fehlen noch...
     public void callFriend(View view){

         new AlertDialog.Builder(this)
                 .setTitle("Anruf")
                 .setMessage("Möchtest Du wirklich anrufen?")
                 .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         // Toast.makeText(DetailsActivity.this, "callFriend aufgerufen: ", Toast.LENGTH_SHORT).show();
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
                                     "Call failed, please try again later.", Toast.LENGTH_SHORT).show();
                         }
                     }
                 })
                 .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         // do nothing
                     }
                 })
                 .setIcon(android.R.drawable.ic_dialog_alert)
                 .show();



    }

    public void sendMail(View view){
      //  Toast.makeText(DetailsActivity.this, "sendMail aufgerufen: ", Toast.LENGTH_SHORT).show();
        Log.i("Send email", "");

        String[] TO = {entryUser.getEmail()};
       // String[] TO = {"badri90@gmx.de"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FriendShift Angebot: " + title);
        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi ich hätte Interesse an dein Angebot: " + title);

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
        //    Toast.makeText(DetailsActivity.this, "button maps clicked", Toast.LENGTH_SHORT).show();

        setUpMapIfNeeded();
        //   Toast.makeText(DetailsActivity.this, "done", Toast.LENGTH_SHORT).show();

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setUpMapIfNeeded() {
        Context context = getApplicationContext();

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> geoResults = geocoder.getFromLocationName(adress, 1);

            while (geoResults.size()==0) {
                System.out.println(adress +"failed");
                Toast.makeText(DetailsActivity.this, "Adress "+ adress + " could not be found", Toast.LENGTH_LONG).show();
                adress = "München"; //München ist default falls eigegebene Adresse unlesbar ist
                geoResults = geocoder.getFromLocationName(adress, 1);

            }

            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                test = new LatLng (addr.getLatitude(),addr.getLongitude());
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }


        try {


            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.show(mMapFragment);
            ft.commit();

            //Close Button in front of map

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            params.weight = 1.0f;
            params.gravity= Gravity.RIGHT;

            params.setMargins(10, 20, 30, 40);

            //close.setLayoutParams(params);

            final Button close = new Button(this);
            //close.setText("Close");
            Drawable d= getResources().getDrawable(R.drawable.deletenew2);
            close.setBackground(d);
            close.setGravity(Gravity.RIGHT);
            close.setLayoutParams(params);
            addContentView(close, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    close.setVisibility(View.GONE);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.hide(mMapFragment);
                    ft.commit();

                }
            });

            if(mMap==null){

                mMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();

                if(mMap!=null){

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Marker marker = mMap.addMarker(new MarkerOptions().
                            position(test).title(adress));
                    mMap.addCircle(new CircleOptions().center(test).fillColor(Color.argb(80, 145, 237, 184)).radius(7000).strokeWidth(1));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(test));

                    // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(test)      // Sets the center of the map to Mountain View
                            .zoom(10)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
