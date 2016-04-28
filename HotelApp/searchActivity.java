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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnLongClickListener;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.view.View.OnDragListener;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class searchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchscreen);
        Intent intent=getIntent();

        View iView = findViewById(R.id.imageView);
        iView.setOnDragListener(dragListener);
        new getToyData().execute();
    }

    byte[] toyByte=null;
    int byteLength=0;

    ToyList inventory;
    ArrayList<Toy> cart = new ArrayList<Toy>();

    public void checkOut(View view)
    {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(searchActivity.this, checkoutActivity.class);
                intent.putExtra("shoppingCart", cart);
                searchActivity.this.startActivity(intent);
            }
        });
    }

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
    public class getToyData extends AsyncTask<String, Byte, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... params)
        {
            URL toyURL=null;

            try
            {
                toyURL=new URL("http://people.cs.georgetown.edu/~wzhou/toy.data");
            }

            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            HttpURLConnection toyConnection=null;

            try
            {
                toyConnection=(HttpURLConnection)toyURL.openConnection();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            InputStream in=null;

            try
            {
                if (toyConnection!=null)
                {
                    in=new BufferedInputStream(toyConnection.getInputStream());
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
                    byteLength=toyConnection.getContentLength();
                    toyByte=new byte[byteLength];
                    int cursor=0;
                    while(cursor<toyConnection.getContentLength())
                    {
                        int readCursor=in.read(toyByte, cursor, byteLength-cursor);
                        cursor+=readCursor;
                    }
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        makeToys(toyByte, byteLength);
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

            toyConnection.disconnect();

            return null;
        }
    }

    public void makeToys(byte[] toyByte, int byteLength)
    {
        ToyList toys = new ToyList(toyByte, byteLength);
        inventory = toys;

        ImageView view=(ImageView)findViewById(R.id.toyImages);
        view.setImageBitmap(toys.ToyList.get(0).toyImage);
        view.setVisibility(view.VISIBLE);
        TextView viewT=(TextView)findViewById(R.id.toyName);
        viewT.setText(toys.ToyList.get(0).toyName);
        TextView viewP=(TextView)findViewById(R.id.toyPrice);
        viewP.setText(Integer.toString(toys.ToyList.get(0).toyPrice));
        //Toy1

        ImageView view2=(ImageView)findViewById(R.id.toyImages2);
        view2.setImageBitmap(toys.ToyList.get(1).toyImage);
        view2.setVisibility(view2.VISIBLE);
        TextView viewT2=(TextView) findViewById(R.id.toyName2);
        viewT2.setText(toys.ToyList.get(1).toyName);
        TextView viewP2=(TextView) findViewById(R.id.toyPrice2);
        viewP2.setText(Integer.toString(toys.ToyList.get(1).toyPrice));
        //Toy2

        ImageView view3=(ImageView)findViewById(R.id.toyImages3);
        view3.setImageBitmap(toys.ToyList.get(2).toyImage);
        view3.setVisibility(view3.VISIBLE);
        TextView viewT3=(TextView) findViewById(R.id.toyName3);
        viewT3.setText(toys.ToyList.get(2).toyName);
        TextView viewP3=(TextView)findViewById(R.id.toyPrice3);
        viewP3.setText(Integer.toString(toys.ToyList.get(2).toyPrice));
        //Toy3

        view.setOnLongClickListener(longCLickListener);
        view2.setOnLongClickListener(longCLickListener);
        view3.setOnLongClickListener(longCLickListener);
    }

    OnDragListener dragListener = new OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();

            switch(dragEvent){
                case DragEvent.ACTION_DROP:
                    ImageView draggedImage = (ImageView) event.getLocalState();

                    if (draggedImage == findViewById(R.id.toyImages)) {
                        Toy newItem = new Toy(inventory.ToyList.get(0).toyName, inventory.ToyList.get(0).toyPrice);
                        cart.add(newItem);
                    }

                    if (draggedImage == findViewById(R.id.toyImages2)) {
                        Toy newItem = new Toy(inventory.ToyList.get(1).toyName, inventory.ToyList.get(1).toyPrice);
                        cart.add(newItem);
                    }

                    if (draggedImage == findViewById(R.id.toyImages3)) {
                        Toy newItem = new Toy(inventory.ToyList.get(2).toyName, inventory.ToyList.get(3).toyPrice);
                        cart.add(newItem);
                    }

                    Toast.makeText(searchActivity.this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };

    OnLongClickListener longCLickListener = new OnLongClickListener(){
        @Override
        public boolean onLongClick(View v){
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new ShadowBuilder(v);
            ImageView toyPic = (ImageView) v;

            v.startDrag(clipData, shadowBuilder, toyPic, 0);
            return true;
        }
    };

    private class ShadowBuilder extends View.DragShadowBuilder{
        private Drawable dragShadow;

        public ShadowBuilder(View v){
            super(v);
            dragShadow = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onDrawShadow(Canvas canvas){
            dragShadow.draw(canvas);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint){
            int height, width;
            height = (int) getView().getHeight() / 2;
            width = (int) getView().getWidth() / 2;

            dragShadow.setBounds(0, 0, width, height);

            shadowSize.set(width, height);
            shadowTouchPoint.set(width / 2, height / 2);
        }
    }

    public class Toy
    {
        private String toyName=null;
        private Bitmap toyImage=null;
        private int toyPrice=0;

        public Toy(byte[] toyByteArray)
        {
            ByteBuffer toyBuffer=ByteBuffer.wrap(toyByteArray);
            int nameLength=toyBuffer.getInt();
            byte[] namebuffer=new byte[nameLength];
            toyBuffer.get(namebuffer, 0, nameLength);
            this.toyName=new String(namebuffer);

            this.toyPrice=toyBuffer.getInt();

            int imageLength=toyBuffer.getInt();
            Bitmap toyBitMap=null;
            final byte[] imagebuffer=new byte[imageLength];
            toyBuffer.get(imagebuffer, 0, imageLength);
            toyImage= BitmapFactory.decodeByteArray(imagebuffer, 0, imagebuffer.length);
        }

        public Toy(String name, int price) {
            toyName = name;
            toyPrice = price;
        }
    }

    public class ToyList
    {
        private ArrayList<Toy> ToyList=new ArrayList<Toy>();

        ToyList(byte[] toyListArray, int length)
        {
            ByteBuffer buffer=ByteBuffer.wrap(toyListArray);
            int cursor=0;
            while(cursor<length)
            {
                int toylength=buffer.getInt();
                byte[] toyBuffer= new byte[toylength];
                buffer.get(toyBuffer, 0, toylength);
                Toy toy=new Toy(toyBuffer);
                ToyList.add(toy);
                cursor+=Integer.SIZE/8+toylength;
            }
        }
    }
}
