package glotech.smartgasapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import glotech.smartgasapp.R;

import java.util.List;

public class IotAdapter extends RecyclerView.Adapter<IotAdapter.ViewHolder> {
    private final OnItemClickListener listener;
    private List<String> iotIds;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String iotId);
    }

    public IotAdapter(List<String> iotIds, OnItemClickListener listener) {
        this.iotIds = iotIds;
        //this.onItemClickListener = onItemClickListener;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iot_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String iotId = iotIds.get(position);
        holder.iotIdTextView.setText(iotId);

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(iotId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return iotIds.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView iotIdTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iotIdTextView = itemView.findViewById(R.id.iotView);
        }
    }
}
