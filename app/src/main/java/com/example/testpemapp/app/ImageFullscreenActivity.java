package com.example.testpemapp.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class ImageFullscreenActivity extends Activity {


    String objectId;
    String name;
    Bitmap bmp;
    ParseFile picFile;
    CustomImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        pic = (CustomImageView)findViewById(R.id.customImageVIew1);

        Bundle bundle = getIntent().getExtras();
        objectId = bundle.getString("objectIdTest");

        loadImage();
    }


    private void loadImage() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
        query.whereEqualTo("objectId", objectId);
        query.include("user");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                picFile = (ParseFile) parseObject.getParseFile("picFile");

                picFile.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data,
                                     ParseException e) {
                        if (e == null) {
                            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bmp, 50, 50);
                            bmp = ThumbImage;
                            pic.setImageBitmap(bmp);
                        } else {
                            Log.d("test", "There was a problem downloading the data.");
                        }
                    }
                });
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_fullscreen, menu);
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
