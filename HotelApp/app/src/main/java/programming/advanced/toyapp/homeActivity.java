package programming.advanced.toyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class homeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

    }

    public void SearchForToys(View view)
    {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView searchName = (TextView)findViewById(R.id.textView);
                TextView searchLocation = (TextView)findViewById(R.id.editText);
                Intent intent = new Intent(homeActivity.this, searchActivity.class);
                String nameQuery = searchName.getText().toString();
                String locationQuery = searchName.getText().toString();
                if(nameQuery == "@string/name")
                    nameQuery = "";
                if(locationQuery == "@string/location" || locationQuery == "")
                    locationQuery = "";
                intent.putExtra("nameQuery", nameQuery);
                intent.putExtra("locationQuery", locationQuery);
                homeActivity.this.startActivity(intent);
            }
        });
    }
}
