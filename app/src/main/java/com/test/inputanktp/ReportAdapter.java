package com.test.inputanktp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private Context context;
    private List<DataModal> dataList;
    private OnItemLongClickListener longClickListener;

    // Interface untuk mendeteksi Long Click pada item
    public interface OnItemLongClickListener {
        void onItemLongClick(DataModal data, int position);
    }

    public ReportAdapter(Context context, List<DataModal> dataList, OnItemLongClickListener longClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModal item = dataList.get(position);
        holder.tvNama.setText(item.getNama());
        holder.tvNik.setText("NIK: " + item.getNik());
        holder.tvAlamat.setText("Alamat: " + item.getAlamat());

        // Event Long Click pada CardView
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(item, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void filterList(List<DataModal> filteredList) {
        this.dataList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvNik, tvAlamat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvNik = itemView.findViewById(R.id.tv_nik);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
        }
    }
}