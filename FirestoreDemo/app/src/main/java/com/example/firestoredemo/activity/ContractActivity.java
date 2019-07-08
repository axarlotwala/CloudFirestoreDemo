package com.example.firestoredemo.activity;

import android.app.DatePickerDialog;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.firestoredemo.Package.Constant;
import com.example.firestoredemo.R;
import com.example.firestoredemo.adapter.Client_spinner_adapter;
import com.example.firestoredemo.adapter.Parts_spinner_Adapter;
import com.example.firestoredemo.models.ClientModel;
import com.example.firestoredemo.models.PartsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import in.apparray.mylibrary.classes.SelectSpinner;
import in.apparray.mylibrary.classes.SpinnerListener;

public class ContractActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    DocumentReference reference;

    TextInputEditText et_contract_name, et_desc, et_quantity, et_weight,et_kanban_date;
    Spinner spinner_client, spinner_parts;
    String clientId,clientName,contractId,partId,partName,group,remark,contract_name,part_desc,quantity,stdWeight;
    int contractNo,year,month,date,dayofmonth;
    Button btn_Save;
    Button btn_select_date;
    SelectSpinner ssClient;

    ArrayList<ClientModel> clientModels;
    ArrayList<HashMap<String, Object>> clientList;
    ArrayList<PartsModel> partsModels;

    Client_spinner_adapter client_spinner_adapter;
    Parts_spinner_Adapter parts_spinner_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection(Constant.COLLECTION_MFG).document("abc").collection("contractsBook").document();
        contractId = reference.getId();

        et_contract_name = findViewById(R.id.et_contract_name);
        et_desc = findViewById(R.id.et_desc);
        et_quantity = findViewById(R.id.et_quantity);
        et_weight = findViewById(R.id.et_weight);
        et_kanban_date = findViewById(R.id.et_kanban_date);

        spinner_client = findViewById(R.id.spinner_client);
        spinner_parts = findViewById(R.id.spinner_parts);
        ssClient = findViewById(R.id.ssClient);

        btn_Save = findViewById(R.id.btn_save);
        btn_select_date = findViewById(R.id.btn_select_date);

        FillClients();
        FillParts();

        btn_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog;
                dialog = new DatePickerDialog(ContractActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_kanban_date.setText(year + "-" +(month+1) + "-"+dayOfMonth);
                    }
                },year,month,dayofmonth);
                dialog.show();

            }
        });

        spinner_client.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                clientId = clientModels.get(position).getClientId();
                clientName = clientModels.get(position).getClientName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_parts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                partId = partsModels.get(position).getPartId();
                partName = partsModels.get(position).getPartName();
                group = partsModels.get(position).getGroup();
                remark = partsModels.get(position).getRemark();
                stdWeight = ""+partsModels.get(position).getStdWeight();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveContract();
            }
        });



    }

    private void FillClients() {

                firestore.collection(Constant.COLLECTION_MFG).document("abc").collection("clients")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                clientModels = new ArrayList<>();
                                clientList = new ArrayList<>();

                                if (!queryDocumentSnapshots.isEmpty()){

                                    ArrayList<DocumentSnapshot> documentSnapshots = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();

                                    for (DocumentSnapshot snapshot : documentSnapshots){
                                        clientList.add((HashMap<String, Object>) snapshot.getData());
                                        ClientModel clientModel = snapshot.toObject(ClientModel.class);
                                        clientModels.add(clientModel);
                                    }

                                    for (int i=0;i<array.size();i++){

                                    }

                                     client_spinner_adapter = new Client_spinner_adapter(ContractActivity.this, clientModels);
                                     spinner_client.setAdapter(client_spinner_adapter);

                                }
                               // ssClient.setItems(clientList, "");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

    }

    private void FillParts() {


        firestore.collection(Constant.COLLECTION_MFG).document("abc").collection("parts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        partsModels = new ArrayList<>();

                        if (!queryDocumentSnapshots.isEmpty()){

                            ArrayList<DocumentSnapshot> documents  = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot : documents){
                                PartsModel partsModel = snapshot.toObject(PartsModel.class);
                                partsModels.add(partsModel);
                            }

                            parts_spinner_adapter = new Parts_spinner_Adapter(ContractActivity.this,partsModels);
                            spinner_parts.setAdapter(parts_spinner_adapter);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void SaveContract(){

        Random random = new Random();
        contractNo = 100000 + random.nextInt(900000);

        contract_name = et_contract_name.getText().toString().trim();
        part_desc = et_desc.getText().toString().trim();
        quantity = et_quantity.getText().toString().trim();
        stdWeight = et_weight.getText().toString().trim();

        Map<String,Object> contractMap = new HashMap<>();
        contractMap.put(Constant.CONTRACT_ID,contractId);
        contractMap.put(Constant.CONTRACT_NAME,contract_name);
        contractMap.put(Constant.CONTRACT_NO,contractNo);
        contractMap.put(Constant.CONTRACT_QUANTITY,quantity);
        contractMap.put(Constant.STATE,"-");
        contractMap.put(Constant.STD_WEIGHT,stdWeight);
        contractMap.put(Constant.PART_DESC,part_desc);
        contractMap.put(Constant.PARTS,null);
        contractMap.put(partId,null);

        HashMap<String,Object> partmap = new HashMap<>();
        partmap.put(Constant.PART_ID,partId);
        partmap.put(Constant.PART_NAME,partName);
        partmap.put(Constant.PART_GROUP,group);
        partmap.put(Constant.STD_WEIGHT,stdWeight);

        contractMap.put("nested",partmap);

        reference
                .set(contractMap)
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
