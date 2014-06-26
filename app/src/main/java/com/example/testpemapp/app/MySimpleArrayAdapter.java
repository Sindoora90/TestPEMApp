package com.example.testpemapp.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final Entry[] val;

    public MySimpleArrayAdapter(Context context, String[] values, Entry[] val) {
        super(context, R.layout.simplerow, values);
        this.context = context;
        this.values = values;
        this.val = val;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.simplerow, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
        TextView preis = (TextView) rowView.findViewById(R.id.textView);
        ImageView geschenk = (ImageView) rowView.findViewById(R.id.geschenkview);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position]);
       // textView2.setText("eingestellt von:   " + val[position].getName().getUsername() +  " (position: " +  position + ")");
        // Change the icon for Windows and iPhone
        textView2.setText("von: " + val[position].getName().getUsername() +  " (position: " +  position + ")");
        if(val[position].isGeschenk()){
            preis.setVisibility(View.GONE);
        }else{
            geschenk.setVisibility(View.GONE);
            preis.setText(Double.toString(val[position].getPrice()) + " €");
        }
        String s = values[position];
        //if (s.startsWith("Windows7") || s.startsWith("iPhone")
          //      || s.startsWith("Solaris")) {
            imageView.setImageBitmap(val[position].getPicture());
        //} else {
          //  imageView.setImageResource(R.drawable.fri);
        //}

        return rowView;
    }
}