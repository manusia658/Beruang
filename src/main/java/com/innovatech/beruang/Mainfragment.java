package com.innovatech.beruang;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nirhart.parallaxscroll.views.ParallaxListView;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Mainfragment extends Fragment {

    TextView saldo, batas, welcome;
    SharedPreferences sharedPreferences;
    FloatingActionButton fab, toleft;
    String jsonData = "";
    JSONObject data;
    LinearLayout batasclick;
    ParallaxListView listViewhistory;
    NumberFormat numberFormat;
    ArrayList<JSONObject> historyyyyy = new ArrayList<JSONObject>();
    ImageView setting;
    Listviewadapter listviewadapter;
    JSONArray history, sublist;
    int totalsaldo = 0;
    TEST test;
    String tipedipilih = "pemasukan";
    SharedPreferences limitshared;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewnew = inflater.inflate(R.layout.fragment_mainfragment, container, false);

        data = new JSONObject();
        listViewhistory = (ParallaxListView) viewnew.findViewById(R.id.listhistory);
        if (getActivity().getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false)){
            test = new TEST(true);
        } else {
            test = new TEST(false);
        }

        history = new JSONArray();
        sublist = new JSONArray();
        numberFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listheader = inflater1.inflate(R.layout.listheaderfile, null);
        welcome = listheader.findViewById(R.id.welcome);

        //test
        System.out.println(new SimpleDateFormat("dd").format(test.getcurrentdate()));

        try {
            FileInputStream fis = getActivity().openFileInput("Data.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            jsonData = stringBuilder.toString();
            data = new JSONObject(jsonData);
            history = data.getJSONArray("history");
            for (int i = 0; i < history.length(); i++) {
                JSONObject datacek = history.getJSONObject(i);
                historyyyyy.add(datacek);
                String tipe = datacek.getString("tipe");
                if (tipe.equals("pemasukan")){
                    totalsaldo += Integer.parseInt(datacek.getString("jumlah"));
                }else {
                    totalsaldo -= Integer.parseInt(datacek.getString("jumlah"));
                }
            }
            fis.close();
            welcome.setText("Selamat Datang, "+ data.getString("nama"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        fab = viewnew.findViewById(R.id.tambah);
        toleft = viewnew.findViewById(R.id.toleft);
        saldo = listheader.findViewById(R.id.saldo);
        batas = listheader.findViewById(R.id.batasan);
        batasclick = listheader.findViewById(R.id.limitclick);
        setting = listheader.findViewById(R.id.setting);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(" LLLL yyyy");
            Calendar calendar = test.getcurrenttime();
            calendar.add(Calendar.MONTH, -1);
            for (int j = 0; j < history.length(); j++) {
                if (history.getJSONObject(j).getString("fulldate").equals(dateFormat.format(calendar.getTime())) &&
                        new SimpleDateFormat("dd").format(test.getcurrentdate()).equals("01")){
                    toleft.setVisibility(View.VISIBLE);
                    break;
                }
            }
        } catch (JSONException e) {}

        TranslateAnimation translateAnimation = new TranslateAnimation(1f, -15f, 1f, 1f);
        translateAnimation.setDuration(400);
        translateAnimation.setRepeatMode(Animation.REVERSE);
        translateAnimation.setRepeatCount(Animation.INFINITE);
        toleft.startAnimation(translateAnimation);

        toleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ListFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.utama, fragment);
                fragmentTransaction.commit();
                BottomNavigationView navigationView = getActivity().findViewById(R.id.nav);
                navigationView.setSelectedItemId(R.id.news);
            }
        });

        listViewhistory.addParallaxedHeaderView(listheader);
        listviewadapter = new Listviewadapter(getActivity().getApplicationContext(), data, false,
                getActivity().getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false));
        listViewhistory.setAdapter(listviewadapter);
        saldo.setText("Rp"+numberFormat.format(totalsaldo));
        sharedPreferences = getActivity().getSharedPreferences("Listdata", MODE_PRIVATE);

        limitshared = getActivity().getSharedPreferences("limit", MODE_PRIVATE);
        editor = limitshared.edit();
        System.out.println(limitshared.getString("day", "") + limitshared.getInt("limit", 0));

        if (limitshared.getString("day", "").equals(new SimpleDateFormat("dd").format(test.getcurrentdate()))) {
            batas.setText("Rp" + numberFormat.format(limitshared.getInt("limittoday", 0)));
        } else {
            editor.putString("day", new SimpleDateFormat("dd").format(test.getcurrentdate()));
            editor.putInt("limittoday", limitshared.getInt("limittoday", 500000));
            editor.apply();
            batas.setText("Rp" + numberFormat.format(500000));
        }

        batasclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputLayout textInputLayout = new TextInputLayout(getContext());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 10, 30, 10);

                textInputLayout.setLayoutParams(params);

                TextInputEditText angka = new TextInputEditText(getContext());
                angka.setHint("Maksimal pengeluaran?");
                angka.setInputType(InputType.TYPE_CLASS_NUMBER);
                textInputLayout.addView(angka);
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Atur Maksimal Pengeluaran")
                        .setView(textInputLayout)
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editor.putInt("limittoday", Integer.parseInt(angka.getText().toString()));
                                editor.apply();
                                batas.setText("Rp" + numberFormat.format(Integer.parseInt(angka.getText().toString())));
                            }
                        })
                        .show();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setting.animate().rotation(40f).setDuration(300).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), Settings.class));
                    }
                }, 200);
            }
        });

        listViewhistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view1, int i, long l) {
                ImageView update = view1.findViewById(R.id.updatebutton);
                ImageView delete = view1.findViewById(R.id.deletebutton);
                ImageView close = view1.findViewById(R.id.closebutton);
                view1.animate().scaleX(0.95f).scaleY(0.95f).setDuration(300).start();
                update.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialogeadd);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(true);
                        tipedipilih = "pemasukan";

                        TextInputEditText judul = dialog.findViewById(R.id.judul);
                        TextInputEditText jumlah = dialog.findViewById(R.id.rupiah);
                        CheckBox pemasukan = dialog.findViewById(R.id.pemasukan);
                        CheckBox pengeluaran = dialog.findViewById(R.id.pengeluaran);
                        AutoCompleteTextView subkategori = dialog.findViewById(R.id.Kategori);
                        Button button = dialog.findViewById(R.id.done);
                        Button cancel = dialog.findViewById(R.id.cancell);
                        ArrayList<String> pemasukanlist = getArraylist("pemasukanlist");
                        ArrayList<String> pengeluaranlist = getArraylist("pengeluaranlist");
                        subkategori.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, pemasukanlist));

                        try {
                            JSONObject baru = history.getJSONObject(i/2-1);
                            judul.setText(baru.getString("judul"));
                            jumlah.setText(baru.getString("jumlah"));
                            if (baru.getString("tipe").equals("pemasukan")){
                                pemasukan.setChecked(true);
                                tipedipilih = "pemasukan";
                            } else {
                                tipedipilih = "pengeluaran";
                                pemasukan.setChecked(false);
                                pengeluaran.setChecked(true);
                            }
                            subkategori.setText(baru.getString("subkategori"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pemasukan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tipedipilih = "pemasukan";
                                pengeluaran.setChecked(false);
                                pemasukan.setClickable(false);
                                pengeluaran.setClickable(true);
                                subkategori.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, pemasukanlist));
                            }
                        });
                        pengeluaran.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tipedipilih = "pengeluaran";
                                pemasukan.setChecked(false);
                                pemasukan.setClickable(true);
                                pengeluaran.setClickable(false);
                                subkategori.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, pemasukanlist));
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (jumlah.getText().toString().isEmpty()){
                                    jumlah.setError("Masukkan jumlah");
                                    jumlah.requestFocus();
                                } else {
                                    try {
                                        Integer minus = Integer.parseInt(history.getJSONObject(i/2-1).getString("jumlah"))-Integer.parseInt(jumlah.getText().toString());
                                        JSONObject baru = history.getJSONObject(i / 2-1);
                                        baru.put("jumlah", jumlah.getText());
                                        baru.put("judul", judul.getText());
                                        baru.put("tipe", tipedipilih);
                                        history.put((i / 2) - 1, baru);
                                        data.put("history", history);
                                        FileOutputStream fos = getActivity().openFileOutput("Data.json", MODE_PRIVATE);
                                        fos.write(data.toString().getBytes());
                                        fos.close();
                                        totalsaldo = 0;
                                        listviewadapter = new Listviewadapter(getActivity().getApplicationContext(), data,
                                                false, getActivity().getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false));
                                        listViewhistory.setAdapter(listviewadapter);
                                        listviewadapter.notifyDataSetChanged();
                                        editor.putInt("limittoday", limitshared.getInt("limittoday", 0)+minus);
                                        editor.apply();
                                        batas.setText("Rp" + numberFormat.format(limitshared.getInt("limittoday", 0)));
                                        for (int i = 0; i < history.length(); i++) {
                                            JSONObject datacek = history.getJSONObject(i);
                                            String tipe = datacek.getString("tipe");
                                            if (tipe.equals("pemasukan")) {
                                                totalsaldo += Integer.parseInt(datacek.getString("jumlah"));
                                            } else {
                                                totalsaldo -= Integer.parseInt(datacek.getString("jumlah"));
                                            }
                                        }
                                        saldo.setText("Rp" + numberFormat.format(totalsaldo));
                                        dialog.dismiss();
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        dialog.show();
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view1.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                        update.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                        close.setVisibility(View.GONE);
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            editor.putInt("limittoday", limitshared.getInt("limittoday", 0)+Integer.parseInt(history.getJSONObject(i/2-1).getString("jumlah")));
                            editor.apply();
                            batas.setText("Rp" + numberFormat.format(limitshared.getInt("limittoday", 0)));
                            ArrayList<JSONObject> list = new ArrayList<>();
                            for (int j = 0; j < history.length(); j++) {
                                list.add(history.getJSONObject(j));
                            }
                            totalsaldo -= Integer.parseInt(history.getJSONObject(i/2-1).getString("jumlah"));
                            list.remove(i/2-1);
                            history = new JSONArray(list);
                            data.put("history", history);
                            listViewhistory.setAdapter(new Listviewadapter(getActivity().getApplicationContext(), data, false,
                                    getActivity().getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false)));
                            saldo.setText("Rp"+numberFormat.format(totalsaldo));
                            FileOutputStream fos = getActivity().openFileOutput("Data.json", MODE_PRIVATE);
                            fos.write(data.toString().getBytes());
                            fos.close();
                        } catch (JSONException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialogeadd);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);
                tipedipilih = "pemasukan";

                TextInputEditText judul = dialog.findViewById(R.id.judul);
                TextInputEditText angka = dialog.findViewById(R.id.rupiah);
                CheckBox pemasukan = dialog.findViewById(R.id.pemasukan);
                AutoCompleteTextView subkategori = dialog.findViewById(R.id.Kategori);
                CheckBox pengeluaran = dialog.findViewById(R.id.pengeluaran);
                Button button = dialog.findViewById(R.id.done);
                Button cancel = dialog.findViewById(R.id.cancell);

                ArrayList<String> pemasukanlist = getArraylist("pemasukanlist");
                ArrayList<String> pengeluaranlist = getArraylist("pengeluaranlist");
                subkategori.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, pemasukanlist));

                pemasukan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tipedipilih = "pemasukan";
                        pengeluaran.setChecked(false);
                        pemasukan.setClickable(false);
                        pengeluaran.setClickable(true);
                        subkategori.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, pemasukanlist));
                    }
                });
                pengeluaran.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tipedipilih = "pengeluaran";
                        pemasukan.setChecked(false);
                        pemasukan.setClickable(true);
                        pengeluaran.setClickable(false);
                        subkategori.setAdapter(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, pengeluaranlist));
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (angka.getText().toString().isEmpty()){
                            angka.setError("Masukkan jumlah");
                            angka.requestFocus();
                        } else {
                            savejson(judul.getText().toString(), angka.getText().toString(), tipedipilih, subkategori.getText().toString());
                            listviewadapter = new Listviewadapter(getActivity().getApplicationContext(), data, false,
                                    getActivity().getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false));
                            listViewhistory.setAdapter(listviewadapter);
                            if (tipedipilih.equals("pemasukan")){
                                totalsaldo += Integer.parseInt(angka.getText().toString());
                            }else {
                                totalsaldo -= Integer.parseInt(angka.getText().toString());
                                int limit = limitshared.getInt("limittoday", 0);
                                limit -= Integer.parseInt(angka.getText().toString());
                                editor.putInt("limittoday", limit);
                                editor.apply();
                                batas.setText("Rp"+numberFormat.format(limit));
                            }

                            saldo.setText("Rp"+numberFormat.format(totalsaldo));
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        return viewnew;
    }

    private void savejson(String judul, String jumlah, String tipe, String subkategori) {
        JSONObject baru = new JSONObject();
        JSONArray temp;
        JSONArray kepala;
        JSONObject object;
        int jumlahsub = 0;

        try {
            if (!data.has("RangeSubkategori")) {
                kepala = new JSONArray();
                JSONObject initialObj = new JSONObject();
                initialObj.put("pemasukan", new JSONObject());
                initialObj.put("pengeluaran", new JSONObject());
                kepala.put(initialObj);
                data.put("RangeSubkategori", kepala);
            } else {
                kepala = data.getJSONArray("RangeSubkategori");
            }

            object = kepala.getJSONObject(0);
            JSONObject tipeObj = object.getJSONObject(tipe);

            if (!tipeObj.has(subkategori)) {
                tipeObj.put(subkategori, 0);
            }

            jumlahsub = tipeObj.getInt(subkategori);
            jumlahsub++;

            SimpleDateFormat tanggal = new SimpleDateFormat("dd");
            SimpleDateFormat bulta = new SimpleDateFormat(" LLLL yyyy");
            baru.put("jumlah", jumlah);
            baru.put("judul", judul);
            baru.put("tanggal", tanggal.format(test.getcurrentdate()));
            baru.put("tipe", tipe);
            baru.put("fulldate", bulta.format(test.getcurrentdate()));
            baru.put("subkategori", subkategori);

            if (!data.has("history")) {
                temp = new JSONArray();
            } else {
                temp = data.getJSONArray("history");
            }

            JSONArray newHistory = new JSONArray();
            newHistory.put(baru);
            for (int i = 0; i < temp.length(); i++) {
                newHistory.put(temp.getJSONObject(i));
            }

            data.put("history", newHistory);
            tipeObj.put(subkategori, jumlahsub);

            FileOutputStream fos = getActivity().openFileOutput("Data.json", MODE_PRIVATE);
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