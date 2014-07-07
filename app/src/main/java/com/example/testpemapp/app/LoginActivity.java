package com.example.testpemapp.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class LoginActivity extends Activity {

    EditText emailEditText;
    EditText pwEditText;

    String email;
    String pw;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton b = (ImageButton) findViewById(R.id.imageButton2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(LoginActivity.this, "button register clicked", Toast.LENGTH_SHORT).show();
                // an parse den neuen nutzer schicken...

                logInUser();
              //  getFriends();
                //finish();
            }
        });

        ImageButton b2 = (ImageButton) findViewById(R.id.imageButton);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(LoginActivity.this, "button register clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), getPackageName() + ".SignUpActivity");
                //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                startActivity(intent);

            }
        });


    }




    private void logInUser() {

        emailEditText = (EditText)findViewById(R.id.editText);
        pwEditText = (EditText)findViewById(R.id.editText2);

        email = emailEditText.getText().toString();
        pw = pwEditText.getText().toString();


        System.out.println("email: " + email);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email );
         query.getFirstInBackground(new GetCallback<ParseUser>() {
             public void done(ParseUser object, ParseException e) {
                 if (object == null) {
                     Log.d("score", "The getFirst request failed.");
                 } else {
                     Log.d("score", "Retrieved the object.");
                     name = object.getUsername();
                     ParseUser.logInInBackground(name, pw, new LogInCallback() {
                         public void done(ParseUser user, ParseException e) {
                             if (user != null) {
                                 // Hooray! The user is logged in.
                               //   Toast.makeText(LoginActivity.this, "login hat geklappt", Toast.LENGTH_SHORT).show();
                                 Intent intent = new Intent();
                                 intent.setClassName(getPackageName(), getPackageName() + ".MainActivity");
                                 //intent.putExtra("selected", lv.getAdapter().getItem(arg2).toString());
                                 startActivity(intent);
                                 finish();
                             } else {
                                 // Signup failed. Look at the ParseException to see what happened.
                                 Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

                             }
                         }
                     });

                 }
             }
         });

       // finish();

    }



    // in dieser methode soll adressbuch durchsucht werden, mit handynummern verglichen und passende user ausgegeben werden
    private void getFriends() {


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

}
