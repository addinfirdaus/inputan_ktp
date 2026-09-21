package com.test.inputanktp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Transaction extends AppCompatActivity {

    EditText et_nama,et_addres,et_nik,et_born,et_date_born,et_country,et_work,et_exp;
    Spinner sp_jk,sp_religion,sp_status;
    Button btpost;
    String item_jk;
    String agama;
    String status;
    Calendar calendar;
    RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/ktp_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);

        et_nama = findViewById(R.id.et_name);
        et_addres = findViewById(R.id.et_addres);
        et_nik = findViewById(R.id.et_nik);
        et_born = findViewById(R.id.et_born);
        et_date_born = findViewById(R.id.et_date_born);
        et_country = findViewById(R.id.et_country);
        et_work = findViewById(R.id.et_work);
        et_exp = findViewById(R.id.et_exp);

        calendar = Calendar.getInstance();
        et_date_born.setFocusable(false);
        et_date_born.setClickable(true);

        et_exp.setFocusable(false);
        et_exp.setClickable(true);

        et_date_born.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog("born");
            }
        });

        et_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog("exp");
            }
        });

        sp_jk = findViewById(R.id.sp_jk);
        // Spinner click listener
        sp_jk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item_jk = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadMasterData("get_jenis_kelamin.php", sp_jk);

        sp_religion = findViewById(R.id.sp_religion);
        sp_religion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                agama = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadMasterData("get_religion.php", sp_religion);

        sp_status = findViewById(R.id.sp_status);
        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadMasterData("get_status.php", sp_status);


        btpost = findViewById(R.id.btpost);

        btpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Transaction.this, ""+et_nik.getText().toString(), Toast.LENGTH_SHORT).show();
                postData(et_nik.getText().toString(),et_nama.getText().toString(),et_addres.getText().toString(),et_born.getText().toString()+", "+et_date_born.getText().toString(),item_jk,agama,et_country.getText().toString(),et_work.getText().toString(),status,et_exp.getText().toString());

            }
        });


    }

    private void loadMasterData(String endpointUrl, Spinner targetSpinner) {
        Call<List<MasterModel>> call = retrofitAPI.getMasterData(endpointUrl);

        call.enqueue(new Callback<List<MasterModel>>() {
            @Override
            public void onResponse(Call<List<MasterModel>> call, Response<List<MasterModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MasterModel> dataList = response.body();
                    List<String> namesList = new ArrayList<>();

                    for (MasterModel model : dataList) {
                        namesList.add(model.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            Transaction.this,
                            android.R.layout.simple_spinner_item,
                            namesList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    targetSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(Transaction.this, "Gagal memuat data dari " + endpointUrl, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MasterModel>> call, Throwable t) {
                Toast.makeText(Transaction.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(String et) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Transaction.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                    String dateFormat = "dd-MM-yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

                    if (et=="born") {
                        et_date_born.setText(sdf.format(calendar.getTime()));
                    }else{
                        et_exp.setText(sdf.format(calendar.getTime()));
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }


    private void postData(String nik, String nama,String alamat, String til, String jenis_kelamin, String agama, String negara, String pekerjaan, String satatus, String masa_berlaku) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/ktp_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        DataModal modal = new DataModal(nik, nama, alamat, til, jenis_kelamin, agama, negara,pekerjaan,satatus,masa_berlaku);

        Call<DataModal> call = retrofitAPI.createPost(modal);

        call.enqueue(new Callback<DataModal>() {
            @Override
            public void onResponse(Call<DataModal> call, Response<DataModal> response) {
                Toast.makeText(Transaction.this, "Data added to API", Toast.LENGTH_SHORT).show();

                // below line is for hiding our progress bar.
//                loadingPB.setVisibility(View.GONE);

                // on below line we are setting empty text
                // to our both edit text.
//                jobEdt.setText("");
//                nameEdt.setText("");

                // we are getting response from our body
                // and passing it to our modal class.
                DataModal responseFromAPI = response.body();

                // on below line we are getting our data from modal class and adding it to our string.
//                String responseString = "Response Code : " + response.code() + "\nName : " + responseFromAPI.getName() + "\n" + "Job : " + responseFromAPI.getJob();

                // below line we are setting our
                // string to our text view.
//                responseTV.setText(responseString);
            }

            @Override
            public void onFailure(Call<DataModal> call, Throwable t) {
                // setting text to our text view when
                // we get error response from API.
                Toast.makeText(Transaction.this, "Data error : "+t.getMessage(), Toast.LENGTH_SHORT).show();

//                responseTV.setText("Error found is : " + t.getMessage());
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Menutup activity saat ini dan kembali ke activity sebelumnya
        return true;
    }


}
