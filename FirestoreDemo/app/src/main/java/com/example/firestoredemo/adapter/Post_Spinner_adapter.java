package com.example.firestoredemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firestoredemo.R;
import com.example.firestoredemo.models.SelectPost_model;

import java.util.ArrayList;

public class Post_Spinner_adapter extends ArrayAdapter<SelectPost_model> {


    public Post_Spinner_adapter(Context context, ArrayList<SelectPost_model> postModels){
        super(context,0,postModels);
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        return InitView(position, convertView, parent);
    }



    @Override
    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
        return InitView(position, convertView, parent);
    }

    private View InitView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_cooman_list,parent,false);
        }

        TextView tv_common_name = convertView.findViewById(R.id.tv_common_name);


        SelectPost_model selectPost_model  = getItem(position);

        if (selectPost_model != null){
            tv_common_name.setText(selectPost_model.getPost());
        }
        return convertView;
    }
}
