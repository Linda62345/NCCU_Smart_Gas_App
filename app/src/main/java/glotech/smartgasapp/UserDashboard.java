package glotech.smartgasapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import glotech.smartgasapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;

import glotech.smartgasapp.ui.login.LoginActivity;

public class UserDashboard extends AppCompatActivity {

    private Button userInfo;
    private Button volumeInfo;
    private ImageButton search,usageHistory,notification,exchange,eventOrAct,famCoupon;
    private Button logout;
    private Spinner iot;
    private TextView VolumeLeft,showName;
    private ProgressBar progressBar;
    private String selectedSensorId, IotId;
    public String result = "", Customer_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        userInfo = findViewById(R.id.go_edit_profile);
        //iot = findViewById(R.id.iotSpinner);
        search = findViewById(R.id.search);
        usageHistory = findViewById(R.id.usageHistory);

        notification = findViewById(R.id.notificationFrequency);
        exchange = findViewById(R.id.volExchange);
        eventOrAct = findViewById(R.id.activityButton);

        famCoupon = findViewById(R.id.familyCodeButton);
        logout = findViewById(R.id.logout_button);

        showName = findViewById(R.id.show_name);
        LoginActivity loginActivity = new LoginActivity();
        showName.setText(loginActivity.Customer_Name);

        // Initialize selectedSensorId
        if (selectedSensorId == null) {
            selectedSensorId = ""; // Set it to an empty string initially
        }

        Customer_ID = String.valueOf(loginActivity.getCustomerID());

        NetworkTask networkTask1 = new NetworkTask();
        networkTask1.execute(selectedSensorId, Customer_ID);




        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, EditPersonalInfo.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, OrderListUnfinished.class);
                startActivity(intent);
            }
        });
        usageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, UsageHistory.class);
                startActivity(intent);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, NotificationFrequency.class);
                startActivity(intent);
            }
        });
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, GasExchange.class);
                startActivity(intent);
            }
        });
        eventOrAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, EventPage.class);
                startActivity(intent);
            }
        });
        famCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, FamilyInvitationCode.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, LoginActivity.class);
                clearLoginData();
                intent.putExtra("clearCredentials", true); // Add extra information
                startActivity(intent);

                stopService(new Intent(UserDashboard.this, NotificationForegroundService.class));

                finish();

              //  NotificationFrequency.cancelNotification(UserDashboard.this);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                        overridePendingTransition(0, 0);
                        NetworkTask networkTask = new NetworkTask();
                        networkTask.execute(selectedSensorId, Customer_ID);
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), Homepage.class));
                        overridePendingTransition(0, 0);
                        NetworkTask networkTask1 = new NetworkTask();
                        networkTask1.execute(selectedSensorId, Customer_ID);
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

    private void clearLoginData() {
        SharedPreferences sharedPref = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("email");
        editor.remove("password");
        editor.apply();
    }


    private class NetworkTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String selectedSensorId = null;
                String customerID = null;
                if (params.length > 0) {
                    selectedSensorId = params[0];
                    customerID = params[1];
                }
                String Showurl = "http://54.199.33.241/test/Iot_Connect.php";
                URL url = new URL(Showurl);

                HttpURLConnection httpURLConnection3 = (HttpURLConnection) url.openConnection();
                httpURLConnection3.setRequestMethod("POST");
                httpURLConnection3.setDoOutput(true);
                httpURLConnection3.setDoInput(true);
                OutputStream outputStream3 = httpURLConnection3.getOutputStream();
                BufferedWriter bufferedWriter3 = new BufferedWriter(new OutputStreamWriter(outputStream3, "UTF-8"));

                String post_data3 = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Customer_ID, "UTF-8");
                //String post_data4 = URLEncoder.encode("id1", "UTF-8") + "=" + URLEncoder.encode(selectedSensorId, "UTF-8");
                //String post_data4 = "id1=" + URLEncoder.encode(selectedSensorId, "UTF-8");

                Log.i("customerID: ", post_data3);

                bufferedWriter3.write(post_data3);
                // bufferedWriter3.write(post_data4);
                bufferedWriter3.flush();
                bufferedWriter3.close();
                outputStream3.close();

                int statusCode3 = httpURLConnection3.getResponseCode();
                int statusCode = 0;
                if (statusCode3 == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream3 = httpURLConnection3.getInputStream();
                    BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(inputStream3, "iso-8859-1"));
                    String line3 = "";
                    StringBuilder result3 = new StringBuilder();
                    while ((line3 = bufferedReader3.readLine()) != null) {
                        result3.append(line3);
                    }
                    bufferedReader3.close();
                    inputStream3.close();
                    httpURLConnection3.disconnect();
                    Log.i("result3", "[" + result3 + "]");



                    return result3.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                int progressValue = 0;
                double sensorWeight = 0.0;

                try {
                    JSONArray jsonArray = new JSONArray(result);

                    // Check if selectedSensorId is null or empty
                    if (selectedSensorId == null || selectedSensorId.isEmpty()) {
                        // Get the first sensor ID from the JSON response
                        if(jsonArray!=null){
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                selectedSensorId = jsonObject.getString("sensorId");
                            }
                            catch (Exception e){
                                Log.i("userDashBoard iot exception",e.toString());
                            }
                        }
                    }

                    // Find the JSON object with the selected sensor ID
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String sensorId = jsonObject.getString("sensorId");
                        if (sensorId.equals(selectedSensorId)) {
//                            progressValue = jsonObject.getInt("Result");
                            progressValue = jsonObject.getInt("SENSOR_Weight");
                            sensorWeight = jsonObject.getDouble("SENSOR_Weight");
                            break;
                        }
                    }
                    Log.i("progressBar: ", String.valueOf(progressValue));
                    Log.i("sensorWeight: ", String.valueOf(sensorWeight));

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                //updateUI(progressValue, sensorWeight);

//                try {
//                    JSONArray jsonArray = new JSONArray(result);
//                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) iot.getAdapter();
//                    adapter.clear();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String sensorId = jsonObject.getString("sensorId");
//                        adapter.add("Iot Id: " + sensorId);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    private void updateUI(int progressValue, double sensorWeight) {
        // Format progressValue and sensorWeight to 2 decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedSensorWeight = decimalFormat.format(sensorWeight);

        //progressBar.setProgress(progressValue);
        //     VolumeLeft.setText(formattedSensorWeight);

        TextView progressText = findViewById(R.id.progress_text);
        progressText.setText(String.valueOf(decimalFormat.format(sensorWeight) + "%"));
//        progressText.setText(String.valueOf(decimalFormat.format(progressValue) + "%"));
    }


}
