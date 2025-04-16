package com.innovatech.beruang;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Welcome extends AppCompatActivity {


    ViewPager2 welcomeview;
    MaterialButton next, back;
    RelativeLayout parent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //ONLYFORBETA
    SharedPreferences sharedbeta;

    private String[] topArray = {"Selamat Datang \ndi BerUang", "", "", "Gabung bersama kami sekarang!"};
    private String[] bottomArray = {"Sebuah aplikasi pengelola keuangan yang ramah dan mudah digunakan.",
            "Catat pengeluaran Anda lebih cepat, mudah, dan efisien dengan berbagai pintasan yang telah kami sediakan!",
            "Kami memiliki asisten keuangan pintar berbasis AI yang siap memberikan saran serta evaluasi keuangan bulanan secara cerdas.",
            "Dapatkan kontrol penuh atas keuangan Anda dan wujudkan tujuan finansial Anda"};
    private Integer[] imgsrc = {R.mipmap.icon, R.drawable.dashboard, R.drawable.three, R.mipmap.icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferences = getSharedPreferences("already", MODE_PRIVATE);

        //ONLYFORBETA
        sharedbeta = getSharedPreferences("BETATEST", MODE_PRIVATE);
        editor = sharedbeta.edit();
        if (sharedbeta.getBoolean("adddataset", true)){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getFilesDir(), "Data.json");

                    if (!file.exists()) {
                        try (InputStream in = getAssets().open("Data.json");
                             OutputStream out = new FileOutputStream(file)) {
                            byte[] buffer = new byte[1024 * 8];  // buffer yang lebih besar
                            int length;
                            while ((length = in.read(buffer)) > 0) {
                                out.write(buffer, 0, length);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        editor.putBoolean("adddataset", false);
        editor.apply();

        if (sharedPreferences.getBoolean("alreadywelcome", false)){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        editor = sharedPreferences.edit();
        welcomeview = findViewById(R.id.welcomeviewpager);
        next = findViewById(R.id.Next);
        back = findViewById(R.id.Previos);
        parent = findViewById(R.id.buttonparent);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float animx = displayMetrics.widthPixels*0.3720238095238095f;

        welcomeview.setUserInputEnabled(false);
        welcomeview.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            View view;

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                view = LayoutInflater.from(Welcome.this).inflate(R.layout.welcomeholder, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView atas = view.findViewById(R.id.toptext);
                TextView bawah = view.findViewById(R.id.bottomtext);
                atas.setText(topArray[position]);
                bawah.setText(bottomArray[position]);
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (welcomeview.getCurrentItem() < 2){
                    welcomeview.setCurrentItem(welcomeview.getCurrentItem()+1);
                    next.animate().translationX(animx).setDuration(300).start();
                    back.setVisibility(View.VISIBLE);
                } else if (welcomeview.getCurrentItem() == 2){
                    welcomeview.setCurrentItem(welcomeview.getCurrentItem()+1);
                    next.setIcon(getDrawable(R.drawable.baseline_done_24));
                } else {
                    new MaterialAlertDialogBuilder(Welcome.this).setTitle("PERHATIAN!!")
                                    .setMessage("Versi aplikasi BerUang yang terinstal adalah versi uji coba khusus.\n" +
                                            "\nTim InnvoaTech membuat versi ini untuk di uji cobakan oleh Penguji/Juri.\n" +
                                            "\nVersi uji coba ini telah menyediakan Dataset, dan juga beberapa fitur uji coba. Anda dapat melihatnya di Tab Pengaturan.\n" +
                                            "\n**Tanggal dalam Aplikasi telah diubah ke tanggal 28 October 2024.")
                            .setPositiveButton("Mengerti!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Welcome.this, pendaftaran.class));
                                }
                            }).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcomeview.setCurrentItem(welcomeview.getCurrentItem()-1);
                if (welcomeview.getCurrentItem() == 0){
                    next.animate().translationX(0f).setDuration(300).start();
                    back.setVisibility(View.GONE);
                } else if (welcomeview.getCurrentItem() < 3) {
                    next.setIcon(getDrawable(R.drawable.baseline_arrow_forward_24));
                }
            }
        });
    }
}