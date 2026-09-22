package com.test.inputanktp;

import static java.util.Locale.filter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Report extends AppCompatActivity {

    RecyclerView recyclerView;
    ReportAdapter adapter;
    List<DataModal> dataList;
    RetrofitAPI retrofitAPI;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = findViewById(R.id.toolbar_report);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/ktp_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadDataReport();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataReport();
    }

    private void loadDataReport() {
        swipeRefreshLayout.setRefreshing(true);
        Call<List<DataModal>> call = retrofitAPI.getKtpData();
        call.enqueue(new Callback<List<DataModal>>() {
            @Override
            public void onResponse(Call<List<DataModal>> call, Response<List<DataModal>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    dataList = response.body();
                    adapter = new ReportAdapter(Report.this, dataList, (data, position) -> {
                        // Dialog Opsi saat Item Ditekan Lama (Long Click)
                        CharSequence[] options = {"Edit", "Delete"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(Report.this);
                        builder.setTitle("Pilih Aksi");
                        builder.setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                // OPSI EDIT: Buka TransactionActivity dan bawa data
                                Intent intent = new Intent(Report.this, Transaction.class);
                                intent.putExtra("is_edit", true);
                                intent.putExtra("id", data.getId());
                                intent.putExtra("nik", data.getNik());
                                intent.putExtra("nama", data.getNama());
                                intent.putExtra("alamat", data.getAlamat());
                                intent.putExtra("til", data.getTil());
                                intent.putExtra("jenis_kelamin", data.getJenis_kelamin());
                                intent.putExtra("agama", data.getAgama());
                                intent.putExtra("negara", data.getNegara());
                                intent.putExtra("pekerjaan", data.getPekerjaan());
                                intent.putExtra("satatus", data.getSatatus());
                                intent.putExtra("masa_berlaku", data.getMasa_berlaku());
                                startActivity(intent);
                            } else if (which == 1) {
                                // OPSI DELETE
                                deleteData(data.getId());
                            }
                        });
                        builder.show();
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<DataModal>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Report.this, "Gagal memuat data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteData(String id) {
        Call<DataModal> call = retrofitAPI.deleteKtpData(id);
        call.enqueue(new Callback<DataModal>() {
            @Override
            public void onResponse(Call<DataModal> call, Response<DataModal> response) {
                Toast.makeText(Report.this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                loadDataReport(); // Refresh list
            }

            @Override
            public void onFailure(Call<DataModal> call, Throwable t) {
                Toast.makeText(Report.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Cari nama atau NIK...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        return true;
    }

    private void filter(String text) {
        List<DataModal> filteredList = new ArrayList<>();
        for (DataModal item : dataList) {
            // Pencarian berdasarkan Nama atau NIK
            if (item.getNama().toLowerCase().contains(text.toLowerCase()) ||
                    item.getNik().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}