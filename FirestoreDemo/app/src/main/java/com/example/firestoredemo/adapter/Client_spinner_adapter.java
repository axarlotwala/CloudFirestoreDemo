package com.example.firestoredemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firestoredemo.R;
import com.example.firestoredemo.models.ClientModel;

import java.util.ArrayList;

public class Client_spinner_adapter extends ArrayAdapter<ClientModel> {

    public Client_spinner_adapter(Context context, ArrayList<ClientModel> clientModels){
        super(context,0,clientModels);
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
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

        ClientModel clientModel = getItem(position);

        if (clientModel != null){
            tv_common_name.setText(clientModel.getClientName());
        }
        return convertView;
    }


}
