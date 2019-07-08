package com.example.firestoredemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firestoredemo.R;
import com.example.firestoredemo.models.Country_model;

import java.util.ArrayList;

public class Country_spinner_adapter extends ArrayAdapter<Country_model> {


    public Country_spinner_adapter(Context context, ArrayList<Country_model> country_models) {
        super(context, 0,country_models);
    }



    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        return InitView(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return InitView(position, convertView, parent);
    }

    private View InitView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_cooman_list,parent,false);
        }

        TextView tv_common_name = convertView.findViewById(R.id.tv_common_name);

        Country_model country_model = getItem(position);

        if (country_model != null){
            tv_common_name.setText(country_model.getName());
        }

        return convertView;
    }
}
