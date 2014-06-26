package com.example.testpemapp.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


// es sollen alle freunde angezeigt werden in einer einfachen liste

public class FriendActivity extends Activity {

    Person person;
    Person[] persons;

    private ListView lv;
    ListAdapter adapter;

    EditText email;
    ImageButton search;
    String emailAdresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
//
//        Parse.initialize(this, "MHHSAa8eQ6gpV4GnGO8TJBVjQ7f4bN8EuqKego9l", "DUhSOqqpyz677Zaz1TuA0jthlRINYTN9u4LYxQdL");
//        PushService.setDefaultPushCallback(this, MainActivity.class);
//        ParseInstallation.getCurrentInstallation().saveInBackground();

        email = (EditText) findViewById(R.id.editText);
        search = (ImageButton) findViewById(R.id.imageButton);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              sendFriendReq();

            }
        });


        createFriendListView();






        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                Intent intent = new Intent();
//                intent.setClassName(getPackageName(), getPackageName()+".DetailsActivity");
//                intent.putExtra("index", arg2);
//                //gibt title mit
//                intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
//                intent.putExtra("objectIdTest", entrys[arg2].getId());
//                startActivity(intent);

                Toast.makeText(FriendActivity.this, "clicccckkkkkk", Toast.LENGTH_SHORT).show();


                //public void onListItemClick(ListView l, View v, int position, long id) {
                //showDetails(position)
                //void showDetails(int index) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), DetailsActivity.class);
//                intent.putExtra("index", index);
//                intent.putExtra("input", adapter.getItem(index).toGanzString());
//                startActivity(intent);

            }
        });


        Import_contacts_from_address_book();
    }

    private void createFriendListView() {

        lv = (ListView) findViewById(R.id.listView);

        // set up the query on the Follow table
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo("fromUser", ParseUser.getCurrentUser());
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {

                if(e==null && followList.size()>0) {

                    List friendList = new ArrayList<String>();

                    for (int i = 0; i < followList.size(); i++) {
                        friendList.add(((ParseUser) followList.get(i).get("toUser")).getUsername());
                        System.out.println("friendlist eintrag: " + ((ParseUser) followList.get(i).get("toUser")).getUsername());
                    }
                    adapter = new ArrayAdapter(getApplicationContext(), R.layout.mytextview, friendList);
                    lv.setAdapter(adapter);
                    Toast.makeText(FriendActivity.this, "adapter erzeugt", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(FriendActivity.this, "freundeliste leer", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void sendFriendReq() {
        // falls nur update gemacht werden soll...
        if (email.getText() != null) {
            emailAdresse = email.getText().toString();

            // email an parse schicken
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", emailAdresse);
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null && objects.size()>0) {
                        // The query was successful. -> freund hinzufügen..
                        Toast.makeText(FriendActivity.this, "found user :))", Toast.LENGTH_SHORT).show();

                        ParseObject follow = new ParseObject("Friendship");
                        follow.put("fromUser", ParseUser.getCurrentUser());
                        follow.put("toUser", objects.get(0));
                        //follow.put("date", Date());
                        follow.saveInBackground();

                        //TODO: PushNotification an den Freund
                        createFriendListView();
                        Toast.makeText(FriendActivity.this, "you and "+emailAdresse+" are now friends", Toast.LENGTH_SHORT).show();

                    } else {
                        // Something went wrong.
                        Toast.makeText(FriendActivity.this, "could not find user, email wrong?)", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            Toast.makeText(FriendActivity.this, "ok clicked, email: " + emailAdresse, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(FriendActivity.this, "Bitte Email Adresse eingeben", Toast.LENGTH_SHORT).show();

        }
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
    //TextView t1;
   // TextView t2;

    private void Import_contacts_from_address_book() {

        //t1 = (TextView) findViewById(R.id.textView);
      //  t2 = (TextView) findViewById(R.id.textView3);

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
        System.out.println("---------------------->" + cursor.getCount());
        if (cursor.getCount() > 0) {
            countContact = cursor.getCount();

            // test:
            persons = new Person[countContact];
            int counter = 0;

            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));


                //------------------- Catch phone number, neu phone >0 thi get phone, ko thi lam chuyen khac
                //if(hasPhoneNumber > 0 ){
                output.append("\nFirst Name: " + name);
                person = new Person(name, "");


                // Query and loop for every phone number of the contact
                Cursor phoneCursor = contentResolver.query(PHONECONTENT_URI, null, PHONECONTACT_ID + "=?", new String[]{contact_id}, null);
                while (phoneCursor.moveToNext()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    output.append("\n Phone number: " + phoneNumber);
                    person.setTelnr(phoneNumber);
                }
                phoneCursor.close();
                // Query and loop for every email of the contact

                Cursor emailCurosr = contentResolver.query(EMAILCONTENT_URI, null, EMAILCONTACT_ID + "=?", new String[]{contact_id}, null);
                while (emailCurosr.moveToNext()) {
                    email = emailCurosr.getString(emailCurosr.getColumnIndex(EMAIL));
                    output.append("\nEmail: " + email);
                }
                emailCurosr.close();
                //}
                output.append("\n");

                persons[counter] = person;
                counter++;

            }
            cursor.close();
        }

//        for (int i = 0; i < persons.length; i++) {
//            System.out.println(persons[i].getName() + " tel: " + persons[i].getTelnr());
//        }

        //txtViewContactsInfor.setText("Contacts: "+String.valueOf(countContact));
        //outputText.setText(output.toString());

        Toast.makeText(FriendActivity.this, "Contacts: " + String.valueOf(countContact), Toast.LENGTH_SHORT).show();

        //Toast.makeText(FriendActivity.this, output.toString(), Toast.LENGTH_SHORT).show();


//        // speichern des eigentlichen Contacts objekts mit Verweis zu sich selbst
//        ParseObject newContact = new ParseObject("Friend");
//        //newContact.put("email", email);
//        newContact.put("user", ParseUser.getCurrentUser());
//       // newContact.put("with", friendname);
//        newContact.saveInBackground();


        // hole daten und vergleiche:

//        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("Contact");
//        innerQuery.whereExists("nummer");
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Contact");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> commentList, ParseException e) {
//                // comments now contains the comments for posts with images.
//
//
//            }
//        });


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        //query.whereEqualTo("gender", "female");
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    for (int j = 0; j < objects.size(); j++) {
                        for (int i = 0; i < persons.length; i++) {
                           // System.out.println(persons[i].getName() + " tel: " + persons[i].getTelnr());
                        }
                    }
                } else {
                    // Something went wrong.
                }
            }
        });

        //for (int i = 0; i < persons.length; i++) {
          //  System.out.println(persons[i].getName() + " tel: " + persons[i].getTelnr());
       // }


       // t1.setText("Contacts: " + String.valueOf(countContact));
       // t2.setText(output.toString());


    }

}
