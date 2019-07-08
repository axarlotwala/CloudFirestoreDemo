package com.example.firestoredemo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.example.firestoredemo.adapter.Parts_spinner_Adapter;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.firestoredemo.Package.Constant;
import com.example.firestoredemo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.apparray.aceta.CompanyActivity;

public class PartsActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    DocumentReference reference;


    TextInputEditText et_part_name,et_weight;
    RadioGroup p_group,remark_group;
    Button btn_save,btn_contract,btn_press,btn_company;
    RadioButton radioButton;

    String group_value,remark_value,partId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts);

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection(Constant.COLLECTION_MFG).document("abc").collection("parts").document();
        partId = reference.getId();


        et_part_name = findViewById(R.id.et_part_name);
        et_weight = findViewById(R.id.et_weight);
        p_group = findViewById(R.id.p_group);
        remark_group = findViewById(R.id.remark_group);
        btn_save = findViewById(R.id.btn_save);
        btn_contract = findViewById(R.id.btn_contract);
        btn_press = findViewById(R.id.btn_press);
        btn_company = findViewById(R.id.btn_company);

        btn_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(PartsActivity.this, SelectItemActivity.class);
                startActivity(intent);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PartsActivity.this,CompanyActivity.class);
                startActivity(intent);
                //SaveParts();
            }
        });

        btn_contract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartsActivity.this,TestActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SweetAlertDialog sd = new SweetAlertDialog(PartsActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                sd.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                sd.setTitleText("Loading");
                sd.setCancelable(true);
                sd.setCanceledOnTouchOutside(true);
                sd.show();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void SaveParts(){

        int selectId = p_group.getCheckedRadioButtonId();
        radioButton = findViewById(selectId);
        group_value = radioButton.getText().toString().trim();


        int remarkId = remark_group.getCheckedRadioButtonId();
        radioButton = findViewById(remarkId);
        remark_value =  radioButton.getText().toString().trim();

        String pname = et_part_name.getText().toString().trim();
        String weight = et_weight.getText().toString().trim();

        if (pname.isEmpty()){
            et_part_name.setError("require partname");
            et_part_name.requestFocus();
            return;
        }

        if (weight.isEmpty()){
            et_weight.setError("require weight");
            et_weight.requestFocus();
            return;
        }

        if (p_group.getCheckedRadioButtonId()<=0 ){
            //Toast.makeText(this, "please select group", Toast.LENGTH_SHORT).show();
            radioButton.setError(null);
        }

        if (remark_group.getCheckedRadioButtonId()<=0){
            //Toast.makeText(this, "please select remark group", Toast.LENGTH_SHORT).show();
            radioButton.setError(null);
        }



        Map<String,Object> partMap = new HashMap<>();
        partMap.put(Constant.PART_ID,partId);
        partMap.put(Constant.PART_NAME,pname);
        partMap.put(Constant.PART_STANDARD_WEIGHT,weight);
        partMap.put(Constant.PART_GROUP,group_value);
        partMap.put(Constant.PART_REMARK,remark_value);

        reference.
                set(partMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
