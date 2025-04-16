package com.innovatech.beruang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class pendaftaran extends AppCompatActivity {

    TextInputEditText nama, wilayah, target;
    AutoCompleteTextView jenjang, penghasilan;
    FloatingActionButton done;
    JSONObject jsonObject;
    String jsonData = "";
    String namadidata = "";
    SharedPreferences.Editor editor, editor1;
    ArrayList<String> pengeluaranlist, pemasukanlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pendaftaran);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Listdata", MODE_PRIVATE);
        editor1 = getSharedPreferences("already", MODE_PRIVATE).edit();
        editor = sharedPreferences.edit();
        nama = findViewById(R.id.nama);
        wilayah = findViewById(R.id.wilayah);
        jenjang = findViewById(R.id.jenjang);
        target = findViewById(R.id.target);
        penghasilan = findViewById(R.id.penghasilan);
        done = findViewById(R.id.done);

        try {
            FileInputStream fileInputStream = openFileInput("Data.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            jsonData = stringBuilder.toString();
            jsonObject = new JSONObject(jsonData);
            namadidata = jsonObject.getString("nama");
            System.out.println();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if (!namadidata.isEmpty()){
            startActivity(new Intent(pendaftaran.this, MainActivity.class));
            finish();
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nama.getText().toString().isEmpty()){
                    jsonObject = new JSONObject();
                    try {
                        jsonObject = new JSONObject(jsonData);
                        jsonObject.put("nama", nama.getText().toString());
                        jsonObject.put("jenjang", jenjang.getText().toString());
                        jsonObject.put("wilayah", wilayah.getText().toString());
                        jsonObject.put("target", target.getText().toString());
                        jsonObject.put("penghasilan", penghasilan.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String jsonstring = jsonObject.toString();
                    try {
                        FileOutputStream fos = openFileOutput("Data.json", MODE_PRIVATE);
                        fos.write(jsonstring.getBytes());
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    saveArraylist();
                    startActivity(new Intent(pendaftaran.this, MainActivity.class));
                    finish();
                } else {
                    nama.setError("Wajib Diisi");
                    nama.requestFocus();
                }
            }
        });
    }

    void saveArraylist(){
        pengeluaranlist = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.pemasukanlist)));
        pemasukanlist = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.pengeluaranlist)));
        editor.putString("pemasukanlist", new Gson().toJson(pengeluaranlist));
        editor.putString("pengeluaranlist", new Gson().toJson(pemasukanlist));
        editor1.putBoolean("alreadywelcome", true);
        editor.apply();
        editor1.apply();
    }
}