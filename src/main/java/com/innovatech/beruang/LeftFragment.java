package com.innovatech.beruang;

import static android.content.Context.MODE_PRIVATE;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nirhart.parallaxscroll.views.ParallaxListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LeftFragment extends Fragment {

    ParallaxListView listView;
    TextView saranharini, expandtext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView imgrotate;
    ConstraintLayout constraintLayout;
    private static final String API_KEY = "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";
    private static final String BASE_URL = "https://api.openai.com/v1/chat/completions";
    private final static int maksline = 3;
    private final boolean initialcollapes = true;
    private boolean isCollapsed = initialcollapes;
    JSONObject data;
    JSONArray history;
    CardView gotoeval;
    RelativeLayout loading;
    boolean enablerequest = true;
    SimpleDateFormat dateFormat;
    View view, listheader;
    ArrayList<JSONObject> datas = new ArrayList<JSONObject>();
    String responseData = "Loading................";
    String yesterday, massageprompt, rolemassage;
    private OkHttpClient client;
    TEST test;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_left, container, false);

//        client = new OkHttpClient();
        client = ONLYFORTEST.getUnsafeClient();
        if (getActivity().getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false)){
            test = new TEST(true);
        } else {
            test = new TEST(false);
        }

        sharedPreferences = getActivity().getSharedPreferences("requestcheck", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView = view.findViewById(R.id.beritaa);
        LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listheader = inflater1.inflate(R.layout.leftheaderfile, null);

        saranharini = listheader.findViewById(R.id.saranharinitextview);
        constraintLayout = listheader.findViewById(R.id.rootlayout);
        gotoeval = listheader.findViewById(R.id.gotoeval);
        imgrotate = listheader.findViewById(R.id.imgrotate);
        expandtext = listheader.findViewById(R.id.expandtext);

        listView.addParallaxedHeaderView(listheader);
        loading = view.findViewById(R.id.loading);

        Calendar calendar = test.getcurrenttime();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterdayformat = calendar.getTime();
        yesterday = new SimpleDateFormat("dd").format(yesterdayformat);
        dateFormat = new SimpleDateFormat(" LLLL yyyy");

        loading.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(null);

        gotoeval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EvaluasiPerbulan.class));
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCollapsed) {
                    saranharini.setMaxLines(Integer.MAX_VALUE);
                    constraintLayout.requestLayout();
                    imgrotate.animate().rotation(-180).setDuration(500).start();
                    expandtext.setText("Sembunyikan");
                } else {
                    imgrotate.animate().rotation(0).setDuration(500).start();
                    expandtext.setText("Tampilkan");
                    saranharini.setMaxLines(maksline);
                }
                isCollapsed = !isCollapsed;
            }
        });

        history = new JSONArray();
        try {
            FileInputStream fileInputStream = getActivity().openFileInput("Data.json");
            InputStreamReader isp = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isp);
            StringBuilder stringBuilder = new StringBuilder();
            String jsonData;
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            jsonData = stringBuilder.toString();
            data = new JSONObject(jsonData);
            history = data.getJSONArray("history");
        } catch (IOException | JSONException e) {
        }

        try {
            Calendar temp1 = test.getcurrenttime();
            temp1.add(Calendar.MONTH, -1);
            for (int j = 0; j < history.length(); j++) {
                if (history.getJSONObject(j).getString("fulldate").equals(dateFormat.format(temp1.getTime()))&&new SimpleDateFormat("dd").format(test.getcurrentdate()).equals("01")){
                    gotoeval.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.GONE);
                    enablerequest = false;
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.05f, 1f, 1.05f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(600);
                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                    scaleAnimation.setRepeatCount(Animation.INFINITE);
                    gotoeval.startAnimation(scaleAnimation);
                    break;
                }
            }
        } catch (JSONException e) {}

        System.out.println(sharedPreferences.getString("alreadysaran", "") + sharedPreferences.getString("responesaran", ""));
        System.out.println(sharedPreferences.getString("alreadyberita", "") + sharedPreferences.getString("responeberita", ""));

        if (enablerequest && !sharedPreferences.getString("alreadysaran", "").equals(new SimpleDateFormat("dd").format(test.getcurrentdate()))){
            try {
                boolean adadatakemarin = false;
                for (int j = 0; j < history.length(); j++) {
                    if (history.getJSONObject(j).getString("fulldate").equals(dateFormat.format(test.getcurrentdate()))&&history.getJSONObject(j).getString("tanggal").equals(yesterday)){
                        adadatakemarin = true;
                        break;
                    }
                }
                System.out.println(adadatakemarin);
                if (adadatakemarin) {
                    rolemassage = "Buatlah saran harian berdasarkan data pengeluaran dan pemasukan pengguna pada hari kemarin, dengan mempertimbangkan juga data pribadi pengguna sebagai referensi. Saran harus relevan dan tidak melenceng dari data yang diberikan, disajikan dalam bentuk poin-poin yang ringkas dan mudah dibaca. Fokuskan pada tips atau rekomendasi praktis yang dapat membantu pengguna mengelola pengeluaran lebih baik, meningkatkan pemasukan, atau mencapai keseimbangan finansial yang lebih baik. Pastikan saran ringkas, tidak melebihi 630 karakter.";
                    massageprompt = "Berikan saran keuangan hari ini yang dapat membantu pengguna mengelola keuangan hari ini, gunaka data pribadi pungguna sebagai data refrensi tambahan dan gunakan data pemasukan/pengeluaran pada hari kemarin untuk memberi saran yang relevan, ingat tidak lebih dari 630 karakter, ini adalah data pengguna: \n" + " pengguna pada hari kemarin: \n";
                    StringBuilder prompt = new StringBuilder("Berikan saran keuangan hari ini yang dapat membantu pengguna mengelola keuangan hari ini, gunaka data pribadi pungguna sebagai data refrensi tambahan dan gunakan data pemasukan/pengeluaran pada hari kemarin untuk memberi saran yang relevan, ini adalah data pengguna yang dapat membantu: \n");
                    prompt.append("Nama:" + data.getString("nama"));
                    try {prompt.append(" ,Wilayah tempat tinggal:" + data.getString("wilayah"));} catch (JSONException e) {}
                    try {prompt.append(" ,Penghasilan:" + data.getString("penghasilan"));} catch (JSONException e) {}
                    try {prompt.append(" ,Target:" + data.getString("target"));} catch (JSONException e) {}
                    prompt.append("\ndan ini adalah catatan pengguna pada hari hari kemarin: \n");
                    System.out.println(adadatakemarin);
                    client.newCall(makeRequest(history, prompt.toString(), rolemassage)).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            System.out.println("ReqestSaran: " + e.getMessage());// Handle failure
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    String temp = response.body().string();
                                    JSONObject responejson = new JSONObject(temp);
                                    responseData = responejson.getJSONArray("choices").getJSONObject(0)
                                            .getJSONObject("message").getString("content");
                                    editor.putString("alreadysaran", new SimpleDateFormat("dd").format(test.getcurrentdate()));
                                    editor.putString("responesaran", responseData);
                                    editor.apply();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saranharini.setText(responseData);
                                            listView.setVisibility(View.VISIBLE);
                                            if (listView.getAdapter() == null){
                                                listView.setAdapter(null);
                                            }
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (enablerequest && !sharedPreferences.getString("alreadyberita", "").equals(new SimpleDateFormat("dd").format(test.getcurrentdate()))){
                                                        try {
                                                            rolemassage = "Analisis data pengeluaran dan pemasukan pengguna dari hari kemarin, dan hasilkan hanya keyword berita yang relevan tanpa penjelasan tambahan. Misalnya, jika pengeluaran terbesar adalah makanan, hasilkan keyword seperti 'diskon makanan' atau 'promo restoran'. Jika terkait transportasi, berikan keyword seperti 'promo transportasi'. Berikan hasil berupa satu atau beberapa kata kunci yang langsung dapat digunakan dalam News API.";
                                                            massageprompt = "Berikan saya keyword berita yang relevan dengan data histori pengeluaran dan pemasukan uang saya pada hari kemarin sebagai berikut: \n";

                                                            client.newCall(makeRequest(history, massageprompt, rolemassage)).enqueue(new Callback() {
                                                                @Override
                                                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                                    System.out.println("Request: " + e.getMessage());
                                                                }

                                                                @Override
                                                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                                    if (response.isSuccessful()){
                                                                        //start
                                                                        try {
                                                                            JSONObject responejson = new JSONObject(response.body().string());
                                                                            responseData = responejson.getJSONArray("choices").getJSONObject(0)
                                                                                    .getJSONObject("message").getString("content");
                                                                            String[] keyword = responseData.split(",");
                                                                            for (int i = 0; i < keyword.length; i++) {
                                                                                client.newCall(getNews(keyword[i])).enqueue(new Callback() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                                                        System.out.println("News Request: " + e.getMessage());
                                                                                    }

                                                                                    @Override
                                                                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                                                        if (response.isSuccessful()){
                                                                                            try {
                                                                                                JSONObject result = new JSONObject(response.body().string());
                                                                                                JSONArray satu = result.getJSONArray("news");
                                                                                                for (int i = 0; i < satu.length(); i++) {
                                                                                                    datas.add(satu.getJSONObject(i));
                                                                                                }
                                                                                                System.out.println(satu.toString());
                                                                                                editor.putString("alreadyberita", new SimpleDateFormat("dd").format(test.getcurrentdate()));
                                                                                                editor.putString("responeberita", satu.toString());
                                                                                                editor.apply();
                                                                                                NewsListviewAdapter newsListviewAdapter = new NewsListviewAdapter(getActivity().getApplicationContext(), datas);
                                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        loading.setVisibility(View.GONE);
                                                                                                        listView.setVisibility(View.VISIBLE);
                                                                                                        listView.setAdapter(newsListviewAdapter);
                                                                                                    }
                                                                                                });
                                                                                            } catch (JSONException e) {}
                                                                                        } else {
                                                                                            System.out.println("Request Failed. Status Code: " + response.code());
                                                                                            System.out.println("Error Message: " + response.message());
                                                                                            System.out.println("Error Body: " + response.body().string());
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        } catch (JSONException e) {
                                                                        }
                                                                    } else {
                                                                        System.out.println("Request Failed. Status Code: " + response.code());
                                                                        System.out.println("Error Message: " + response.message());
                                                                        System.out.println("Error Body: " + response.body().string());
                                                                    }
                                                                }
                                                            });
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        String jsonstring = sharedPreferences.getString("responeberita", "");
                                                        try {
                                                            JSONArray satu = new JSONArray(jsonstring);
                                                            for (int i = 0; i < satu.length(); i++) {
                                                                datas.add(satu.getJSONObject(i));
                                                            }
                                                            NewsListviewAdapter newsListviewAdapter = new NewsListviewAdapter(getActivity().getApplicationContext(), datas);
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    loading.setVisibility(View.GONE);
                                                                    listView.setVisibility(View.VISIBLE);
                                                                    listView.setAdapter(newsListviewAdapter);
                                                                }
                                                            });
                                                        } catch (JSONException e) {}
                                                    }
                                                }
                                            }, 2000);
                                        }
                                    });
                                    System.out.println("Respone : " + temp);
                                } catch (JSONException e) {
                                }
                            } else {
                                System.out.println("Request Failed. Status Code: " + response.code());
                                System.out.println("Error Message: " + response.message());
                                System.out.println("Error Body: " + response.body().string());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        saranharini.setText(responseData);
                                        loading.setVisibility(View.GONE);
                                        listView.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });

                    saranharini.setText(responseData);
                    LayoutTransition transition = new LayoutTransition();
                    transition.setDuration(600);
                    transition.enableTransitionType(LayoutTransition.CHANGING);
                    constraintLayout.setLayoutTransition(transition);
                }
            } catch (JSONException e) {}
        } else {
            responseData = sharedPreferences.getString("responesaran", "");
            saranharini.setText(responseData);
            if (enablerequest && sharedPreferences.getString("alreadyberita", "").equals(new SimpleDateFormat("dd").format(test.getcurrentdate()))){
                String jsonstring = sharedPreferences.getString("responeberita", "");
                try {
                    JSONArray satu = new JSONArray(jsonstring);
                    for (int i = 0; i < satu.length(); i++) {
                        datas.add(satu.getJSONObject(i));
                    }
                    NewsListviewAdapter newsListviewAdapter = new NewsListviewAdapter(getActivity().getApplicationContext(), datas);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            listView.setAdapter(newsListviewAdapter);
                        }
                    });
                } catch (JSONException e) {}        
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int ii, long l) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Buka Berita")
                        .setMessage("Apakah anda ingin membuka berita ini?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Buka", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent browserintent = null;
                                try {
                                    browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse(datas.get(ii).getString("link")));
                                    startActivity(browserintent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    public Request makeRequest(JSONArray data, String massagee, String roleMassage) throws JSONException {
        StringBuilder prompt = new StringBuilder(massagee);
        for (int i = 0; i < data.length(); i++) {
            if (data.getJSONObject(i).getString("fulldate").equals(dateFormat.format(test.getcurrentdate())) && data.getJSONObject(i).getString("tanggal").equals(yesterday)){
                JSONObject rndata = data.getJSONObject(i);
                prompt.append("Pada tanggal ");
                prompt.append(rndata.getString("tanggal") + rndata.getString("fulldate"));
                prompt.append(", " + rndata.getString("tipe") + " sebesar ");
                prompt.append(rndata.getString("jumlah") + " rupiah");
                prompt.append(" untuk ");
                prompt.append(rndata.getString("subkategori"));
                prompt.append(".\n");
            }
        }
        String rollmass = roleMassage;
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "gpt-4o");

        JSONArray massage = new JSONArray();
        JSONObject systemmassage = new JSONObject();
        systemmassage.put("role", "system");
        systemmassage.put("content", rollmass);
        massage.put(systemmassage);

        JSONObject usermassagee = new JSONObject();
        usermassagee.put("role", "user");
        usermassagee.put("content", prompt);
        massage.put(usermassagee);

        jsonBody.put("messages", massage);
        jsonBody.put("max_tokens", 160);
        jsonBody.put("user", this.data.getString("nama"));

        RequestBody body = RequestBody.create(
                jsonBody.toString(), MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        return request;
    }

    private Request getNews(String query){
        String requestJson = "{\"q\":\"" + query + "\",\"gl\":\"id\"}";
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestJson);
        Request request = new Request.Builder().url("https://google.serper.dev/news")
                .method("POST", body)
                .addHeader("X-API-KEY", "449e741bd3efb65e46472b285a64331c77f6ed1a")
                .addHeader("Content-Type", "application/json")
                .build();
        return request;
    }
}