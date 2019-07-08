package com.example.firestoredemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firestoredemo.R;
import com.example.firestoredemo.models.City_model;
import com.example.firestoredemo.models.Country_model;

import java.util.ArrayList;

public class City_spinner_adapter  extends ArrayAdapter<City_model> {


    public City_spinner_adapter(Context context, ArrayList<City_model> city_models) {
        super(context, 0,city_models);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

        City_model city_model = getItem(position);

        if (city_model != null){
            tv_common_name.setText(city_model.getCity_name());
        }

        return convertView;
    }
}
