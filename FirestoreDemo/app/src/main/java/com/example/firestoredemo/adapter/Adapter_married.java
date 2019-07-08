package com.example.firestoredemo.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.firestoredemo.R;
import com.example.firestoredemo.models.Marrital_status_model;

import java.util.ArrayList;

public class Adapter_married extends RecyclerView.Adapter<Adapter_married.Holder> {

    private Context context;
    private ArrayList<Marrital_status_model> statusModels;
    private int mSelected_ID = -1;
    String mstatus;



    public Adapter_married(Context context, ArrayList<Marrital_status_model> statusModels) {
        this.context = context;
        this.statusModels = statusModels;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_marriedstatus_rowlist, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        mstatus = statusModels.get(i).getStatus();
        holder.m_status.setText(statusModels.get(i).getStatus());
        holder.m_status.setChecked(mSelected_ID == i);

    }

    @Override
    public int getItemCount() {
        return statusModels.size();
    }



    class Holder extends RecyclerView.ViewHolder {


        RadioButton m_status;

        public Holder(@NonNull View itemView) {
            super(itemView);

            m_status = itemView.findViewById(R.id.m_status);

            m_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mSelected_ID = getAdapterPosition();
                    notifyDataSetChanged();

                    Toast.makeText(context,m_status.getText().toString(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


}
