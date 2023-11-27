package glotech.smartgasapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import glotech.smartgasapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

import glotech.smartgasapp.ui.login.LoginActivity;

public class GasExchange extends AppCompatActivity {
    RequestQueue requestQueue;
    public static String Gas_Type, Gas_Weight, Gas_Volume;
    public String Customer_ID, Company_Id, Company_Name;
    public static int Gas_Quantity;
    public TextView volume, name;
    public Button next,backButton;
    int totalValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_exchange);

        
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        requestQueue = Volley.newRequestQueue(this);

        LoginActivity loginActivity = new LoginActivity();
        Customer_ID = String.valueOf(loginActivity.getCustomerID());
        Company_Id = String.valueOf(loginActivity.COMPANY_Id);
        volume = findViewById(R.id.curentGasVolume);
        name = findViewById(R.id.gasCompanyTitle);
        next = findViewById(R.id.confirm_exchangegas_button);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GasExchange.this, UserDashboard.class);
                startActivity(intent);
            }
        });

        Gas_Quantity = 0;

        //顯示殘氣資料
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    showGasRemain();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), Homepage.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), OrderListUnfinished.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }


    public void showGasRemain() {
        try {
            String Showurl = "http://54.199.33.241/test/Gas_Remain.php";
            URL url = new URL(Showurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("Customer_ID", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(Customer_ID), "UTF-8")
                    + "&" +
                    URLEncoder.encode("Company_Id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(Company_Id), "UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.i("Gas Exchange result", "[" + result + "]");
            JSONObject responseJSON = new JSONObject(result);
            Gas_Volume = responseJSON.getString("Gas_Volume");
            Company_Name = responseJSON.getString("Company_Name");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    volume.setText("目前累積殘氣: " + Gas_Volume + "公斤");
                    name.setText("瓦斯行: " + Company_Name);
                }
            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (e.toString().contains("No value for Gas_Volume")) {
                        Toast.makeText(getApplicationContext(), "無殘氣", Toast.LENGTH_LONG).show();
                    }
                }
            });
            Log.i("GasExchange Exception", e.toString());
        }
    }

}