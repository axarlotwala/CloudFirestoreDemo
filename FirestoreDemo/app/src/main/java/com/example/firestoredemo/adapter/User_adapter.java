package com.example.firestoredemo.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firestoredemo.R;
import com.example.firestoredemo.activity.MainActivity;
import com.example.firestoredemo.models.Member_model;

import java.util.ArrayList;

public class User_adapter extends RecyclerView.Adapter<User_adapter.ViewHolder> {

    private Context context;
    private ArrayList<Member_model> memberModel;

    public User_adapter(Context context, ArrayList<Member_model> memberModel) {
        this.context = context;
        this.memberModel = memberModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {

        final String docId = memberModel.get(i).getDocumentId();
        viewHolder.tv_username.setText(memberModel.get(i).getFirst_name());
        viewHolder.linear_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("docid",docId);
                intent.putExtra("firstname",memberModel.get(i).getFirst_name());
                intent.putExtra("middlename",memberModel.get(i).getMiddle_name());
                intent.putExtra("surname",memberModel.get(i).getSurname());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberModel.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linear_user;
        TextView tv_username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linear_user = itemView.findViewById(R.id.linear_user);
            tv_username = itemView.findViewById(R.id.tv_username);
        }
    }
}
