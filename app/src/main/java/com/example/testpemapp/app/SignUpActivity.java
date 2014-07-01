package com.example.testpemapp.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends Activity {

    EditText name;
    EditText email;
    EditText pw;
    EditText phone;
    EditText adr;
    TextView description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
//        PushService.setDefaultPushCallback(this, MainActivity.class);
//        ParseInstallation.getCurrentInstallation().saveInBackground();
//

        description = (TextView) findViewById(R.id.logintextView);

        // other font_family:
        //Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/segoe-ui-1361529660.ttf");
        //description.setTypeface(tf);

        ImageButton b = (ImageButton) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(LoginActivity.this, "button register clicked", Toast.LENGTH_SHORT).show();

                // an parse den neuen nutzer schicken...

                registerNewUser();
                getFriends();
                //finish();
            }
        });

        ImageButton b2 = (ImageButton) findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Toast.makeText(SignUpActivity.this, "button login clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".LoginActivity");
                //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                startActivity(intent);


            }
        });

    }

    // in dieser methode soll adressbuch durchsucht werden, mit handynummern verglichen und passende user ausgegeben werden
    private void getFriends() {


    }


    private void registerNewUser() {

         name = (EditText)findViewById(R.id.editText);
         email = (EditText)findViewById(R.id.editText3);
         pw = (EditText)findViewById(R.id.editText2);
         phone = (EditText)findViewById(R.id.editText4);
         adr = (EditText)findViewById(R.id.editText5);

        //TODO falls leere felder -> absturz
        ParseUser user = new ParseUser();
        user.setUsername(name.getText().toString());
        user.setPassword(pw.getText().toString());
        user.setEmail(email.getText().toString());

        user.put("phone",phone.getText().toString());
        user.put("adr", adr.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Toast.makeText(SignUpActivity.this, "button register clicked", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent();
                    //intent.setClassName(getPackageName(), getPackageName() + ".MainActivity");
                    //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                    //startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException to figure out what went wrong

                    //TODO sign in nochmal zeigen
                    Toast.makeText(SignUpActivity.this, "failed to register", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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








    int countContact;

    private void Import_contacts_from_address_book() {
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

        Toast.makeText(SignUpActivity.this, "Contacts: "+String.valueOf(countContact), Toast.LENGTH_SHORT).show();

        Toast.makeText(SignUpActivity.this, output.toString(), Toast.LENGTH_SHORT).show();



    }


}

//
//class Contact {
//    public String name;
//    public List<String> phones;
//    public List<String> emails;
//
//
//    private static final Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
//    private static final String _ID = ContactsContract.Contacts._ID;
//    private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
//
//    private static final Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
//    private static final String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
//
//    private static final Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
//    private static final String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
//    private static final String EMAIL = ContactsContract.CommonDataKinds.Email.DATA;
//
//
//    private Collection<Contact> importContactsFromAddressBook() {
//        Map<String, Contact> contactMap = new HashMap<String, Contact>();
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                String contactId = cursor.getString(cursor.getColumnIndex(_ID));
//                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
//                Contact contact = new Contact();
//                contact.name = name;
//                contact.phones = new ArrayList<String>();
//                contact.emails = new ArrayList<String>();
//                contactMap.put(contactId, contact);
//            }
//        }
//        cursor.close();
//
//        // Query and loop for every phone number of the contact
//        Cursor phoneCursor = contentResolver.query(PHONE_CONTENT_URI, null, null, null, null);
//        while (phoneCursor.moveToNext()) {
//            String contactId = phoneCursor.getString(phoneCursor.getColumnIndex(PHONE_CONTACT_ID));
//            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
//            Contact contact = contactMap.get(contactId);
//            contact.phones.add(phoneNumber);
//        }
//        phoneCursor.close();
//
//        // Query and loop for every email of the contact
//
//        Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null, EMAIL_CONTACT_ID + "=?", new String[]{contactId}, null);
//        while (emailCursor.moveToNext()) {
//            String contactId = emailCursor.getString(emailCursor.getColumnIndex(EMAIL_CONTACT_ID));
//            String email = emailCursor.getString(emailCursor.getColumnIndex(EMAIL));
//            Contact contact = contactMap.get(contactId);
//            contact.emails.add(email);
//        }
//        emailCursor.close();
//        return contactMap.values();
//    }
//
//}



