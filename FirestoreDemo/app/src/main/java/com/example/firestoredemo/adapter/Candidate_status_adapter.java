package com.example.firestoredemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firestoredemo.R;
import com.example.firestoredemo.models.Candidate_status_model;

import java.util.ArrayList;
import java.util.Collection;

public class Candidate_status_adapter extends ArrayAdapter<Candidate_status_model> {

    public Candidate_status_adapter(Context context,ArrayList<Candidate_status_model> models) {
        super(context,0,models);
    }


    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        return InitView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position,View convertView,ViewGroup parent) {
        return InitView(position, convertView, parent);
    }

    private View InitView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_cooman_list,parent,false);
        }

        TextView tv_common_name = convertView.findViewById(R.id.tv_common_name);

        Candidate_status_model candidate_status_model = getItem(position);

        if (candidate_status_model != null){
            tv_common_name.setText(candidate_status_model.getC_status());
        }
        return convertView;
    }


}
