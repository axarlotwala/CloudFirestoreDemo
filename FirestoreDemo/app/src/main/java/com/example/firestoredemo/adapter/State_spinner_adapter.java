package com.example.firestoredemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firestoredemo.R;
import com.example.firestoredemo.models.Country_model;
import com.example.firestoredemo.models.State_model;

import java.util.ArrayList;

public class State_spinner_adapter extends ArrayAdapter<State_model> {


    public State_spinner_adapter(Context context, ArrayList<State_model> state_models) {
        super(context, 0,state_models);
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

        State_model state_model = getItem(position);

        if (state_model != null){
            tv_common_name.setText(state_model.getState());
        }

        return convertView;
    }
}
