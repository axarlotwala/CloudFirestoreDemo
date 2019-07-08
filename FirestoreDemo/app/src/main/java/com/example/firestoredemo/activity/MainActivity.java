package com.example.firestoredemo.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.firestoredemo.Package.Constant;
import com.example.firestoredemo.R;
import com.example.firestoredemo.adapter.BloodGroup_spinner_adapter;
import com.example.firestoredemo.adapter.Candidate_status_adapter;
import com.example.firestoredemo.adapter.City_spinner_adapter;
import com.example.firestoredemo.adapter.Country_spinner_adapter;
import com.example.firestoredemo.adapter.Post_Spinner_adapter;
import com.example.firestoredemo.adapter.State_spinner_adapter;
import com.example.firestoredemo.models.Bloodgroup_model;
import com.example.firestoredemo.models.Candidate_status_model;
import com.example.firestoredemo.models.City_model;
import com.example.firestoredemo.models.Country_model;
import com.example.firestoredemo.models.SelectPost_model;
import com.example.firestoredemo.models.State_model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore fire_store;
    DocumentReference reference;

    LinearLayout first_frame, second_frame, third_frame;
    FloatingActionButton btn_personal_detail, btn_address;
    Button btn_save, btn_view_user;
    Spinner post_spinner, status_spinner, spinner_country, spinner_state, spinner_city, spinner_blood_group;
    RadioGroup groups_select, group_mstatus;
    TextInputEditText et_first_name, et_middle_name, et_surname, et_street_address, et_landmark, et_pincode, et_area, et_phone_no, et_email_address, et_child_name;
    TextInputEditText et_dob;
    RadioButton radioButton, radio_single, radio_married, radio_widow, radio_separated, radio_divorced, radio_mr, radio_ms;
    RelativeLayout relative_save;
    Button btn_dob;


    String select_post_name, candidate_status, get_gender, firstname, middlename, surname, s_address, landmark, pin, area, phone, email_address;
    String country_name, state_name, city_name, dateofbirth, mStatus, gender, bloodgroup,document_id;

    int dayofmonth, year, month, date, current_year, age;

    ArrayList<SelectPost_model> post_modelList;
    ArrayList<Candidate_status_model> status_models;
    ArrayList<Country_model> country_models;
    ArrayList<State_model> state_models;
    ArrayList<City_model> city_models;
    ArrayList<Bloodgroup_model> bloodgroup_models;


    Post_Spinner_adapter post_spinner_adapter;
    Candidate_status_adapter status_adapter;
    Country_spinner_adapter countrySpinnerAdapter;
    State_spinner_adapter state_spinner_adapter;
    City_spinner_adapter city_spinner_adapter;
    BloodGroup_spinner_adapter bloodGroup_spinner_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Linear Layout*/
        first_frame = findViewById(R.id.first_frame);
        second_frame = findViewById(R.id.second_frame);
        third_frame = findViewById(R.id.third_frame);

        /*Relative Layout*/
        relative_save = findViewById(R.id.relative_save);

        /*Buttons*/
        btn_personal_detail = findViewById(R.id.btn_personal_detail);
        btn_address = findViewById(R.id.btn_address);
        btn_view_user = findViewById(R.id.btn_view_user);

        btn_save = findViewById(R.id.btn_save);
        btn_dob = findViewById(R.id.btn_dob);

        /*Spinner*/
        post_spinner = findViewById(R.id.post_spinner);
        status_spinner = findViewById(R.id.status_spinner);
        spinner_country = findViewById(R.id.spinner_country);
        spinner_city = findViewById(R.id.spinner_city);
        spinner_state = findViewById(R.id.spinner_state);
        spinner_blood_group = findViewById(R.id.spinner_blood_group);


        /*Edittext*/
        et_first_name = findViewById(R.id.et_first_name);
        et_middle_name = findViewById(R.id.et_middle_name);
        et_surname = findViewById(R.id.et_surname);
        et_street_address = findViewById(R.id.et_street_address);
        et_landmark = findViewById(R.id.et_landmark);
        et_pincode = findViewById(R.id.et_pincode);
        et_area = findViewById(R.id.et_area);
        et_phone_no = findViewById(R.id.et_phone_no);
        et_email_address = findViewById(R.id.et_email_address);
        et_dob = findViewById(R.id.et_dob);

        /*RadioGroup*/
        groups_select = findViewById(R.id.groups_select);
        group_mstatus = findViewById(R.id.group_mstatus);

        /*RadioButton*/
        radio_single = findViewById(R.id.radio_single);
        radio_married = findViewById(R.id.radio_married);
        radio_widow = findViewById(R.id.radio_widow);
        radio_separated = findViewById(R.id.radio_separated);
        radio_divorced = findViewById(R.id.radio_divorced);
        radio_mr = findViewById(R.id.radio_mr);
        radio_ms = findViewById(R.id.radio_ms);


        fire_store = FirebaseFirestore.getInstance();
        reference = fire_store.collection(Constant.COLLECTION_MEMBER).document();
        document_id = reference.getId();
        /*get firestore auto generated id and put it*/


        Fill_select_post();
        Fill_Candidate_status();
        Fill_Country();
        Fill_State();
        Fill_City();
        Fill_BloodGroup();


        btn_view_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ViewUserActivity.class);
                startActivity(intent);
                finish();
            }
        });


        post_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                select_post_name = post_modelList.get(position).getPost();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                candidate_status = status_models.get(position).getC_status();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                country_name = country_models.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                state_name = state_models.get(position).getState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                city_name = city_models.get(position).getCity_name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_blood_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                bloodgroup = bloodgroup_models.get(position).getGroup();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("docid");
        String fname = intent.getStringExtra("firstname");
        String mname = intent.getStringExtra("middlename");
        String sname = intent.getStringExtra("surname");

        et_first_name.setText(fname);
        et_middle_name.setText(mname);
        et_surname.setText(sname);


        btn_personal_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Log.e("getname", "onClick: "+member_model.getFirst_name());

                firstname = et_first_name.getText().toString().trim();
                middlename = et_middle_name.getText().toString().trim();
                surname = et_surname.getText().toString().trim();

                if (firstname.isEmpty()) {
                    et_first_name.setError("require firstname");
                    et_first_name.requestFocus();
                    return;
                }

                if (middlename.isEmpty()) {
                    et_middle_name.setError("require middlename");
                    et_middle_name.requestFocus();
                    return;
                }

                if (surname.isEmpty()) {
                    et_surname.setError("require surname");
                    et_surname.requestFocus();
                    return;
                }

                int Select_id = groups_select.getCheckedRadioButtonId();
                radioButton = findViewById(Select_id);
                get_gender = radioButton.getText().toString();

                groups_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (radio_mr.isChecked()) {
                            gender = "male";
                        } else if (radio_ms.isChecked()) {
                            gender = "female";
                        } else {

                        }


                    }
                });

                first_frame.setVisibility(View.GONE);
                second_frame.setVisibility(View.VISIBLE);
            }
        });

        btn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                s_address = et_street_address.getText().toString().trim();
                landmark = et_landmark.getText().toString().trim();
                pin = et_pincode.getText().toString().trim();
                area = et_area.getText().toString().trim();

                if (s_address.isEmpty()) {
                    et_street_address.setError("require address");
                    et_street_address.requestFocus();
                    return;
                }

                if (landmark.isEmpty()) {
                    et_landmark.setError("require landmark");
                    et_landmark.requestFocus();
                    return;
                }

                if (pin.isEmpty()) {
                    et_pincode.setError("require pincode");
                    et_pincode.requestFocus();
                    return;
                }

                if (area.isEmpty()) {
                    et_area.setError("require area");
                    et_area.requestFocus();
                    return;
                }

                second_frame.setVisibility(View.GONE);
                third_frame.setVisibility(View.VISIBLE);

            }
        });

        btn_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog;
                current_year = Calendar.getInstance().get(Calendar.YEAR);
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                date = calendar.get(Calendar.DATE);
                dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

                dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        et_dob.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        age = current_year - year;
                        Log.e("age", "onDateSet: " + age);

                    }
                }, year, month, dayofmonth);
                dialog.show();

                // dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SaveUSer();
            }
        });
    }


    private void Fill_select_post() {

        post_modelList = new ArrayList<>();
        fire_store.collection(Constant.COLLECTION_SELECT_POST)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            ArrayList<DocumentSnapshot> snapshotList = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                            Log.e("getPost", "onSuccess: " + snapshotList.toString());

                            for (DocumentSnapshot documentSnapshot : snapshotList) {
                                SelectPost_model selectPostModel = documentSnapshot.toObject(SelectPost_model.class);
                                post_modelList.add(selectPostModel);
                                Log.e("select_post", "onSuccess: " + documentSnapshot.getString("post"));
                            }
                            post_spinner_adapter = new Post_Spinner_adapter(MainActivity.this, post_modelList);
                            post_spinner.setAdapter(post_spinner_adapter);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void Fill_Candidate_status() {

        status_models = new ArrayList<>();
        fire_store.collection(Constant.COLLECTION_CANDIDATE_STATUS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            ArrayList<DocumentSnapshot> snapshotList = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                            Log.e("getPost", "onSuccess: " + snapshotList.toString());

                            for (DocumentSnapshot documentSnapshot : snapshotList) {
                                Candidate_status_model candidate_status_model = documentSnapshot.toObject(Candidate_status_model.class);
                                status_models.add(candidate_status_model);

                            }
                            status_adapter = new Candidate_status_adapter(MainActivity.this, status_models);
                            status_spinner.setAdapter(status_adapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("getError_status", "onFailure: " + e.toString());
                    }
                });
    }

    private void Fill_Country() {

        country_models = new ArrayList<>();
        fire_store.collection(Constant.COLLECTION_COUNTRY)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            ArrayList<DocumentSnapshot> list = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot : list) {

                                Country_model country_model = snapshot.toObject(Country_model.class);
                                country_models.add(country_model);
                            }

                            countrySpinnerAdapter = new Country_spinner_adapter(MainActivity.this, country_models);
                            spinner_country.setAdapter(countrySpinnerAdapter);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("getCountry_error", "onFailure: " + e.toString());
                    }
                });
    }

    private void Fill_State() {
        state_models = new ArrayList<>();

        fire_store.collection(Constant.COLLECTION_STATE)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            ArrayList<DocumentSnapshot> list = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot : list) {

                                State_model state_model = snapshot.toObject(State_model.class);
                                state_models.add(state_model);
                            }

                            state_spinner_adapter = new State_spinner_adapter(MainActivity.this, state_models);
                            spinner_state.setAdapter(state_spinner_adapter);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("getstate_error", "onFailure: " + e.toString());
                    }
                });

    }

    private void Fill_City() {
        city_models = new ArrayList<>();

        fire_store.collection(Constant.COLLECTION_CITY)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            ArrayList<DocumentSnapshot> list = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot : list) {

                                City_model cityModel = snapshot.toObject(City_model.class);
                                city_models.add(cityModel);
                            }

                            city_spinner_adapter = new City_spinner_adapter(MainActivity.this, city_models);
                            spinner_city.setAdapter(city_spinner_adapter);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("getCity_error", "onFailure: " + e.toString());
                    }
                });

    }

    private void Fill_BloodGroup() {

        bloodgroup_models = new ArrayList<>();

        fire_store.collection(Constant.COLLECTION_BLOOD_GROUP)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            ArrayList<DocumentSnapshot> snapshots = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot : snapshots) {

                                Bloodgroup_model bloodgroup_model = snapshot.toObject(Bloodgroup_model.class);
                                bloodgroup_models.add(bloodgroup_model);
                            }
                            bloodGroup_spinner_adapter = new BloodGroup_spinner_adapter(MainActivity.this, bloodgroup_models);
                            spinner_blood_group.setAdapter(bloodGroup_spinner_adapter);

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("getGroup_eror", "onFailure: " + e.toString());
                    }
                });


    }


    private void SaveUSer() {
        phone = et_phone_no.getText().toString().trim();
        email_address = et_email_address.getText().toString().trim();
        dateofbirth = et_dob.getText().toString().trim();

        int id = group_mstatus.getCheckedRadioButtonId();
        radioButton = findViewById(id);
        mStatus = radioButton.getText().toString().trim();


        if (phone.isEmpty()) {
            et_phone_no.setError("require phone");
            et_phone_no.requestFocus();
            return;
        }


        if (email_address.isEmpty()) {
            et_email_address.setError("require email");
            et_email_address.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email_address).matches()) {
            et_email_address.setError("enter valid email address");
            et_email_address.requestFocus();
            return;
        }

        if (dateofbirth.isEmpty()) {
            et_dob.setError("require birthdate");
            et_dob.requestFocus();
            return;
        }

        btn_save.setVisibility(View.VISIBLE);
        Date current_date = Calendar.getInstance().getTime();

        Map<String, Object> saveUser = new HashMap<>();
        saveUser.put(Constant.USER_DOCUMENT_ID,document_id);
        saveUser.put(Constant.SELECT_POST, select_post_name);
        saveUser.put(Constant.USER_APPLIED_DATE, current_date);
        saveUser.put(Constant.CANDIDATE_STATUS, candidate_status);
        saveUser.put(Constant.USER_GENDER_SORT, get_gender);
        saveUser.put(Constant.USER_FIRST_NAME, firstname);
        saveUser.put(Constant.USER_MIDDLE_NAME, middlename);
        saveUser.put(Constant.USER_SURNAME, surname);
        saveUser.put(Constant.STREET_ADDRESS, s_address);
        saveUser.put(Constant.LANDMARK, landmark);
        saveUser.put(Constant.COUNTRY, country_name);
        saveUser.put(Constant.STATE, state_name);
        saveUser.put(Constant.CITY, city_name);
        saveUser.put(Constant.PINCODE, pin);
        saveUser.put(Constant.AREA, area);
        saveUser.put(Constant.USER_MOBILE, phone);
        saveUser.put(Constant.USER_EMAIL, email_address);
        // saveUser.put(Constant.USER__GENDER,gender);
        saveUser.put(Constant.USER_BLOOD_GROUP, bloodgroup);
        saveUser.put(Constant.USER_BIRTH_DATE, dateofbirth);
        saveUser.put(Constant.USER_AGE, age);
        saveUser.put(Constant.MARITAL_STATUS, mStatus);

                reference.set(saveUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
