package glotech.smartgasapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import glotech.smartgasapp.R;

public class OrderFailed extends AppCompatActivity {
    public Button check;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_scan_failed);
        check = findViewById(R.id.exchangeFailedNext);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderFailed.this, Homepage.class);
                startActivity(intent);
            }
        });
    }
}
