package glotech.smartgasapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import glotech.smartgasapp.R;

import java.util.ArrayList;
import java.util.Locale;

public class OrderDetailListAdapter extends ArrayAdapter<glotech.smartgasapp.OrderDetailItem> {
    int mresource;
    private Context mContext;
    glotech.smartgasapp.OrderDetailItem OrderDetailItem;
    OrderDetail orderDetail;
    int Gas_Delete;
    public OrderDetailListAdapter(Context context, int resource, ArrayList<glotech.smartgasapp.OrderDetailItem> objects){
        super(context, resource, objects);
        mresource = resource;
        mContext = context;
        orderDetail = new OrderDetail();
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        String quantity = getItem(position).getQuantity();
        String type = getItem(position).getType();
        String weight = getItem(position).getWeight();
        Boolean exchangeRemainGas = getItem(position).getExchange();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mresource,parent,false);

        TextView tvQuantity = (TextView) convertView.findViewById(R.id.quantity);
        TextView tvType = (TextView) convertView.findViewById(R.id.type);
        TextView tvWeight = (TextView) convertView.findViewById(R.id.weight);
        Spinner SpGasRemain = (Spinner) convertView.findViewById(R.id.remainGas);

        if(tvQuantity!=null&&tvType!=null&&tvWeight!=null){
            tvQuantity.setText(quantity);
            if(type.toLowerCase(Locale.ROOT).equals("composite")){
                tvType.setText("複合式鋼瓶");
            }
            else if(type.toLowerCase(Locale.ROOT).equals("tradition")){
                tvType.setText("傳統鋼瓶");
            }
            else if(type.equals("類別")){
                tvType.setText("類別");
            }
            else{
                tvType.setText(type);
            }
            tvWeight.setText(weight);
        }

        // 1. 要確認殘氣是否足夠 2. 紀錄兌換的欄位 3. Gas_Delete
        final Boolean[] selection = {false};
        final Boolean[] check = {false};
        SpGasRemain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {

                OrderDetailItem = getItem(position);

                //Log.i("Gas_Delete123",String.valueOf(Gas_Delete));
                //Log.i("orderDetail.Gas_Volume",orderDetail.Gas_Volume);

                if (SpGasRemain.getSelectedItem().toString().equals("兌換")) {
                    if(orderDetail.Gas_Volume != null && !orderDetail.Gas_Volume.isEmpty()){
                        Gas_Delete += Integer.parseInt(OrderDetailItem.getQuantity()) * Integer.parseInt(OrderDetailItem.getWeight());
                        if(Gas_Delete <= Integer.parseInt(orderDetail.Gas_Volume)){
                            selection[0] = true;
                        }
                        else{
                            Toast.makeText(mContext.getApplicationContext(), "殘氣量不足", Toast.LENGTH_SHORT).show();
                            selection[0] = false;
                            SpGasRemain.setSelection(0);
                            Gas_Delete -= Integer.parseInt(OrderDetailItem.getQuantity()) * Integer.parseInt(OrderDetailItem.getWeight());
                        }
                    }
                    else{
                        Toast.makeText(mContext.getApplicationContext(), "殘氣量不足", Toast.LENGTH_SHORT).show();
                        selection[0] = false;
                        SpGasRemain.setSelection(0);
                    }
                    check[0] = true;
                } else {
                    selection[0] = false;
                    if(check[0]){
                        Gas_Delete -= Integer.parseInt(OrderDetailItem.getQuantity()) * Integer.parseInt(OrderDetailItem.getWeight());
                    }
                }

                if(Gas_Delete<0){
                    Gas_Delete = 0;
                }

                Log.i("Gas_Delete",String.valueOf(Gas_Delete));

                OrderDetailItem.setExchange(selection[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Check if it's the last item
        boolean isLastItem = position == getCount() - 1;

        // View for the line at the bottom
        View bottomLine = convertView.findViewById(R.id.bottomLine);

        // Set visibility based on whether it's the last item
        if (bottomLine != null) {
            bottomLine.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
        }

        return convertView;
    }
}
