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
                TextView search = (TextView)findViewById(R.id.textView);
                Intent intent = new Intent(homeActivity.this, searchActivity.class);
                String searchQuery = search.getText().toString();
                intent.putExtra("searchQuery", searchQuery);
                homeActivity.this.startActivity(intent);
            }
        });
    }
}
