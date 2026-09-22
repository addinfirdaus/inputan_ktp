package com.test.inputanktp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Transaction extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    EditText et_nama,et_addres,et_nik,et_born,et_date_born,et_country,et_work,et_exp;
    Spinner sp_jk,sp_religion,sp_status;
    Button btpost;
    String item_jk;
    String agama;
    String status;
    Calendar calendar;
    RetrofitAPI retrofitAPI;
    ImageView iv_foto;
    private File imageFile;
    private boolean isEditMode = false;
    private String editId = "";
    private String editJenisKelamin = "";
    private String editAgama = "";
    private String editStatus = "";
    TextView provid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);

        Intent intent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        iv_foto = findViewById(R.id.iv_foto);

        btpost = findViewById(R.id.btpost);

        provid = findViewById(R.id.prov_id);

        calendar = Calendar.getInstance();
        et_date_born.setFocusable(false);
        et_date_born.setClickable(true);

        et_exp.setFocusable(false);
        et_exp.setClickable(true);

        if (intent != null && intent.getBooleanExtra("is_edit", false)) {
            isEditMode = true;
            editId = intent.getStringExtra("id");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit KTP");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            iv_foto.setEnabled(false);
            iv_foto.setImageResource(android.R.color.white);
            provid.setText("EDIT DATA");
            btpost.setText("Update");
            et_nik.setText(intent.getStringExtra("nik"));
            et_nama.setText(intent.getStringExtra("nama"));
            et_addres.setText(intent.getStringExtra("alamat"));
            String til = intent.getStringExtra("til");
            if (til != null && !til.isEmpty()) {
                // Cek apakah string mengandung koma
                if (til.contains(",")) {
                    String[] parts = til.split(",");
                    if (parts.length >= 2) {
                        String tempatLahir = parts[0].trim();  // Hasilnya: "surabaya" (tanpa koma)
                        String tanggalLahir = parts[1].trim(); // Hasilnya: "9-10-1990" (spasi di depan dibuang)

                        // Masukkan ke masing-masing EditText
                        et_born.setText(tempatLahir);
                        et_date_born.setText(tanggalLahir);
                    } else {
                        et_born.setText(til.trim());
                    }
                } else {
                    et_born.setText(til.trim());
                }
            }

            editJenisKelamin = intent.getStringExtra("jenis_kelamin");
            editAgama = intent.getStringExtra("agama");
            editStatus = intent.getStringExtra("satatus");

            et_country.setText(intent.getStringExtra("negara"));
            et_work.setText(intent.getStringExtra("pekerjaan"));
            et_exp.setText(intent.getStringExtra("masa_berlaku"));

        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Transaction");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }}

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

        loadMasterData("get_jenis_kelamin.php", sp_jk, editJenisKelamin);

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

        loadMasterData("get_religion.php", sp_religion, editAgama);

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

        loadMasterData("get_status.php", sp_status, editStatus);

        btpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    // JIKA MODE EDIT: Panggil method update
                    updateData(editId, et_nik.getText().toString(), et_nama.getText().toString(), et_addres.getText().toString(), et_born.getText().toString()+", "+et_date_born.getText().toString(), item_jk, agama, et_country.getText().toString(), et_work.getText().toString(), status, et_exp.getText().toString());
                } else {
                    // JIKA MODE TAMBAH BARU: Panggil method postData dengan gambar
                    postData(et_nik.getText().toString(),et_nama.getText().toString(),et_addres.getText().toString(),et_born.getText().toString()+", "+et_date_born.getText().toString(),item_jk,agama,et_country.getText().toString(),et_work.getText().toString(),status,et_exp.getText().toString(),imageFile);
                }

            }
        });


        iv_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Transaction.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            Transaction.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE
                    );
                } else {
                    bukaKamera();
                }
            }
        });

    }

    private void bukaKamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            Toast.makeText(this, "Tidak dapat membuka kamera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }}

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin kamera diberikan", Toast.LENGTH_SHORT).show();
                    bukaKamera();
                } else {
                    Toast.makeText(this, "Izin kamera ditolak. Aplikasi butuh izin ini untuk mengambil foto.", Toast.LENGTH_LONG).show();
                }
    }}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv_foto.setImageBitmap(imageBitmap); // Tampilkan ke ImageView

            try {
                imageFile = bitmapToFile(imageBitmap, "foto_ktp_" + System.currentTimeMillis() + ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File bitmapToFile(Bitmap bitmap, String fileName) throws IOException {
        File file = new File(getCacheDir(), fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        fos.flush();
        fos.close();
        return file;
    }

    private void loadMasterData(String endpointUrl, Spinner targetSpinner,String targetValue) {
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

                    if (isEditMode) {
                        setSpinnerSelection(targetSpinner, targetValue);
                    }
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


    private void postData(String nik, String nama, String alamat, String til, String jenis_kelamin, String agama, String negara, String pekerjaan, String satatus, String masa_berlaku, File file) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/ktp_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        RequestBody rNik = RequestBody.create(MediaType.parse("text/plain"), nik);
        RequestBody rNama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody rAlamat = RequestBody.create(MediaType.parse("text/plain"), alamat);
        RequestBody rTil = RequestBody.create(MediaType.parse("text/plain"), til);
        RequestBody rJk = RequestBody.create(MediaType.parse("text/plain"), jenis_kelamin);
        RequestBody rAgama = RequestBody.create(MediaType.parse("text/plain"), agama);
        RequestBody rNegara = RequestBody.create(MediaType.parse("text/plain"), negara);
        RequestBody rPekerjaan = RequestBody.create(MediaType.parse("text/plain"), pekerjaan);
        RequestBody rStatus = RequestBody.create(MediaType.parse("text/plain"), satatus);
        RequestBody rMasa = RequestBody.create(MediaType.parse("text/plain"), masa_berlaku);

        MultipartBody.Part imagePart = null;
        if (file != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            imagePart = MultipartBody.Part.createFormData("gambar", file.getName(), requestFile);
        } else {
            RequestBody emptyFile = RequestBody.create(MediaType.parse("image/*"), "");
            imagePart = MultipartBody.Part.createFormData("gambar", "", emptyFile);
        }

        Call<DataModal> call = retrofitAPI.createPostWithImage(rNik, rNama, rAlamat, rTil, rJk, rAgama, rNegara, rPekerjaan, rStatus, rMasa, imagePart);

        call.enqueue(new Callback<DataModal>() {
            @Override
            public void onResponse(Call<DataModal> call, Response<DataModal> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Transaction.this, "Data & Gambar Berhasil Masuk Database!", Toast.LENGTH_SHORT).show();

                    // Opsional: Reset field setelah sukses
                    et_nik.setText("");
                    et_nama.setText("");
                    et_addres.setText("");
                    et_born.setText("");
                    et_country.setText("");
                    et_work.setText("");
                    iv_foto.setImageResource(0); // Reset gambar di ImageView
                    imageFile = null;
                } else {
                    Toast.makeText(Transaction.this, "Gagal dari Server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataModal> call, Throwable t) {
                Toast.makeText(Transaction.this, "Data error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData(String id, String nik, String nama, String alamat, String til, String jenis_kelamin, String agama, String negara, String pekerjaan, String satatus, String masa_berlaku) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/ktp_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        DataModal modal = new DataModal(id, nik, nama, alamat, til, jenis_kelamin, agama, negara, pekerjaan, satatus, masa_berlaku, "");

        Call<DataModal> call = retrofitAPI.updateKtpData(id, modal);

        call.enqueue(new Callback<DataModal>() {
            @Override
            public void onResponse(Call<DataModal> call, Response<DataModal> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Transaction.this, "Data Berhasil Diperbarui!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Transaction.this, "Gagal Update dari Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataModal> call, Throwable t) {
                Toast.makeText(Transaction.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setSpinnerSelection(Spinner spinner, String valueToSelect) {
        if (valueToSelect == null || valueToSelect.isEmpty()) return;

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equalsIgnoreCase(valueToSelect)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }


}
