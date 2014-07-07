package com.example.testpemapp.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.FileNotFoundException;
import java.io.InputStream;


public class NewEntryActivity extends Activity {

    // fuer die cam:
    final static String DEBUG_TAG = "NewEntryActivity";
    private Camera camera;
    private int cameraId = 0;


    private Entry entry;
    String id = "";
    String title = "";
    boolean geschenk;
    double price;
    String description;
    Bitmap pic;
    ParseConnection connection;

    boolean mine;
    String selected;
    String objectId;

    String name;

    ParseFile picFile;
    TextView titleTextView;
    TextView nameTextView;
    TextView descTextView;
    TextView priceTextView;
    //ImageView imageView;

    boolean cameraSelected;

    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    private ImageButton imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        connection = new ParseConnection();

        imageView = (ImageButton) findViewById(R.id.imageButton3);

        price = 0.0;
        description = "";

        // mit Bild initialisieren - aktuell des Logo falls man kein Bild angibt
        pic = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        // falls es von MyEntrys kommt..
        if (savedInstanceState == null) {

            //String bla = getIntent().getExtras().toString();

            Bundle bundle = getIntent().getExtras();
            int index = bundle.getInt("index");
            selected = bundle.getString("selected");
            mine = bundle.getBoolean("mine");

            // Toast.makeText(NewEntryActivity.this, "selected: " + selected, Toast.LENGTH_SHORT).show();

        }

        // fuer die Cam:
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);

            }
        }


        imageView = (ImageButton) findViewById(R.id.imageButton3);
        if (mine) {

            //nameTextView = (TextView)findViewById(R.id.textView);
            descTextView = (TextView) findViewById(R.id.editText3);
            imageView = (ImageButton) findViewById(R.id.imageButton3);
            priceTextView = (TextView) findViewById(R.id.editText2);
            titleTextView = (TextView) findViewById(R.id.titleEditText);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
            query.whereEqualTo("title", selected);
            query.include("user");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
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
                    // nameTextView.setText(name);
                    titleTextView.setText(title);
                    descTextView.setText(description);
                    priceTextView.setText(Double.toString(price));
                    objectId = parseObject.getObjectId();
                }
            });

        }


        // fuer floating menu
        registerForContextMenu(imageView);
        // click auf imageView->contextmenu oeffnet sich:
        imageView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
            }
        });


        ImageButton b = (ImageButton) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // falls nur update gemacht werden soll...
                if (mine) {

                    updateEntry(objectId);
                } else {

                    createNewEntry();
                }
            }
        });

        ImageButton abr = (ImageButton) findViewById(R.id.button2);
        abr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final ImageButton geschenkButton = (ImageButton) findViewById(R.id.imageButton);
        //final ImageButton kaufButton = (ImageButton) findViewById(R.id.imageButton2);
        final EditText priceEditText = (EditText) findViewById(R.id.editText2);
        geschenkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (geschenk == false) {
                    priceEditText.setText("0.00");
                    priceEditText.setEnabled(false);
                    geschenk = true;
                    price = 0.0;
                } else {
                    geschenk = false;
                    priceEditText.setEnabled(true);
                }

            }
        });

    }

    // fuer die Cam:
    public void onClick() {

        camera.takePicture(null, null,
                new PhotoHandler(getApplicationContext()));

    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Suche Cam
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }


    private void updateEntry(String objectId) {
        // updated den vorhandenen entry...
        //EditText nameField = (EditText) findViewById(R.id.inputText);
        //String name = nameField.getText().toString();
        EditText titleText = (EditText) findViewById(R.id.titleEditText);
        title = titleText.getText().toString();

        EditText descText = (EditText) findViewById(R.id.editText3);
        description = descText.getText().toString();
        //geschenk = false;
        EditText priceText = (EditText) findViewById(R.id.editText2);
        price = Double.parseDouble(priceText.getText().toString());

        entry = new Entry(id, title, geschenk, bitmap, price, description, ParseUser.getCurrentUser());
        Toast.makeText(NewEntryActivity.this, (CharSequence) entry.getTitle(), Toast.LENGTH_SHORT).show();

        connection.updateEntry(objectId, pic, title, geschenk, price, description);
        this.finish();
    }


    private void createNewEntry() {

        //EditText nameField = (EditText) findViewById(R.id.inputText);
        //String name = nameField.getText().toString();
        EditText titleText = (EditText) findViewById(R.id.titleEditText);
        title = titleText.getText().toString();

        EditText descText = (EditText) findViewById(R.id.editText3);
        description = descText.getText().toString();
        //geschenk = false;
        EditText priceText = (EditText) findViewById(R.id.editText2);
        if (!priceText.getText().toString().equals("")) {
            price = Double.parseDouble(priceText.getText().toString());
        } else {
            price = 0.0;
        }
        entry = new Entry(id, title, geschenk, bitmap, price, description, ParseUser.getCurrentUser());


        if (!title.equals("")) {
            Toast.makeText(NewEntryActivity.this, (CharSequence) entry.getTitle() + " wurde hinzugefügt!", Toast.LENGTH_SHORT).show();
            connection.createNewEntry(pic, title, geschenk, price, description);
            this.finish();

        } else {
            Toast.makeText(NewEntryActivity.this, "Bitte füge einen Titel ein!", Toast.LENGTH_SHORT).show();

        }

    }

    public void onImageButtonClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (cameraSelected) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bp);
                pic = bp;
            } catch (Exception e) {

            }


        } else {
            InputStream stream = null;
            if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
                try {

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
    }


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


    // fuer floating context menu:

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.actions, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.menu_item_camera:

                cameraSelected = true;
                open();
                break;

            case R.id.menu_item_galerie:
                cameraSelected = false;
                onImageButtonClick();
                break;

        }
        return true;
    }

    public void open() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

}
