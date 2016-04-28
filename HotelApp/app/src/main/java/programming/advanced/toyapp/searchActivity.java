package programming.advanced.toyapp;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View.OnLongClickListener;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.view.View.OnDragListener;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class searchActivity extends AppCompatActivity {

    String nameQuery;
    String locationQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchscreen);
        Intent intent=getIntent();
        nameQuery = intent.getStringExtra("nameQuery");
        locationQuery = intent.getStringExtra("locationQuery");
        new gethotelData().execute();
    }


    public void addListenerOnRatingBar() {

        RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);
        RatingBar price = (RatingBar) findViewById(R.id.ratingBar2);

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {


            }
        });
        price.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {


            }
        });
    }

        byte[] hotelByte=null;
    int byteLength=0;

    hotelList hotelList;

   
    public void cancel(View view)
    {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(searchActivity.this, homeActivity.class);
                searchActivity.this.startActivity(intent);
            }
        });
    }
    public class gethotelData extends AsyncTask<String, Byte, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... params)
        {
            URL hotelURL=null;

            try
            {
                hotelURL=new URL("http://people.cs.georgetown.edu/~wzhou/hotel.data");
            }

            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            HttpURLConnection hotelConnection=null;

            try
            {
                hotelConnection=(HttpURLConnection)hotelURL.openConnection();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            InputStream in=null;

            try
            {
                if (hotelConnection!=null)
                {
                    in=new BufferedInputStream(hotelConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            OutputStream out=null;

            try
            {
                if (hotelConnection!=null)
                {
                    out=new BufferedOutputStream(hotelConnection.getOutputStream());
                }
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                if(out!=null)
                {
                    //send name and location queries to server here
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (in!=null)
                {
                    byteLength=hotelConnection.getContentLength();
                    hotelByte=new byte[byteLength];
                    int cursor=0;
                    while(cursor<hotelConnection.getContentLength())
                    {
                        int readCursor=in.read(hotelByte, cursor, byteLength-cursor);
                        cursor+=readCursor;
                    }
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        populateListView();         //add hotels to listView
                    }
                });
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                in.close();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            hotelConnection.disconnect();

            return null;
        }
    }

    

    public class hotel
    {
        private String name=null;
        private int rating=0;
        private int price=0;

        public hotel(byte[] hotelByteArray)
        {
            ByteBuffer hotelBuffer=ByteBuffer.wrap(hotelByteArray);
            int nameLength=hotelBuffer.getInt();
            byte[] namebuffer=new byte[nameLength];
            hotelBuffer.get(namebuffer, 0, nameLength);
            this.name=new String(namebuffer);
            this.rating=hotelBuffer.getInt();
            this.price=hotelBuffer.getInt();
        }

        public hotel(String name, int price) {
            name = name;
            price = price;
        }
    }

    public class hotelList
    {
        private ArrayList<hotel> hotelList=new ArrayList<hotel>();

            hotelList(byte[] hotelListArray, int length)
        {
            ByteBuffer buffer=ByteBuffer.wrap(hotelListArray);
            int cursor=0;
            while(cursor<length)
            {
                int hotellength=buffer.getInt();
                byte[] hotelBuffer= new byte[hotellength];
                buffer.get(hotelBuffer, 0, hotellength);
                hotel hotel=new hotel(hotelBuffer);
                hotelList.add(hotel);
                cursor+=Integer.SIZE/8+hotellength;
            }
        }
    }
    private void populateListView() {       //file the UI with the items from the list
        ArrayAdapter<hotel> adapter = new hotelListAdapter();
        ListView list = (ListView) findViewById(R.id.hotel_list);
        list.setAdapter(adapter);
    }

    private class hotelListAdapter extends ArrayAdapter<hotel> {

        public hotelListAdapter(){
            super(searchActivity.this, R.layout.list_item, (List<hotel>) hotelList);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View view = convertView;
            hotel hotel = hotelList.gethotel(position);

            if (convertView == null){       //if the view is null, create the view
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }


            TextView name = (TextView) view.findViewById(R.id.item_name);
            name.setText(hotel.gethotelName());             //find and set the name

            TextView rating = (ImageView) view.findViewById(R.id.item_list_image);
            rating.setText(hotel.gethotelRating());

            TextView price = (TextView) view.findViewById(R.id.item_price);
            price.setText(Integer.toString(hotel.getPrice()));    //find and set the price

            return view;
        }
    }
}
