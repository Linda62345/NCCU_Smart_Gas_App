package glotech.smartgasapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import glotech.smartgasapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class CompositeGasMenu extends AppCompatActivity {

    private Button cylinder;
    private Button order;
    private Button delivery;
    private AppCompatButton minus1,minus2,minus3,plus1,plus2,plus3;
    private TextView first,second,third;
    public static int a = 0, b = 0, c = 0;
    public static String weight1, weight2, weight3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composite_gas_menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.edit = false;
        a = 0;
        b = 0;
        c = 0;

        cylinder = findViewById(R.id.cylinderGas);
        order = findViewById(R.id.compositeGas);
        delivery = findViewById(R.id.ChooseDeliverMethod);

        BottomNavigationView bottomNavigationView=findViewById(R.id.nav_view);

        Minus_Add();


        weight1 = "5";
        weight2 = "16";
        weight3 = "20";

        cylinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompositeGasMenu.this, cylinder_gas_menu.class);
                startActivity(intent);
            }
        });


        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompositeGasMenu.this, OrderGas.class);
                startActivity(intent);
            }
        });

        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompositeGasMenu.this, DeliveryMethod.class);
                startActivity(intent);
                orderDetail.edit = true;
            }
        });

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), Homepage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), OrderListUnfinished.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
    public void Minus_Add(){
        first = findViewById(R.id.first);
        first.setText(String.valueOf(a));
        second = findViewById(R.id.second);
        second.setText(String.valueOf(b));
        third = findViewById(R.id.third);
        third.setText(String.valueOf(c));

        minus1 = findViewById(R.id.minus1);
        minus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a -= 1;
                if(a<0){
                    a = 0;
                }
                first.setText(String.valueOf(a));
            }
        });
        minus2 = findViewById(R.id.minus2);
        minus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b -= 1;
                if(b<0){
                    b = 0;
                }
                second.setText(String.valueOf(b));
            }
        });
        minus3 = findViewById(R.id.minus3);
        minus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c -= 1;
                if(c<0){
                    c = 0;
                }
                third.setText(String.valueOf(c));
            }
        });
        plus1 = findViewById(R.id.plus1);
        plus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a += 1;
                first.setText(String.valueOf(a));
            }
        });
        plus2 = findViewById(R.id.plus2);
        plus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b += 1;
                second.setText(String.valueOf(b));
            }
        });
        plus3 = findViewById(R.id.plus3);
        plus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c += 1;
                third.setText(String.valueOf(c));
            }
        });
    }
}
