package com.innovatech.beruang;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    RelativeLayout editpemasukan, editpengeluaran;
    LinearLayout main;
    RelativeLayout listbox;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ListView listView;
    private ArrayList<String> items;
    FloatingActionButton fab;
    String tab = "main";
    private SubkategoriAdapter adapter;

    //onlyfortest
    Switch showevalbulan;
    Button deletedataset;
    SharedPreferences fortest;
    SharedPreferences.Editor editorfortest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferences = getApplicationContext().getSharedPreferences("Listdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editpemasukan = findViewById(R.id.editpemasukan);
        editpengeluaran = findViewById(R.id.editpengeluaran);
        main = findViewById(R.id.main);
        listbox = findViewById(R.id.listbox);
        listView = findViewById(R.id.listitem);
        fab = findViewById(R.id.tambah);

        //onlyfortest
        showevalbulan = findViewById(R.id.showevalbulan);
        deletedataset = findViewById(R.id.deletedataset);
        fortest = getSharedPreferences("BETATEST", MODE_PRIVATE);
        editorfortest = fortest.edit();

        showevalbulan.setChecked(fortest.getBoolean("showeval", false));

        showevalbulan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    editorfortest.putBoolean("showeval", true);
                    editorfortest.apply();
                    Toast.makeText(Settings.this, "Tanggal diubah ke 1 November 2024", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            System.exit(0);
                        }
                    },1000);
                } else {
                    editorfortest.putBoolean("showeval", false);
                    editorfortest.apply();
                    Toast.makeText(Settings.this, "Tanggal diubah ke 28 October 2024", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            System.exit(0);
                        }
                    },1000);
                }
            }
        });
        deletedataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editpemasukan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.setVisibility(View.GONE);
                listbox.setVisibility(View.VISIBLE);
                tab = "pemasukanlist";
                adapter = new SubkategoriAdapter(Settings.this, getArraylist("pemasukanlist"), "pemasukanlist");
                listView.setAdapter(adapter);
            }
        });
        editpengeluaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.setVisibility(View.GONE);
                listbox.setVisibility(View.VISIBLE);
                tab = "pengeluaranlist";
                adapter = new SubkategoriAdapter(Settings.this, getArraylist("pengeluaranlist"), "pengeluaranlist");
                listView.setAdapter(adapter);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputLayout textInputLayout = new TextInputLayout(Settings.this);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 10, 30, 10);

                textInputLayout.setLayoutParams(params);

                TextInputEditText judul = new TextInputEditText(Settings.this);
                judul.setHint("Judul kategori");
                textInputLayout.addView(judul);
                new MaterialAlertDialogBuilder(Settings.this)
                        .setTitle("Tambah kategori")
                        .setView(textInputLayout)
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<String> baru = getArraylist(tab);
                                if (judul.getText().toString().isEmpty()){
                                    judul.setError("Kosong");
                                } else {
                                    baru.add(judul.getText().toString());
                                    editor.putString(tab, new Gson().toJson(baru));
                                    editor.apply();
                                    adapter = new SubkategoriAdapter(Settings.this, getArraylist(tab), tab);
                                    listView.setAdapter(adapter);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (tab.equals("pengeluaranlist") || tab.equals("pemasukanlist")) {
            listbox.setVisibility(View.GONE);
            main.setVisibility(View.VISIBLE);
            tab = "main";
        } else {
            finish();
        }
    }

    public ArrayList<String> getArraylist(String list){
        String json = sharedPreferences.getString(list, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}