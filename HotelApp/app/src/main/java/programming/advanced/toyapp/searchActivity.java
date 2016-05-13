package programming.advanced.toyapp;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Parcelable;
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
import java.lang.Integer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class searchActivity extends AppCompatActivity {

    String nameQuery;
    String locationQuery;
    ArrayList<hotel> array = new ArrayList<hotel>();
    hotelList hotels;
    BTree hotelTreeRating = new BTree<String, hotel>();
    BTree hotelTreePrice = new BTree<String, hotel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchscreen);
        Intent intent=getIntent();
        nameQuery = intent.getStringExtra("nameQuery");
        locationQuery = intent.getStringExtra("locationQuery");


        new gethotelData().execute();

        hotelData();
        populateListView();
//        new gethotelData().execute();



        /*hotel hotel = new hotel("a",2,3);
        hotels.hotelList.add(hotel);            TEST CODE FOR ADAPTER
        populateListView();*/
    }


    public void addListenerOnRatingBar() {

        RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);
        RatingBar price = (RatingBar) findViewById(R.id.ratingBar2);

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float newRating,
                                        boolean fromUser) {
                refreshNewRating((int) newRating);
            }
        });
        price.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float newPrice,
                                        boolean fromUser) {
                refreshNewPrice((int) newPrice);
            }
        });
    }

    public void refreshNewRating(int newRating){
        if(!array.isEmpty())
        {
            array.clear();
        }
        else {
            while (hotelTreeRating.doesLeafNodeExist(Integer.toString(newRating))) {
                array.add(hotelTreeRating.search(Integer.toString(newRating)));
                if (hotelTreeRating.getRightSibling(Integer.toString(newRating)) == null) {
                    break;
                }
            }
        }
        hotels = new hotelList(array);
        populateListView();
    }

    public void refreshNewPrice(int newPrice){
        if(!array.isEmpty())
        {
            array.clear();
        }
        else {
            while (hotelTreePrice.doesLeafNodeExist(Integer.toString(newPrice))) {
                array.add(hotelTreePrice.search(Integer.toString(newPrice)));
                if(hotelTreePrice.getRightSibling(Integer.toString(newPrice)) == null ){
                    break;
                }
            }
            hotels = new hotelList(array);
            populateListView();
        }
    }

    byte[] hotelByte=null;
    int byteLength=0;

    public void hotelData(){
        /*array.add(new hotel("Marriot", "NewYork", 2, 4));
        array.add(new hotel("Hilton", "NewYork", 2, 3));
        array.add(new hotel("Towers", "NewYork", 5, 5));
        array.add(new hotel("Super 6", "NewYork", 1, 3));
        array.add(new hotel("Belvedere", "NewDehli", 4, 5));
        array.add(new hotel("Marriot", "NewEngland", 3, 4));*/
        hotels = new hotelList(array);
    }

    public void cancel(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(searchActivity.this, hotelActivity.class);
                searchActivity.this.startActivity(intent);
            }
        });
    }
    public class gethotelData extends AsyncTask<String, Byte, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... params)
        {
            Socket socket=null;

            try
            {
                socket=new Socket("52.91.239.95", 7777);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            InputStream in=null;

            try
            {
                in=new BufferedInputStream(socket.getInputStream());
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                //I made my own strings here but you would get it from the textbox the user entered
                OutputStream out=new BufferedOutputStream(socket.getOutputStream());
                String name="Marriott";
                String location="LosAngeles";
                out.write(name.getBytes());
                out.write(location.getBytes());
                out.flush();
                //Send queries here
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                int byteLength=0;
                if (in!=null)
                {
                    try
                    {
                        byteLength=in.available();
                    }

                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    byte[] hotelBytes=new byte[byteLength];
                    int count;
                    try
                    {
                        while((count=in.read(hotelBytes)) > 0)
                        {
                            count++;
                        }
                    }

                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    hotels=new hotelList(hotelBytes, byteLength);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String hotelDataString = streamToString(in);

                            Scanner scan = new Scanner(hotelDataString);
                            while( scan.hasNext() ) //put the unfiltered search into a list
                            {
                                String name = scan.next();
                                String loc = scan.next();
                                int rating = Integer.parseInt(scan.next());
                                int price = Integer.parseInt(scan.nextLine());
                                hotel value = new hotel(name,loc,rating,price);
                                array.add(value);
                            }

                            Scanner ratingScan = new Scanner(hotelDataString);
                            while( ratingScan.hasNext() )
                            {
                                String nameR = ratingScan.next()
                                String locR = ratingScan.next();
                                String keyR = ratingScan.next();
                                int ratingR = Integer.parseInt(key);
                                int priceR = Integer.parseInt(ratingScan.nextLine());
                                hotel valueR = new hotel(nameR,locR,ratingR,priceR);
                                hotelTreeRating.insert(keyR,valueR);
                            }

                            Scanner priceScan = newScanner(hotelDataString);
                            while( priceScan.hasNext() )
                            {
                                String nameP = priceScan.next()
                                String locP = priceScan.next();
                                int ratingP = Integer.parseInt(priceScan.next());
                                String keyP = priceScan.nextLine();
                                int priceP = Integer.parseInt(key);
                                hotel valueP = new hotel(name, loc, rating, price);
                                hotelTreePrice.insert(keyP, valueP);
                            }

                            hotels = new hotelList(array);
                            populateListView();         //add hotels to listView
                        }
                    });
                }

                try
                {
                    socket.close();
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            finally
            {
                return null;
            }
        }
    }

    public static String streamToString(BufferedInputStream bis)
    {
        BufferedInputStream in = bis;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(read);
            builder.appdend(System.getProperty("line.separator"));
        }

        reader.close();
        return builder.toString();
    }


    public class hotel
    {
        public String getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }

        public int getRating() {
            return rating;
        }

        public int getPrice() {
            return price;
        }

        private String name=null;
        private int rating=0;
        private int price=0;
        private String location =null;

        public hotel(byte[] hotelByteArray)
        {
            ByteBuffer hotelBuffer=ByteBuffer.wrap(hotelByteArray);
            int nameLength=hotelBuffer.getInt();
            byte[] namebuffer=new byte[nameLength];
            hotelBuffer.get(namebuffer, 0, nameLength);
            this.name=new String(namebuffer);
            nameLength=hotelBuffer.getInt();
            namebuffer=new byte[nameLength];
            hotelBuffer.get(namebuffer, 0, nameLength);
            this.location=new String(namebuffer);
            this.price=hotelBuffer.getInt();
            this.rating=hotelBuffer.getInt();
        }

        public hotel(String name, String location, int price, int rating) {
            this.name = name;
            this.price = price;
            this.rating = rating;
            this.location=location;
        }
    }

    public class hotelList
    {
        public hotel getHotel(int position) {
            return hotelList.get(position);
        }

        private ArrayList<hotel> hotelList=new ArrayList<hotel>();

        public hotelList(ArrayList<hotel> hotelList) {
            this.hotelList = hotelList;
        }

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
            super(searchActivity.this, R.layout.list_item, (List<hotel>) hotels.hotelList);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View view = convertView;
            hotel hotel = hotels.getHotel(position);

            if (convertView == null){       //if the view is null, create the view
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }



            TextView name = (TextView) view.findViewById(R.id.item_name);
            name.setText(hotel.getName());//find and set the name
            name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(searchActivity.this,hotelActivity.class);
                    startActivity(intent);
                }
            });
          // name.setOnClickListener(new myListener(hotel));

            TextView location = (TextView) view.findViewById(R.id.item_location);
            location.setText(hotel.getLocation());

            TextView rating = (TextView) view.findViewById(R.id.item_rating);
            rating.setText(Integer.toString(hotel.getRating()));

            TextView price = (TextView) view.findViewById(R.id.item_price);
            price.setText(Integer.toString(hotel.getPrice()));    //find and set the price

            return view;
        }
    }

    public class myListener implements View.OnClickListener
    {

        hotel hotel;
        public myListener(hotel data) {
            this.hotel = data;
        }

        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(searchActivity.this,hotelActivity.class);
            intent.putExtra("hotel", (Parcelable) hotel);
            startActivity(intent);
        }

    };
}
