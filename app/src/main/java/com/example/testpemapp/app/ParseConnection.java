package com.example.testpemapp.app;

import android.graphics.Bitmap;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

/**
 * Created by Sindoora on 04.06.14.
 *
 * hier soll alles was zu parse gehoert ausgefuehrt werden also user registrieren, objekte speichern/updaten/loeschen/holen....
 */
public class ParseConnection {

    //Methoden die rein muessen:

    // - createUser
    // - createNewEntry
    // - updateUser
    // - updateEntry
    // - deleteEntry
    // - getAllEntrys
    // - ...



//
//    Entry[] eentrys;
//    Entry[] entrys;
//
//    Bitmap pic;
//
//    public Entry[] getAllEntrys(){
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> scoreList, ParseException e) {
//                if (e == null) {
//                    Log.d("score", "Retrieved " + scoreList.size() + " scores");
//                    entrys = new Entry[scoreList.size()];
//                    Log.d("entry array size: ", "size of entry array:" + entrys.length);
//                    Entry entry;
//                    for(int i = 0; i < scoreList.size(); i++){
//                        // Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){
//
//                        ParseFile picFile = (ParseFile)scoreList.get(i).getParseFile("picFile");
//
//
//                        picFile.getDataInBackground(new GetDataCallback() {
//                            public void done(byte[] data,
//                                             ParseException e) {
//                                if (e == null) {
//                                   pic = BitmapFactory.decodeByteArray(data, 0, data.length);
//
//                                } else {
//                                    Log.d("test",
//                                            "There was a problem downloading the data.");
//                                }
//                            }
//                        });
//
//
//                        //Entry eins = new Entry(0,"Teddy", true, BitmapFactory.decodeResource(getResources(), R.drawable.bri)
//                        //        ,0.00, "description blablabla", ParseUser.getCurrentUser());
//
//                        entry = new Entry(i, scoreList.get(i).getString("title"), scoreList.get(i).getBoolean("geschenk"), pic, scoreList.get(i).getDouble("price"), scoreList.get(i).getString("description"), ParseUser.getCurrentUser());
//                        Log.d("entry", entry.toString());
//                        entrys[i] = entry;
//                    }
//
//
//                } else {
//                    Log.d("score", "Error: " + e.getMessage());
//                }
//                Log.d("allentrys: ", "Entry array dass an main geschickt werden soll: " + entrys);
//                eentrys = entrys;
//            }
//        });
//
//       return eentrys;
//    }



   public void createNewEntry( Bitmap pic, String title, boolean geschenk, double price, String description){

       // Bild muss extra gespeichert werden:

       ByteArrayOutputStream stream = new ByteArrayOutputStream();
       pic.compress(Bitmap.CompressFormat.JPEG, 10, stream);
       byte[] bytearray = stream.toByteArray();
       ParseFile file = new ParseFile("nameDesBildes.png", bytearray);
       file.saveInBackground();

       // speichern des eigentlichen Entry Objekts mit einem Verweis auf das Bild:

       ParseObject newEntry = new ParseObject("Entry");
       newEntry.put("title", title);
       newEntry.put("geschenk", geschenk);
       newEntry.put("price", price);
       newEntry.put("description", description);
       newEntry.put("user", ParseUser.getCurrentUser());
       newEntry.put("picFile", file);
       newEntry.saveInBackground();

    }



    // bisher macht sie dasselbe wie newEntry also erzeugt ein neues object aber es soll des vorhandene updaten (-> objectID?)
    public void updateEntry(String id,Bitmap pic, final String title, final boolean geschenk, final double price, final String description) {


        // Bild updaten geht noch nicht
        // Bild muss extra gespeichert werden:

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        // hier richtig compressen...
//        pic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] bytearray = stream.toByteArray();
//        final ParseFile file = new ParseFile("nameDesBildes.png", bytearray);
//        file.saveInBackground();

//        // speichern des eigentlichen Entry Objekts mit einem Verweis auf das Bild:
//
//        ParseObject newEntry = new ParseObject("Entry");
//        newEntry.put("title", title);
//        newEntry.put("geschenk", geschenk);
//        newEntry.put("price", price);
//        newEntry.put("description", description);
//        newEntry.put("user", ParseUser.getCurrentUser());
//        newEntry.put("picFile", file);
//        newEntry.saveInBackground();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");

// Retrieve the object by id
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject newEntry, ParseException e) {
                if (e == null) {
                    newEntry.put("title", title);
                    newEntry.put("geschenk", geschenk);
                    newEntry.put("price", price);
                    newEntry.put("description", description);
                    newEntry.put("user", ParseUser.getCurrentUser());
                    //newEntry.put("picFile", file);
                    newEntry.saveInBackground();

                }
            }
        });
    }
}
