package com.example.firestoredemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firestoredemo.R;
import com.example.firestoredemo.models.ClientModel;
import com.example.firestoredemo.models.PartsModel;

import java.util.ArrayList;

public class Parts_spinner_Adapter extends ArrayAdapter<PartsModel> {

    public Parts_spinner_Adapter(Context context, ArrayList<PartsModel> partsModels){
        super(context,0,partsModels);
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

        PartsModel partsModel = getItem(position);

        if (partsModel != null){
            tv_common_name.setText(partsModel.getPartName());
        }
        return convertView;
    }
}
