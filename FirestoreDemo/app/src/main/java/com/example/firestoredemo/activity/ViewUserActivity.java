package com.example.firestoredemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.example.firestoredemo.Package.Constant;
import com.example.firestoredemo.R;
import com.example.firestoredemo.adapter.User_adapter;
import com.example.firestoredemo.models.Member_model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewUserActivity extends AppCompatActivity {


    RecyclerView user_list;
    private ArrayList<Member_model> member_models;
    LinearLayoutManager linearLayoutManager;
    User_adapter user_adapter;
    FirebaseFirestore firestore;
    String snap_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        firestore = FirebaseFirestore.getInstance();
        user_list = findViewById(R.id.user_list);

        SetUSerList();
    }

    private void SetUSerList(){

        firestore.collection(Constant.COLLECTION_MEMBER)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        member_models = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()){

                            ArrayList<DocumentSnapshot> snapshots = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : snapshots){
                                Member_model membermodel = snapshot.toObject(Member_model.class);
                                Log.e("snapId", "onSuccess: "+snapshot.getId());
                                snap_id = snapshot.getId();
                                member_models.add(membermodel);
                             }

                            linearLayoutManager = new LinearLayoutManager(ViewUserActivity.this);
                            user_list.setLayoutManager(linearLayoutManager);
                            user_adapter = new User_adapter(ViewUserActivity.this,member_models);
                            user_list.setAdapter(user_adapter);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("getUser_error", "onFailure: "+e.toString());
                    }
                });

    }
}
