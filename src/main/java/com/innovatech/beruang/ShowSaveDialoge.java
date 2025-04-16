package com.innovatech.beruang;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowSaveDialoge extends AppCompatActivity {
    String tipedipilih = "pemasukan";
    JSONArray history;
    JSONObject data;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogeadd);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);


        sharedPreferences = getSharedPreferences("Listdata", MODE_PRIVATE);
        TextInputEditText judul = dialog.findViewById(R.id.judul);
        TextInputEditText angka = dialog.findViewById(R.id.rupiah);
        CheckBox pemasukan = dialog.findViewById(R.id.pemasukan);
        AutoCompleteTextView subkategori = dialog.findViewById(R.id.Kategori);
        CheckBox pengeluaran = dialog.findViewById(R.id.pengeluaran);
        Button button = dialog.findViewById(R.id.done);

        ArrayList<String> pemasukanlist = getArraylist("pemasukanlist");
        ArrayList<String> pengeluaranlist = getArraylist("pengeluaranlist");
        subkategori.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, pemasukanlist));

        pemasukan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipedipilih = "pemasukan";
                pengeluaran.setChecked(false);
            }
        });
        pengeluaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipedipilih = "pengeluaran";
                pemasukan.setChecked(false);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (angka.getText().toString().isEmpty()){
                    angka.setError("Masukkan jumlah");
                    angka.requestFocus();
                } else if (!pemasukan.isChecked() && !pengeluaran.isChecked()) {
                    Toast.makeText(ShowSaveDialoge.this, "Pilih Kategori terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    savejson(judul.getText().toString(), angka.getText().toString(), tipedipilih, subkategori.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void savejson(String judul, String jumlah, String tipe, String subkategori) {
        JSONObject baru = new JSONObject();
        try {
            SimpleDateFormat tanggal = new SimpleDateFormat("dd");
            SimpleDateFormat bulta = new SimpleDateFormat(" LLLL yyyy");
            baru.put("jumlah", jumlah);
            baru.put("judul", judul);
            baru.put("tanggal", tanggal.format(new Date()));
            baru.put("tipe", tipe);
            baru.put("fulldate", bulta.format(new Date()));
            baru.put("subkategori", subkategori);
            history.put(baru);
            data.put("history", history);
            FileOutputStream fos = openFileOutput("Data.json", MODE_PRIVATE);
            fos.write(data.toString().getBytes());
            fos.close();
        } catch (JSONException | IOException e) {
            System.out.println("3"+e);
        }

    }
    public ArrayList<String> getArraylist(String list){
        String json = sharedPreferences.getString(list, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}
