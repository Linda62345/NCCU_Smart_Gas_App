package glotech.smartgasapp;

import static glotech.smartgasapp.R.id.navigation_dashboard;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import glotech.smartgasapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import glotech.smartgasapp.ui.login.LoginActivity;

public class OrderListFinished extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener pickerDialog;
    Calendar calendar2 = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener pickerDialog2;
    private Button unfinished,backButton;
    private ListView orderList;
    public String Customer_Id, start_date, end_date;
    InputStream is = null;
    String[] data,order_Id;
    Date startDate, endDate;
    TextView startDateChangeable, endDateChangeable;
    public static String static_order_id;
    EditText startYearEditText, startMonthEditText, startDateEditText, endYearEditText, endMonthEditText, endDateEditText;
    private String URL = "http://54.199.33.241/test/customer_OrderList.php";
    ArrayList<OrderListFinishList> orderListFinishLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_finished);

        

        unfinished = findViewById(R.id.order_unfinished);
        backButton = findViewById(R.id.backButton);

        unfinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderListFinished.this, OrderListUnfinished.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderListFinished.this, Homepage.class);
                startActivity(intent);
            }
        });

        startDateChangeable = findViewById(R.id.startDateChangeable);
        endDateChangeable = findViewById(R.id.endDateChangeable);

        // 日期選擇
        Date();
        startDateChangeable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(v);
            }
        });
        // 日期選擇
        Date2();
        endDateChangeable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker2(v);
            }
        });

        Button enterButton = findViewById(R.id.enterSearch);
        enterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginActivity loginActivity = new LoginActivity();
                Customer_Id = String.valueOf(loginActivity.getCustomerID());
                Log.i("finish: ",Customer_Id);

                Log.i("End Date:", end_date);
                Log.i("Start Date:", start_date);

                orderList = (ListView)findViewById(R.id.list_item);
                StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

                orderList.setAdapter(null);
                try {
                    getData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                setAdapter();

            }
        });

        orderListFinishLists = new ArrayList<OrderListFinishList>();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_notifications);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case navigation_dashboard:
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

    private void setAdapter() {
        if(orderListFinishLists.size()>0){
            OrderListFinishAdapterList adapter=
                    new OrderListFinishAdapterList(getApplicationContext(),R.layout.adapter_list_un_finish,orderListFinishLists);
            orderList.setAdapter(adapter);
            orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //當備案下時
                    String msg = data[position];
                    //Toast.makeText(OrderListFinished.this, msg, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrderListFinished.this, SearchOrderResultFinished.class);
                    String Id = order_Id[position];
                    static_order_id = Id;
                    startActivity(intent);
                }
            });
            orderListFinishLists = new ArrayList<OrderListFinishList>();
        }
        else{
            Toast.makeText(this, "無訂單", Toast.LENGTH_SHORT).show();
        }

    }

    private void getData() throws IOException {
        //還要把customer Id丟過去

        String Showurl = "http://54.199.33.241/test/customer_OrderList.php";
        URL url = new URL(Showurl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        String post_data1 = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(Customer_Id), "UTF-8");
        String post_data2 = URLEncoder.encode("start_date", "UTF-8") + "=" + URLEncoder.encode(start_date, "UTF-8")
                + "&" + URLEncoder.encode("end_date", "UTF-8") + "=" + URLEncoder.encode(end_date, "UTF-8");
        Log.i("post_data1: ", post_data1);
        Log.i("post_data2: ", post_data2);

        bufferedWriter.write(post_data1);
        bufferedWriter.write("&");
        bufferedWriter.write(post_data2);
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
        Log.i("result here", "["+result+"]");
        try{
            Log.i("result try","["+result+"]");
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;

            orderListFinishLists.clear();

            data = new String[ja.length()];
            order_Id = new String[ja.length()];

            for(int i = 0; i<ja.length();i++){
                jo = ja.getJSONObject(i);
                String orderTime = jo.getString("Order_Time");

                OrderListFinishList order = new OrderListFinishList(orderTime, "已完成");
                orderListFinishLists.add(order);

                Log.i("order data", order.getTime());
                order_Id[i] = jo.getString("ORDER_Id");

                data[i] = "訂購時間: " + orderTime + " - " + "已完成";

                Log.i("order data",data[i]);
                order_Id[i] = jo.getString("ORDER_Id");
            }
        }catch(Exception e){
            Log.i("OrderList JSON Exception",e.toString());
        }
    }

    public void Date(){
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(timeZone);

        // Format the current date and time as a string in the correct format
        String currentDateTimeString = dateFormat.format(new Date());
        startDateChangeable.setText(currentDateTimeString);
        start_date = currentDateTimeString;
        pickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DATE,dayOfMonth);
                start_date = year+"-"+(month+1)+"-"+dayOfMonth;
                startDateChangeable.setText(year+"-"+(month+1)+"-"+dayOfMonth);
            }
        };
    }

    public void datePicker(View v){
        //建立date的dialog
        DatePickerDialog dialog = new DatePickerDialog(v.getContext(),
                pickerDialog,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void Date2(){
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(timeZone);

        // Format the current date and time as a string in the correct format
        String currentDateTimeString = dateFormat.format(new Date());
        endDateChangeable.setText(currentDateTimeString);
        end_date = currentDateTimeString;
        pickerDialog2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar2.set(Calendar.YEAR,year);
                calendar2.set(Calendar.MONTH,month);
                calendar2.set(Calendar.DATE,dayOfMonth);
                end_date = year+"-"+(month+1)+"-"+dayOfMonth;
                endDateChangeable.setText(year+"-"+(month+1)+"-"+dayOfMonth);
            }
        };
    }

    public void datePicker2(View v){
        //建立date的dialog
        DatePickerDialog dialog = new DatePickerDialog(v.getContext(),
                pickerDialog2,
                calendar2.get(Calendar.YEAR),
                calendar2.get(Calendar.MONTH),
                calendar2.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

}