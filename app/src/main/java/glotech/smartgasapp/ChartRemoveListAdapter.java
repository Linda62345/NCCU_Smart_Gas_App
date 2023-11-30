package glotech.smartgasapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import glotech.smartgasapp.R;

import java.util.ArrayList;
import java.util.Locale;

public class ChartRemoveListAdapter extends ArrayAdapter<glotech.smartgasapp.OrderDetailItem> {
    int mresource;
    private Context mContext;

    glotech.smartgasapp.OrderDetailItem OrderDetailItem;
    OrderDetail orderDetail;
    int Gas_Delete;

    public ChartRemoveListAdapter(Context context, int resource, ArrayList<glotech.smartgasapp.OrderDetailItem> objects) {
        super(context, resource, objects);
        mresource = resource;
        mContext = context;
        orderDetail = new OrderDetail();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String quantity = getItem(position).getQuantity();
        String type = getItem(position).getType();
        String weight = getItem(position).getWeight();

        //  Boolean exchangeRemainGas = getItem(position).getExchange();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mresource, parent, false);

        TextView tvQuantity = (TextView) convertView.findViewById(R.id.quantity);
        TextView tvType = (TextView) convertView.findViewById(R.id.type);
        TextView tvWeight = (TextView) convertView.findViewById(R.id.weight);
        Button delete = (Button) convertView.findViewById(R.id.deleteButton);
        //Spinner SpGasRemain = (Spinner) convertView.findViewById(R.id.remainGas);

        if (tvQuantity != null && tvType != null && tvWeight != null) {
            tvQuantity.setText(quantity);
            if (type.toLowerCase(Locale.ROOT).equals("composite")) {
                tvType.setText("複合式鋼瓶");
            } else if (type.toLowerCase(Locale.ROOT).equals("tradition")) {
                tvType.setText("傳統鋼瓶");
            } else if (type.equals("類別")) {
                tvType.setText("類別");
            } else {
                tvType.setText(type);
            }
            tvWeight.setText(weight);
        }

        int intQuantity = Integer.parseInt(quantity);
        if (intQuantity > 1) {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Deduct the quantity by 1
                    getItem(position).setQuantity(String.valueOf(intQuantity - 1));
                    notifyDataSetChanged();
                }
            });
        } else {
            // If the quantity is not greater than 2, remove the item directly
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(getItem(position));
                    notifyDataSetChanged();
                }
            });
        }


            // 1. 要確認殘氣是否足夠 2. 紀錄兌換的欄位 3. Gas_Delete
            final Boolean[] selection = {false};
            final Boolean[] check = {false};


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
