package com.innovatech.beruang;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EvaluasiPerbulan extends AppCompatActivity {

    PieChart pemasukan, pengeluaran;
    JSONObject dataa;
    JSONArray head, history;
    int indexawal, indexakhir;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int jumlahpemasukan, jumlahpengeluaran;
    JSONObject indexterbesarmasuk, indexterbesarkeluar;
    ArrayList<PieEntry> entriesPemasukan = new ArrayList<>();
    ArrayList<PieEntry> entriesPengeluaran = new ArrayList<>();
    TextView textbanyakdata;
    OkHttpClient client;
    String responseData = "Loading.......";
    TextView textsaran;
    TEST test;
    TextView textpemasukanterbesar, textpengeluaranterbesar;
    TextView texttotalpemasukan, texttotalpengeluaran;
    private static final String API_KEY = "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";
    private static final String BASE_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evaluasi_perbulan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("evalrequest", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false)){
            test = new TEST(true);
        } else {
            test = new TEST(false);
        }

        try {
            FileInputStream fis = openFileInput("Data.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonData = stringBuilder.toString();

            dataa = new JSONObject(jsonData);
            indexterbesarkeluar = new JSONObject();
            indexterbesarmasuk = new JSONObject();
            history = dataa.getJSONArray("history");
            head = dataa.getJSONArray("RangeSubkategori");

            JSONObject pemasukanobj = head.getJSONObject(0).getJSONObject("pemasukan");
            Iterator<String> key = pemasukanobj.keys();
            while (key.hasNext()) {
                String k = key.next();
                entriesPemasukan.add(new PieEntry(pemasukanobj.getInt(k), k));
            }
            JSONObject pengeluaranobj = head.getJSONObject(0).getJSONObject("pengeluaran");
            key = pengeluaranobj.keys();
            while (key.hasNext()) {
                String k = key.next();
                entriesPengeluaran.add(new PieEntry(pengeluaranobj.getInt(k), k));
            }

            fis.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        client = ONLYFORTEST.getUnsafeClient();
        SimpleDateFormat format = new SimpleDateFormat(" LLLL yyyy");
        Calendar calendar = test.getcurrenttime();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String bulanlalu = format.format(calendar.getTime());
        int hariterakhir = calendar.get(Calendar.DAY_OF_MONTH);

        try {
            for (int i = history.length()-1; i >= 0; i--) {
                JSONObject temp = history.getJSONObject(i);
                if (temp.getString("fulldate").equals(bulanlalu)&&temp.getString("tanggal").equals("01")){
                    indexawal = i; //13
                    break;
                }
            }} catch (JSONException e) {}
        try {
            for (int i = 0; i < history.length(); i++) {
                JSONObject temp = history.getJSONObject(i);
                if (temp.getString("fulldate").equals(bulanlalu)&&Integer.parseInt(temp.getString("tanggal"))==hariterakhir){
                    indexakhir = i; //0
                    break;
                }
            }} catch (JSONException e) {}

        try {
            for (int i = indexawal; i >= indexakhir ; i--) {
                JSONObject temp = history.getJSONObject(i);
                if (temp.getString("tipe").equals("pemasukan")) {
                    jumlahpemasukan += Integer.parseInt(temp.getString("jumlah"));
                } else {
                    jumlahpengeluaran += Integer.parseInt(temp.getString("jumlah"));
                }
            }
        } catch (JSONException e) {}

        try {
            int tempjummlah = 0;
            for (int i = indexawal; i >= indexakhir; i--) {
                JSONObject temp = history.getJSONObject(i);
                if (temp.getString("tipe").equals("pemasukan") && Integer.parseInt(temp.getString("jumlah")) > tempjummlah){
                    indexterbesarmasuk = temp;
                }
            }
            tempjummlah = 0;
            for (int i = indexawal; i >= indexakhir; i--) {
                JSONObject temp = history.getJSONObject(i);
                if (temp.getString("tipe").equals("pengeluaran") && Integer.parseInt(temp.getString("jumlah")) > tempjummlah){
                    indexterbesarkeluar = temp;
                }
            }
        } catch (JSONException e) {}


        pemasukan = findViewById(R.id.pemasukan);
        pengeluaran = findViewById(R.id.pengeluaran);
        textpemasukanterbesar = findViewById(R.id.pemasukanterbesar);
        textpengeluaranterbesar = findViewById(R.id.pengeluaranterbesar);
        texttotalpemasukan = findViewById(R.id.totalpemasukan);
        textsaran = findViewById(R.id.tekssaran);
        texttotalpengeluaran = findViewById(R.id.totalpengeluaran);
        textbanyakdata = findViewById(R.id.banyakcatatan);

        textbanyakdata.setText("Rangkuman 1 Bulan Terakhir\nDengan Catatan Sebanyak: " + (indexawal-indexakhir+1));

        PieDataSet dataSet = new PieDataSet(entriesPemasukan, "");
        dataSet.setFormSize(17);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieDataSet dataSet1 = new PieDataSet(entriesPengeluaran, "");
        dataSet1.setFormSize(17);
        dataSet1.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(15);
        pemasukan.setData(data);
        pemasukan.getDescription().setText("Subkategori Pemasukan");
        pemasukan.getDescription().setTextSize(15);
        pemasukan.setEntryLabelTextSize(15);
        pemasukan.invalidate();
        PieData data1 = new PieData(dataSet1);
        data1.setValueTextSize(15);
        pengeluaran.setData(data1);
        pengeluaran.getDescription().setText("Subkategori Pengeluaran");
        pengeluaran.getDescription().setTextSize(15);
        pengeluaran.setEntryLabelTextSize(15);
        pengeluaran.invalidate();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));

        String temptext = "Total Pemasukan Sebesar: \n  Rp"+numberFormat.format(jumlahpemasukan);
        SpannableString span = new SpannableString(temptext);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(EvaluasiPerbulan.this, R.color.md_theme_primaryContainer)), 28, temptext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        texttotalpemasukan.setText(span);

        temptext = "Total Pengeluaran Sebesar: \n  Rp"+numberFormat.format(jumlahpengeluaran);
        span = new SpannableString(temptext);
        span.setSpan(new ForegroundColorSpan(Color.RED), 30, temptext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        texttotalpengeluaran.setText(span);

        try {
            textpemasukanterbesar.setText("Pemasukan Terbesar:\n  Tanggal: " + indexterbesarmasuk.getString("tanggal") + indexterbesarmasuk.get("fulldate") +
                    "\n  Sebesar: Rp" + numberFormat.format(Integer.parseInt(indexterbesarmasuk.getString("jumlah"))) +
                    "\n  Judul: " + indexterbesarmasuk.getString("judul") +"\n  Kategori: " + indexterbesarmasuk.getString("subkategori"));
            textpengeluaranterbesar.setText("Pemasukan Terbesar:\n  Tanggal: " + indexterbesarkeluar.getString("tanggal") + indexterbesarkeluar.get("fulldate") +
                    "\n  Sebesar: Rp" + numberFormat.format(Integer.parseInt(indexterbesarkeluar.getString("jumlah"))) +
                    "\n  Judul: " + indexterbesarkeluar.getString("judul") +"\n  Kategori: " + indexterbesarkeluar.getString("subkategori"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!sharedPreferences.getBoolean("alreadyrequest", false)) {
            try {
                String rolemassage = "Anda adalah asisten keuangan pengguna yang akan memberikan evaluasi bulanan berdasarkan data pengeluaran, pemasukan, dan data pribadi pengguna. Awali dengan pujian berdasarkan kebiasaan baik yang terlihat dari data, diikuti dengan evaluasi dan saran keuangan yang relevan. Gunakan data pribadi pengguna sebagai referensi tambahan agar evaluasi lebih sesuai dengan situasi mereka, buat evaluasi menjadi poin poin agar lebih menarik dan batasi evaluasi hanya 1300 karakter.\n" +
                        "\n" +
                        "Struktur Evaluasi:\n" +
                        "\n" +
                        "Paragraf 1 - Pujian: Beri apresiasi atas pencapaian positif pengguna berdasarkan data keuangan dan pribadi mereka. Contoh: “Anda berhasil menjaga pengeluaran untuk [kategori yang relevan] dalam batas yang sesuai dengan tujuan Anda.”\n" +
                        "Paragraf 2 - Evaluasi dan Saran: Berikan saran keuangan praktis dengan mempertimbangkan situasi pribadi pengguna. Sertakan tren finansial terkini jika relevan untuk memberikan konteks.";

                StringBuilder promptnih = new StringBuilder("Berdasarkan data berikut, berikan evaluasi keuangan bulanan yang inspiratif dengan pujian di paragraf pertama dan evaluasi beserta saran di paragraf kedua. Gunakan data pribadi pengguna agar hasil lebih relevan. Dan ingat maksimal 1300 karakter");
                promptnih.append("\nData Pribadi Pengguna:");
                promptnih.append("Nama:" + dataa.getString("nama"));
                promptnih.append(" ,Wilayah tempat tinggal:" + dataa.getString("wilayah"));
                promptnih.append(" ,Penghasilan:" + dataa.getString("penghasilan"));
                promptnih.append(" ,Target:" + dataa.getString("target"));
                textsaran.setText("Loading...");
                client.newCall(makeRequest(history, promptnih.toString(), rolemassage)).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        System.out.println("Eval Request:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String temp = response.body().string();
                                JSONObject responejson = new JSONObject(temp);
                                responseData = responejson.getJSONArray("choices").getJSONObject(0)
                                        .getJSONObject("message").getString("content");
                                System.out.println("Respone : " + temp);
                                editor.putBoolean("alreadyrequest", true);
                                editor.putString("respone", responseData);
                                editor.apply();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textsaran.setText(responseData);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Request Failed. Status Code: " + response.code());
                            System.out.println("Error Message: " + response.message());
                            System.out.println("Error Body: " + response.body().string());
                        }
                    }
                });
            } catch (Exception e) {
            }
        } else {
            textsaran.setText(sharedPreferences.getString("respone", ""));
        }
    }

    public Request makeRequest(JSONArray data, String massagee, String roleMassage) throws JSONException {
        StringBuilder prompt = new StringBuilder(massagee);
        for (int i = indexawal; i >= indexakhir; i--) {
            JSONObject rndata = data.getJSONObject(i);
            prompt.append("Pada tanggal ");
            prompt.append(rndata.getString("tanggal") + rndata.getString("fulldate"));
            prompt.append(", " + rndata.getString("tipe") + " sebesar ");
            prompt.append(rndata.getString("jumlah") + " rupiah");
            prompt.append(" untuk ");
            prompt.append(rndata.getString("subkategori"));
            prompt.append(".\n");
        }
        System.out.println(prompt.toString());

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
        jsonBody.put("max_tokens", 450);
        jsonBody.put("user", dataa.getString("nama"));

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
}