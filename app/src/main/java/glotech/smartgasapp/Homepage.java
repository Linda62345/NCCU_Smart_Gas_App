package glotech.smartgasapp;

import static glotech.smartgasapp.R.id.navigation_dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import glotech.smartgasapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import glotech.smartgasapp.ui.login.LoginActivity;

public class Homepage extends AppCompatActivity {
    private NotificationFrequency notificationFrequencyInstance;
    private static final long SESSION_TIMEOUT_MILLISECONDS = 2 * 60 * 60 * 1000; // 2 hours in milliseconds
    private static final String SESSION_TIMEOUT_KEY = "session_timeout";

    private Button buy;
    private Button search;
    private Button iot;
    private Button personalBarcode;
    private BottomNavigationView bottomNavigationView;
    private TextView remainGas, showName;
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private ImageAdapter adapter;
    private ArrayList<Bitmap> images;
    public String result = "", Customer_ID;
    public JSONObject responseJSON;
    private ViewPager2 viewPager2;
    private DeviceInfoPagerAdapter adapter1;
    private List<DeviceInfo> fragments;
    private TextView iot1;
    private String selectedSensorId;
    private TokenManager tokenManager;
    private String sensorId = "";
    private double sensorWeight = 0.0;
    private int currentItem = 0; // Initially, the current item index is set to 0.

    private List<String> iot2 = new ArrayList<>();



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

//        notificationFrequencyInstance = new NotificationFrequency();
//        try {
//            notificationFrequencyInstance.frequency();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        tokenManager = TokenManager.getInstance(this);

        // Create sample images
        SliderView sliderView;
        int[] images = {R.drawable.logo,
                R.drawable.logo,
                R.drawable.logo};


        viewPager2 = findViewById(R.id.viewPager2);
        Log.d("TouchEvents", "ViewPager2 created");
        fragments = new ArrayList<>();

//        ArrayList<Double> sensorWeights = new ArrayList<>(iot2.size());
//        for (int i = 0; i < iot2.size(); i++) {
//            String sensorId = iot2.get(i); // Get the sensor ID from your data source
//            sensorWeight = sensorWeights.get(i);
//            fragments.add(DeviceInfo.newInstance(sensorId, sensorWeight));
//            Log.i("sensorId & sensorWeight: " , sensorId+sensorWeight);
//        }



        adapter1 = new DeviceInfoPagerAdapter(this, fragments);
        viewPager2.setAdapter(adapter1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.i("TouchEvents", "Page selected: " + position);
                Log.e("sensor id: " , String.valueOf(iot1));
                // Get the data for the selected IoT device and update the fragment
                //updateFragmentWithData(position);
                //fragment.updateData(sensorId, sensorWeight);
            }
        });
        Log.e("TouchEvents", "An error occurred!");
        Log.e("sensor id: " , String.valueOf(iot1));

        ImageButton prevButton = findViewById(R.id.prevButton);
        ImageButton nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager2.getCurrentItem() > 0) {
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1, true); // Swipe to the previous item.
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager2.getCurrentItem() < adapter1.getItemCount() - 1) {
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1, true); // Swipe to the next item.
                }
            }
        });



        // progressBar = findViewById(R.id.progressBar);
        // iot1 = findViewById(R.id.iot);
        //point = findViewById(R.id.changable_pointButton);
        //homeLogin = findViewById(R.id.loginFromHome);
        // remainGas = findViewById(R.id.changableVol_progress);
        buy = findViewById(R.id.buyGasButton);
        search = findViewById(R.id.findOrderListButton);
        iot = findViewById(R.id.iotButton);

        //location = findViewById(R.id.companyButton);
        personalBarcode = findViewById(R.id.myIDButton);
        bottomNavigationView = findViewById(R.id.nav_view);
        showName = findViewById(R.id.show_name);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Check if the session has timed out
        if (isSessionTimedOut() || !isLoggedIn) {
            // Handle session timeout or user not logged in, redirect to login screen
            redirectToLogin(savedEmail,savedPassword);
            return;
        }

        // Continue with normal data retrieval and processing
        showName.setText("您好，" + username);

        if (!username.isEmpty() && isLoggedIn) {
            // User is logged in, update UI accordingly
            showName.setText("您好，" + username);
        } else {
            // User is not logged in, handle it accordingly (e.g., redirect to login screen)
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Close the current activity
        }

        LoginActivity loginActivity = new LoginActivity();
        showName.setText("您好，" + loginActivity.Customer_Name);

        sliderView = findViewById(R.id.Slider);

        SliderAdapter sliderAdapter = new SliderAdapter(images);

        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();


        LoginActivity loginActivity1 = new LoginActivity();
        Customer_ID = String.valueOf(loginActivity1.getCustomerID());

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
//        adapter.add("Iot Id: ");
//        iot1.setAdapter(adapter);

        NetworkTask networkTask1 = new NetworkTask();
        networkTask1.execute();

//        iot1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedSensor = iot1.getSelectedItem().toString();
//                if (!selectedSensor.equals("Iot Id: ")) {
//                    // Remove the "Iot Id: " part from the selected sensor ID
//                    selectedSensorId = selectedSensor.substring("Iot Id: ".length());
//                    NetworkTask networkTask = new NetworkTask();
//                    networkTask.execute(selectedSensorId);
//                }
//            }


//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        /*point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, PointPage.class);
                startActivity(intent);
            }
        });*/

//        homeLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Homepage.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });

//        moreVol.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Homepage.this, userIot.class);
//                startActivity(intent);
//            }
//        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i("customer id", Customer_ID);
                            showData("http://54.199.33.241/test/Customer_Order_Detail_2.php", Customer_ID);
                            responseJSON = new JSONObject(result);
                            if (responseJSON.getString("response").equals("success")) {
                                Intent intent = new Intent(Homepage.this, OrderDetail.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(Homepage.this, OrderGas.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            Log.i("訂單有無", e.toString());
                        }
                    }
                });

                thread.start();
            }
        });


        // 訂單查詢
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, OrderListUnfinished.class);
                startActivity(intent);
            }
        });


//        location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Homepage.this, GasCompanyLocation.class);
//                startActivity(intent);
//            }
//        });

        personalBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, PersonalBarcode.class);
                startActivity(intent);
            }
        });

        iot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, userIot.class);
                startActivity(intent);
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                        overridePendingTransition(0, 0);
                        NetworkTask networkTask = new NetworkTask();
                        networkTask.execute();
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), Homepage.class));
                        overridePendingTransition(0, 0);
                        NetworkTask networkTask1 = new NetworkTask();
                        networkTask1.execute();

                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), OrderListUnfinished.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        //  makeAuthenticatedApiRequest();
    }


    private String fetchDataFromServer(String customerId) {
        try {
            String Showurl = "http://54.199.33.241/test/Iot_Connect.php";
            URL url = new URL(Showurl);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(customerId, "UTF-8");
            Log.i("customerID: ", post_data);

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            int statusCode = httpURLConnection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line;
                StringBuilder result = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.i("result", "[" + result + "]");
                return result.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean isSessionTimedOut() {
        SharedPreferences sharedPref = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        long sessionStartTime = sharedPref.getLong(SESSION_TIMEOUT_KEY, 0); // 0 means no session start time found
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - sessionStartTime;
        return elapsedTime >= SESSION_TIMEOUT_MILLISECONDS;
    }

    private void redirectToLogin(String savedEmail, String savedPassword) {
        // Clear session data
        SharedPreferences sharedPref = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(SESSION_TIMEOUT_KEY);
        editor.apply();

        // Redirect to the login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("email", savedEmail);
        intent.putExtra("password", savedPassword);
        startActivity(intent);
        finish(); // Close the current activity
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String selectedSensorId = null;
                if (params.length > 0) {
                    selectedSensorId = params[0];
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
                Log.i("customerID: ", post_data3);

                bufferedWriter3.write(post_data3);
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
                iot2.clear();
                fragments.clear();

                try{
                    JSONArray jsonArray = new JSONArray(result);
                    // fragments.clear();

                    for (int i = 0; i < jsonArray.length(); i++)  {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        selectedSensorId = jsonObject.getString("sensorId");
                        String sensorId = jsonObject.getString("sensorId");
                        iot2.add(sensorId);

                        sensorWeight = jsonObject.getDouble("SENSOR_Weight");
                        DeviceInfo fragment = DeviceInfo.newInstance(sensorId, sensorWeight);
                        // fragments.add(fragment);

                        fragments.add(DeviceInfo.newInstance(sensorId, sensorWeight));
                        if (sensorId.equals(selectedSensorId)) {
                            progressValue = (int) sensorWeight;
                            sensorWeight = jsonObject.getDouble("SENSOR_Weight");
                        }
                        //updateUI((int) sensorWeight, sensorWeight);

                        Log.i("sensorId:", sensorId);
                        Log.i("progressBar: ", String.valueOf(progressValue));
                        Log.i("sensorWeight: ", String.valueOf(sensorWeight));

                        // Update the UI with the new IoT data (sensorId and sensorWeight).
                        // updateUI( progressValue, sensorWeight);
                        // updateFragmentWithData(viewPager2.getCurrentItem());
                    }

                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            Log.i("TouchEvents", "Page selected: " + position);
                            // Get the data for the selected IoT device and update the fragment
                            //updateFragmentWithData(position);
                        }
                    });

                    // Notify the adapter of data changes
                    adapter1.notifyDataSetChanged();

                    if (!fragments.isEmpty()) {
                        DeviceInfo firstFragment = fragments.get(0);
                        //updateUI((int) firstFragment.getSensorWeight(), firstFragment.getSensorWeight());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void updateUI(int progressValue, double sensorWeight) {
        //TextView iotId = findViewById(R.id.iot);
//        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
//        String formattedSensorWeight = decimalFormat.format(sensorWeight);

        // this.progressValue = progressValue;
        //this.sensorWeight = sensorWeight;

        // progressBar.setProgress(progressValue);
        Log.i("progress Value", String.valueOf(progressValue));
        // remainGas.setText(formattedSensorWeight);

//        TextView progressText = findViewById(R.id.progress_text);
//        DecimalFormat decimalFormat1 = new DecimalFormat("#0.00");
//        String formattedProgressValue = decimalFormat1.format(sensorWeight);
//        progressText.setText(formattedProgressValue + "%");

    }


    private void saveAccessToken(String accessToken) throws GeneralSecurityException, IOException {
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                "my_secure_prefs",
                masterKeyAlias,  // Your master key alias
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", accessToken);
        editor.apply();
    }

    public void showData(String Showurl, String id) {
        try {
            URL url = new URL(Showurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String line = "";
            result = "";

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.i("result", "[" + result + "]");
        } catch (Exception e) {
            Log.i("Order detail", e.toString());
        }
    }
}