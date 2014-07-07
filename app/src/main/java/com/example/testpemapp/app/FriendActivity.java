package com.example.testpemapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class FriendActivity extends Activity {


    private ListView lv;
    ArrayAdapter adapter;

    EditText email;
    ImageButton search;
    String emailAdresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        email = (EditText) findViewById(R.id.editText);
        search = (ImageButton) findViewById(R.id.imageButton);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFriendReq();

            }
        });


        lv = (ListView) findViewById(R.id.listView);

        createFriendListView();

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                Toast.makeText(FriendActivity.this, "clicccckkkkkk", Toast.LENGTH_SHORT).show();
//            }
//        });

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
        // Import_contacts_from_address_book();
    }

    private void createFriendListView() {

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Friendship");
        query2.include("user");
        query2.whereEqualTo("fromUser", ParseUser.getCurrentUser());
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> followList, ParseException e) {

                if (e == null) {
                    if (followList.size() > 0) {

                        ArrayList friendList = new ArrayList<String>();

                        for (int i = 0; i < followList.size(); i++) {
                            try {
                                friendList.add(((ParseUser) followList.get(i).get("toUser")).fetchIfNeeded().getUsername());

                                System.out.println("friendlist eintrag: " + ((ParseUser) followList.get(i).get("toUser")).fetchIfNeeded().getUsername());
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                                System.out.println("miauuuuu");
                            }

                        }
                        adapter = new ArrayAdapter(getApplicationContext(), R.layout.mytextview, friendList);
                        lv.setAdapter(adapter);

                        SwipeDismissListViewTouchListener touchListener =
                                new SwipeDismissListViewTouchListener(
                                        lv,
                                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                            @Override
                                            public boolean canDismiss(int position) {
                                                return true;
                                            }

                                            @Override
                                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                                for (int position : reverseSortedPositions) {


//                                                    ParseQuery<ParseObject> query23 = ParseQuery.getQuery("Friendship");
//                                                    query23.whereEqualTo("toUser", ParseUser.getCurrentUser());
//                                                    System.out.println(followList.get(position).get("toUser"));
//                                                    query23.whereEqualTo("fromUser", (ParseUser) followList.get(position).get("toUser"));
//                                                    query23.getFirstInBackground(new GetCallback<ParseObject>() {
//                                                        @Override
//                                                        public void done(ParseObject parseObject, ParseException e) {
//                                                            if (e != null && parseObject != null) {
//                                                                //parseObject.deleteInBackground();
//                                                                System.out.println("object gefunden: " + parseObject.getString("fromUser"));
//                                                                System.out.println("freund wurde gelsöcht");
//                                                            } else {
//                                                                System.out.println("hat nich geklappt mit freund lsöchen");
//                                                            }
//                                                        }
//                                                    });


                                                    followList.get(position).deleteInBackground();

                                                    adapter.remove(adapter.getItem(position));
                                                }
                                                //loadEntrys();
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                );
                        lv.setOnTouchListener(touchListener);

                        lv.setOnScrollListener(touchListener.makeScrollListener());

                    } else {
                        //        Toast.makeText(FriendActivity.this, "freundeliste leer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //    Toast.makeText(FriendActivity.this, "kein internet", Toast.LENGTH_SHORT).show();
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
                    if (e == null && objects.size() > 0) {
                        System.out.println("currentuser : " + ParseUser.getCurrentUser().getUsername());
                        System.out.println("received user: " + objects.get(0).getUsername());
                        // The query was successful. -> freund hinzufügen..
                        //     Toast.makeText(FriendActivity.this, "found user :))", Toast.LENGTH_SHORT).show();
                        if (!objects.get(0).getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            ParseObject follow = new ParseObject("Friendship");
                            follow.put("fromUser", ParseUser.getCurrentUser());
                            follow.put("toUser", objects.get(0));
                            //follow.put("date", Date());
                            //follow.saveInBackground();
                            follow.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // myObjectSavedSuccessfully();
                                        createFriendListView();
                                    } else {
                                        // myObjectSaveDidNotSucceed();
                                        System.out.println("freund wurde nicht gespeichert");
                                    }
                                }
                            });


                            ParseObject followed = new ParseObject("Friendship");
                            followed.put("fromUser", objects.get(0));
                            followed.put("toUser", ParseUser.getCurrentUser());
                            //follow.put("date", Date());
                            followed.saveInBackground();

                            Toast.makeText(FriendActivity.this, "Du und " + emailAdresse + " sind jetzt Freunde!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FriendActivity.this, "Du kannst dich nicht mir dir selbst befreunden", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Something went wrong.
                        Toast.makeText(FriendActivity.this, "could not find user, email wrong?", Toast.LENGTH_SHORT).show();

                    }
                }
            });

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






    // für das automatische hinzufügen der freunde:
    // es müssten alle contacts hochgeladen werden als tabelle: name + telefonnummer
    // dann müssen die beiden tabellen verglichen werden also contacts und user
    // dann müssen contact + userID zurückgegeben werden...

    // int countContact;
    //TextView t1;
    // TextView t2;
//
//    private void Import_contacts_from_address_book() {
//
//        //t1 = (TextView) findViewById(R.id.textView);
//      //  t2 = (TextView) findViewById(R.id.textView3);
//
//
//
//
//
//        String phoneNumber = null;
//        String email = null;
//        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
//        String _ID = ContactsContract.Contacts._ID;
//        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
//        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
//
//        Uri PHONECONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        String PHONECONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
//        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
//
//        Uri EMAILCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
//        String EMAILCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
//        String EMAIL = ContactsContract.CommonDataKinds.Email.DATA;
//
//        StringBuffer output = new StringBuffer();
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
//        System.out.println("---------------------->" + cursor.getCount());
//        if (cursor.getCount() > 0) {
//            countContact = cursor.getCount();
//
//            // test:
//            persons = new Person[countContact];
//            int counter = 0;
//
//            while (cursor.moveToNext()) {
//                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
//                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
//                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
//
//
//                //------------------- Catch phone number, neu phone >0 thi get phone, ko thi lam chuyen khac
//                //if(hasPhoneNumber > 0 ){
//                output.append("\nFirst Name: " + name);
//                person = new Person(name, "");
//
//
//                // Query and loop for every phone number of the contact
//                Cursor phoneCursor = contentResolver.query(PHONECONTENT_URI, null, PHONECONTACT_ID + "=?", new String[]{contact_id}, null);
//                while (phoneCursor.moveToNext()) {
//                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
//                    output.append("\n Phone number: " + phoneNumber);
//                    person.setTelnr(phoneNumber);
//                }
//                phoneCursor.close();
//                // Query and loop for every email of the contact
//
//                Cursor emailCurosr = contentResolver.query(EMAILCONTENT_URI, null, EMAILCONTACT_ID + "=?", new String[]{contact_id}, null);
//                while (emailCurosr.moveToNext()) {
//                    email = emailCurosr.getString(emailCurosr.getColumnIndex(EMAIL));
//                    output.append("\nEmail: " + email);
//                }
//                emailCurosr.close();
//                //}
//                output.append("\n");
//
//                persons[counter] = person;
//                counter++;
//
//            }
//            cursor.close();
//        }
//
////        for (int i = 0; i < persons.length; i++) {
////            System.out.println(persons[i].getName() + " tel: " + persons[i].getTelnr());
////        }
//
//        //txtViewContactsInfor.setText("Contacts: "+String.valueOf(countContact));
//        //outputText.setText(output.toString());
//
//        Toast.makeText(FriendActivity.this, "Contacts: " + String.valueOf(countContact), Toast.LENGTH_SHORT).show();
//
//        //Toast.makeText(FriendActivity.this, output.toString(), Toast.LENGTH_SHORT).show();
//
//
////        // speichern des eigentlichen Contacts objekts mit Verweis zu sich selbst
////        ParseObject newContact = new ParseObject("Friend");
////        //newContact.put("email", email);
////        newContact.put("user", ParseUser.getCurrentUser());
////       // newContact.put("with", friendname);
////        newContact.saveInBackground();
//
//
//        // hole daten und vergleiche:
//
////        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("Contact");
////        innerQuery.whereExists("nummer");
////
////        ParseQuery<ParseObject> query = ParseQuery.getQuery("Contact");
////        query.findInBackground(new FindCallback<ParseObject>() {
////            public void done(List<ParseObject> commentList, ParseException e) {
////                // comments now contains the comments for posts with images.
////
////
////            }
////        });
//
//
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        //query.whereEqualTo("gender", "female");
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> objects, ParseException e) {
//                if (e == null) {
//                    // The query was successful.
//                    for (int j = 0; j < objects.size(); j++) {
//                        for (int i = 0; i < persons.length; i++) {
//                           // System.out.println(persons[i].getName() + " tel: " + persons[i].getTelnr());
//                        }
//                    }
//                } else {
//                    // Something went wrong.
//                }
//            }
//        });
//
//        //for (int i = 0; i < persons.length; i++) {
//          //  System.out.println(persons[i].getName() + " tel: " + persons[i].getTelnr());
//       // }
//
//
//       // t1.setText("Contacts: " + String.valueOf(countContact));
//       // t2.setText(output.toString());
//
//
//    }

}
