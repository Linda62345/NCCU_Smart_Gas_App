package glotech.smartgasapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import glotech.smartgasapp.R;

import java.text.DecimalFormat;

public class DeviceInfo extends Fragment {

    private String iotId;
    private String selectedSensorId;
    private int progressValue;
    private double sensorWeight;
    private TextView iotIdTextView;
    private TextView gasWeightTextView;

    public static DeviceInfo newInstance(String iotId, double sensorWeight) {
        DeviceInfo fragment = new DeviceInfo();
        fragment.iotId = iotId;
        fragment.sensorWeight = sensorWeight;
        Bundle args = new Bundle();
        args.putString("iotId", iotId);
        args.putDouble("sensorWeight", sensorWeight);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout and populate it with data from arguments
        View view = inflater.inflate(R.layout.layout_viewpayer, container, false);
        String iotId = getArguments().getString("iotId");
        // int progressValue = getArguments().getInt("progressValue");
        sensorWeight = getArguments().getDouble("sensorWeight");

        // Update UI elements with iotId and gasWeight
        TextView iotIdTextView = view.findViewById(R.id.iot);
        iotIdTextView.setText("Iot Id: " + iotId);
        Log.i("iot: " , iotId);

        TextView gasWeightTextView = view.findViewById(R.id.progress_text);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedSensorWeight = decimalFormat.format(sensorWeight);
        gasWeightTextView.setText(formattedSensorWeight + "%");
        Log.i("weight: ", String.valueOf(sensorWeight));
        ProgressBar progressBar = view.findViewById(R.id.progressBar); // Replace with the actual ID of your progress bar
        progressBar.setProgress((int) sensorWeight);

        updateUI();
        return view;
    }

    public void updateData(String sensorId, double updatedSensorWeight) {
        if (iotId.equals(sensorId)) {
            sensorWeight = updatedSensorWeight;
            progressValue = (int) updatedSensorWeight;

            if (getView() != null) {
                TextView gasWeightTextView = getView().findViewById(R.id.progress_text);
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                String formattedSensorWeight = decimalFormat.format(updatedSensorWeight);
                gasWeightTextView.setText(formattedSensorWeight + "%");
            }

            if (getView() != null) {
                ProgressBar progressBar = getView().findViewById(R.id.progressBar); // Replace with the actual ID of your progress bar
                progressBar.setProgress((int) updatedSensorWeight);
            }
        }
    }

    private void updateUI() {
        if (iotIdTextView != null && gasWeightTextView != null) {
            iotIdTextView.setText("Iot Id: " + iotId);
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            String formattedSensorWeight = decimalFormat.format(sensorWeight);
            gasWeightTextView.setText(formattedSensorWeight + "%");
        }
    }


    public String getIotId() {
        return iotId;
    }


    public int getProgressValue() {
        return progressValue;
    }

    public double getSensorWeight() {
        return sensorWeight;
    }
}

