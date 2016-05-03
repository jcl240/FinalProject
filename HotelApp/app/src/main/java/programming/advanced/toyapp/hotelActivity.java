package programming.advanced.toyapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;



public class hotelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Intent intent = getIntent();
        searchActivity.hotel hotel = (searchActivity.hotel)intent.getSerializableExtra("hotel");
        TextView name = (TextView) findViewById(R.id.textView8);
        name.setText(hotel.getName());
        TextView location = (TextView) findViewById(R.id.textView9);
        location.append(hotel.getLocation());
        TextView rating = (TextView) findViewById(R.id.textView10);
        rating.append(Integer.toString(hotel.getRating()));
        TextView price = (TextView) findViewById(R.id.textView11);
        price.append(Integer.toString(hotel.getPrice()));*/
        setContentView(R.layout.hotelscreen);
    }

    public void clicked(){
        Uri uri = Uri.parse("http://www.google.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
