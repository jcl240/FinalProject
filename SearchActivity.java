package com.example.owner.toyapp;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

//Second Screen
public class SearchActivity extends AppCompatActivity {

    ToyList toyList = new ToyList();
    Button cancelButton = (Button) findViewById(R.id.cancel);
    Button checkoutButton = (Button) findViewById(R.id.checkout);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        try {
            URL url = new URL("http://people.cs.georgetown.edu/~wzhou/toy.data");   //open connection to website
            URLConnection connection = url.openConnection();

            File file = new File(url.toURI());  //convert the url to uri and store the file

            populateToyList(file.getName());    //fill the list interface
        }
        catch(IOException e){
            e.printStackTrace(System.out);
        }
        catch(URISyntaxException e){
            e.printStackTrace(System.out);
        }

        populateListView();

        cancelButton.setOnClickListener(new View.OnClickListener() {    //return to first screen on click
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), WelcomeActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {  //go to third screen on click
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void populateToyList(String filename) {
        toyList.readFromFile(filename);     //populate list using driver code's readFromFile method
    }

    private void populateListView() {       //file the UI with the items from the list
        ArrayAdapter<Toy> adapter = new ToyListAdapter();
        ListView list = (ListView) findViewById(R.id.toy_list);
        list.setAdapter(adapter);
    }

    private class ToyListAdapter extends ArrayAdapter<Toy> {

        public ToyListAdapter(){
            super(SearchActivity.this, R.layout.list_item, (List<Toy>) toyList);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View view = convertView;
            Toy toy = toyList.getToy(position);

            if (convertView == null){       //if the view is null, create the view
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }

            ImageView image = (ImageView) view.findViewById(R.id.item_list_image);
            image.setImageBitmap(toy.getImage());       //find and set the image

            TextView name = (TextView) view.findViewById(R.id.item_name);
            name.setText(toy.getToyName());             //find and set the name

            TextView price = (TextView) view.findViewById(R.id.item_price);
            price.setText(Integer.toString(toy.getPrice()));    //find and set the price

            return view;
        }
    }
}
