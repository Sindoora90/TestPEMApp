package com.example.testpemapp.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Activity zum Erstellen neuer Eintraege
 *
 */


public class NewEntryActivity extends Activity {

    private Entry entry;
    int id = 0;
    String title;
    boolean geschenk;
    double price;
    String description;
    Bitmap pic;
    ParseConnection connection;



    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        connection = new ParseConnection();

        imageView = (ImageView) findViewById(R.id.imageButton3);

        price = 0.00;
        description = "";
        //pic = R.drawable.ic_launcher; //initialisieren mit dem ic launcher aber geht nicht..


        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewEntryActivity.this, "button ok clicked", Toast.LENGTH_SHORT).show();
                createNewEntry();
            }
        });

        Button abr = (Button) findViewById(R.id.button2);
        abr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewEntryActivity.this, "button abbrechen clicked", Toast.LENGTH_SHORT).show();
                finish();
             //  abbrechen();
            }
        });


        final ImageButton geschenkButton = (ImageButton) findViewById(R.id.imageButton);
        final ImageButton kaufButton = (ImageButton) findViewById(R.id.imageButton2);
        final EditText priceEditText = (EditText) findViewById(R.id.editText2);
        geschenkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewEntryActivity.this, "image1 clicked", Toast.LENGTH_SHORT).show();
                geschenkButton.setImageResource(R.drawable.bri);
                kaufButton.setImageResource(R.drawable.ic_launcher);
                priceEditText.setText("0.00");
                priceEditText.setEnabled(false);
                geschenk = true;
                price = 0.00;

            }
        });



        kaufButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewEntryActivity.this, "image2 clicked", Toast.LENGTH_SHORT).show();
                geschenkButton.setImageResource(R.drawable.fri);
                kaufButton.setImageResource(R.drawable.bri);
                priceEditText.setEnabled(true);
                geschenk = false;

            }
        });

    }


    private void createNewEntry() {

        //EditText nameField = (EditText) findViewById(R.id.inputText);
        //String name = nameField.getText().toString();
        EditText titleText = (EditText) findViewById(R.id.titleEditText);
        title = titleText.getText().toString();

        EditText descText = (EditText)findViewById(R.id.editText3);
        description = descText.getText().toString();
        //geschenk = false;
        EditText priceText = (EditText)findViewById(R.id.editText2);
        price = Double.parseDouble(priceText.getText().toString());

        entry = new Entry(id,title, geschenk,bitmap,  price, description, ParseUser.getCurrentUser());
        Toast.makeText(NewEntryActivity.this, (CharSequence)entry.getTitle(), Toast.LENGTH_SHORT).show();


//
//        // bild muss extra gespeichert werden:
//
//        //pic = (Bitmap) ex.get("data");
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        pic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        // get byte array here
//        byte[] bytearray = stream.toByteArray();
//
//        // byte[] data = "Working at Parse is great!".getBytes();
//        byte[] data;
//        ParseFile file = new ParseFile("nameDesBildes.png", bytearray);
//        file.saveInBackground();
//
//
//        // speichern des eigentlichen Entry objekts mit einem verweis auf des bild
//        ParseObject newEntry = new ParseObject("Entry");
//        newEntry.put("title", title);
//        newEntry.put("geschenk", geschenk);
//        //newEntry.put("bitmap", pic);
//        newEntry.put("price", price);
//        newEntry.put("description", description);
//        newEntry.put("user", ParseUser.getCurrentUser());
//        newEntry.put("picFile", file);
//        newEntry.saveInBackground();

        connection.createNewEntry(pic, title, geschenk, price, description);




//
//        // mit dem eigtl ParseObject verbinden also mit dem Entry
//        ParseObject entry = new ParseObject("Entry");
//        entry.put("title", title);
//        entry.put("picFile", file);
//        entry.saveInBackground();



        // lokal speichern:
        //ParseObject gameScore = new ParseObject("Entry");
        //gameScore.put("title", title);
        //gameScore.pinInBackground(new SaveCallback() {
        //    public void done(ParseException e) {
                // Handle success or failure here ...
        //    }});

        //Toast.makeText(NewEntryActivity.this, "ID: " + newEntry.getObjectId(), Toast.LENGTH_SHORT).show();
        //System.out.println("ID newentry: " + newEntry.getObjectId());
        //System.out.println("ID gamescore: " + gameScore.getObjectId());



        this.finish();
        //funzt bis hier
    }

    public void onImageButtonClick(View View) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);

                imageView.setImageBitmap(bitmap);
                pic = bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_entry);
//
//
//        Button b = (Button) findViewById(R.id.button);
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(NewEntryActivity.this, "button ok clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_entry, menu);
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
