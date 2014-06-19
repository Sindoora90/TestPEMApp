package com.example.testpemapp.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import java.util.List;


// es sollen alle freunde angezeigt werden in einer einfachen liste

public class FriendActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        Import_contacts_from_address_book();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend, menu);
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




    // es müssen alle contacts hochgeladen werden als tabelle: name + telefonnummer
    // dann müssen die beiden tabellen verglichenc werden also contacts und user
    // dann müssen contact + userID zurückgegeben werden...

    // nur zum testen, muss nachher wieder raus weils in login kommt damit es nur einmal passiert!!!
    int countContact;
    TextView t1;
    TextView t2;

    private void Import_contacts_from_address_book() {

        t1 = (TextView)findViewById(R.id.textView);
        t2 = (TextView)findViewById(R.id.textView3);

        ParseQuery<ParseUser> query2 = ParseUser.getQuery();
        query2.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful. you got all users in a List<ParseUser>
                } else {
                    // Something went wrong.
                }
            }
        });



        // TODO Auto-generated method stub
        String phoneNumber = null;
        String email = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PHONECONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONECONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EMAILCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EMAILCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String EMAIL = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output = new StringBuffer();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        System.out.println("---------------------->"+cursor.getCount());
        if(cursor.getCount() >0){
            countContact = cursor.getCount();
            while(cursor.moveToNext()){
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));


                //------------------- Catch phone number, neu phone >0 thi get phone, ko thi lam chuyen khac
                //if(hasPhoneNumber > 0 ){
                output.append("\nFirst Name: "+name);
                // Query and loop for every phone number of the contact
                Cursor phoneCursor = contentResolver.query(PHONECONTENT_URI, null, PHONECONTACT_ID + "=?", new String[]{contact_id}, null);
                while(phoneCursor.moveToNext()){
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    output.append("\n Phone number: "+phoneNumber);
                }
                phoneCursor.close();
                // Query and loop for every email of the contact

                Cursor emailCurosr = contentResolver.query(EMAILCONTENT_URI, null, EMAILCONTACT_ID+"=?",new String[]{contact_id},null);
                while(emailCurosr.moveToNext()){
                    email = emailCurosr.getString(emailCurosr.getColumnIndex(EMAIL));
                    output.append("\nEmail: "+email);
                }
                emailCurosr.close();
                //}
                output.append("\n");


            }
            cursor.close();
        }
        //txtViewContactsInfor.setText("Contacts: "+String.valueOf(countContact));
        //outputText.setText(output.toString());

        Toast.makeText(FriendActivity.this, "Contacts: " + String.valueOf(countContact), Toast.LENGTH_SHORT).show();

        Toast.makeText(FriendActivity.this, output.toString(), Toast.LENGTH_SHORT).show();



        // speichern des eigentlichen Contacts objekts mit Verweis zu sich selbst
        ParseObject newContact = new ParseObject("Friend");
        //newContact.put("email", email);
        newContact.put("user", ParseUser.getCurrentUser());
       // newContact.put("with", friendname);
        newContact.saveInBackground();


        // hole daten und vergleiche:

        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("Contact");
        innerQuery.whereExists("nummer");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Contact");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> commentList, ParseException e) {
                // comments now contains the comments for posts with images.


            }
        });





        t1.setText("Contacts: " + String.valueOf(countContact));
        t2.setText(output.toString());


    }

}
