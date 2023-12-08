package glotech.smartgasapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompanyNameAdapter extends ArrayAdapter<Company> {

    int mresource;
    private Context mContext;

    public CompanyNameAdapter(Context context, int resource, ArrayList<Company> objects){
        super(context, resource, objects);
        mresource = resource;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getCompanyName());
        return view;
    }
}
