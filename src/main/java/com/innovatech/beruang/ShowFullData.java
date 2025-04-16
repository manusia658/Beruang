package com.innovatech.beruang;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

public class ShowFullData extends AppCompatActivity {

    ListView history;
    String jsonData;
    Button sort, export;
    JSONObject data;
    JSONArray historyarray;
    TEST test;
    Calendar minDate;
    Calendar maxDate;
    DatePickerDialog dari, sampai;
    int startindex, endindex;
    String exporttypeselecteed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_full_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false)){
            test = new TEST(true);
        } else {
            test = new TEST(false);
        }
        minDate = test.getcurrenttime();
        maxDate = test.getcurrenttime();

        try {
            FileInputStream fis = openFileInput("Data.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            jsonData = stringBuilder.toString();
            data = new JSONObject(jsonData);
            historyarray = data.getJSONArray("history");

            JSONObject first = historyarray.getJSONObject(0);
            JSONObject last = historyarray.getJSONObject(historyarray.length()-1);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLLL yyyy");

            minDate.setTime(dateFormat.parse(last.getString("tanggal") + last.getString("fulldate")));
            maxDate.setTime(dateFormat.parse(first.getString("tanggal") + first.getString("fulldate")));

            endindex = historyarray.length()-1;

            fis.close();
        } catch (IOException | JSONException | ParseException e) {}

        history = findViewById(R.id.historii);
        sort = findViewById(R.id.sortbutton);
        export = findViewById(R.id.export);
        Listviewadapter listviewadapter = new Listviewadapter(this, data, true, getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false));
        history.setAdapter(listviewadapter);

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(ShowFullData.this);
                dialog.setContentView(R.layout.exportpopuplayout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);

                AutoCompleteTextView exportdipilih = dialog.findViewById(R.id.typeexport);
                CheckBox daritanggal = dialog.findViewById(R.id.daritanggal);
                CheckBox sorting = dialog.findViewById(R.id.sorting);
                LinearLayout datelayout = dialog.findViewById(R.id.sortdate);
                Button daritanggalpicker = dialog.findViewById(R.id.pickerdari);
                Button sampaitanggalpicker = dialog.findViewById(R.id.pickersampai);
                TextInputLayout sortlayout = dialog.findViewById(R.id.sortinglayout);
                AutoCompleteTextView typesorting = dialog.findViewById(R.id.sorttype);
                Button simpan = dialog.findViewById(R.id.done);
                dialog.findViewById(R.id.cancell).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) {dialog.dismiss();}});

                dari = new DatePickerDialog(ShowFullData.this);
                dari.getDatePicker().setMinDate(minDate.getTimeInMillis());
                dari.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                sampai = new DatePickerDialog(ShowFullData.this);
                sampai.getDatePicker().setMinDate(minDate.getTimeInMillis());
                sampai.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                exportdipilih.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        exporttypeselecteed = exportdipilih.getText().toString();
                    }
                });

                daritanggal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (daritanggal.isChecked()){
                            datelayout.setVisibility(View.VISIBLE);
                        } else {
                            datelayout.setVisibility(View.GONE);
                        }
                    }
                });
                sorting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sorting.isChecked()){
                            sortlayout.setVisibility(View.VISIBLE);
                        } else {
                            sortlayout.setVisibility(View.GONE);
                        }
                    }
                });

                daritanggalpicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dari.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                Calendar selected = test.getcurrenttime();
                                selected.set(i, i1, i2);

                                sampai.getDatePicker().setMinDate(selected.getTimeInMillis());

                                String tanggal = new SimpleDateFormat("dd", Locale.getDefault()).format(selected.getTime());
                                String bulta = new SimpleDateFormat(" LLLL yyyy", Locale.getDefault()).format(selected.getTime());
                                String printtext = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selected.getTime());

                                daritanggalpicker.setText("Dari Tanggal: "+printtext);

                                try {
                                    for (int j = historyarray.length()-1; j >= 0; j--) {
                                        JSONObject temp = historyarray.getJSONObject(j);
                                        if (temp.getString("tanggal").equals(tanggal) && temp.getString("fulldate").equals(bulta)){
                                            startindex = j;
                                            break;
                                        }
                                    }} catch (JSONException e) {}
                            }
                        });
                        dari.show();
                    }
                });
                sampaitanggalpicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sampai.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                Calendar selected = test.getcurrenttime();
                                selected.set(i, i1, i2);

                                dari.getDatePicker().setMaxDate(selected.getTimeInMillis());

                                String tanggal = new SimpleDateFormat("dd", Locale.getDefault()).format(selected.getTime());
                                String bulta = new SimpleDateFormat(" LLLL yyyy", Locale.getDefault()).format(selected.getTime());
                                String printtext = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selected.getTime());

                                sampaitanggalpicker.setText("Sampai Tanggal: "+printtext);

                                try {
                                    for (int j = 0; j < historyarray.length(); j++) {
                                        JSONObject temp = historyarray.getJSONObject(j);
                                        if (temp.getString("tanggal").equals(tanggal) && temp.getString("fulldate").equals(bulta)){
                                            endindex = j;
                                            break;
                                        }
                                    }} catch (JSONException e) {}
                            }
                        });
                        sampai.show();
                    }
                });

                simpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONArray temp = historyarray;
                        String sorttypeselect = "Terbaru";
                        if (daritanggal.isChecked()){
                            temp = new JSONArray();
                            try {
                                for (int i = endindex; i < startindex+1; i++) {
                                    temp.put(historyarray.getJSONObject(i));
                                }} catch (JSONException e) {}
                        }
                        if (sorting.isChecked()){
                            try {
                                if (typesorting.getText().toString().isEmpty()){
                                    Toast.makeText(ShowFullData.this, "Tipe Sorting Otomatis Terbaru", Toast.LENGTH_LONG).show();
                                }
                                if (typesorting.getText().toString().equals("Pengeluaran terbesar")){
                                    temp = sortterbesar(temp).getJSONArray("history");
                                    sorttypeselect = "Pengeluaran terbesar";
                                } else if (typesorting.getText().toString().equals("Terlama")) {
                                    temp = sortterlama(temp).getJSONArray("history");
                                    sorttypeselect = "Terlama";
                                } else if (typesorting.getText().toString().equals("Pengeluaran terkecil")) {
                                    temp = sortterkecil(temp).getJSONArray("history");
                                    sorttypeselect = "Pengeluaran terkecil";
                                }} catch (JSONException e) {}
                        }
                        if (exportdipilih.getText().toString().equals("PDF")){
                            String filepath = Environment.getExternalStorageDirectory() + "/Download/hasil.pdf";
                            savetoPDF(temp, filepath, sorttypeselect);
                        }
//                        else if (exportdipilih.getText().toString().equals("XLSX")) {
//                            String filepath = Environment.getExternalStorageDirectory() + "/Download/hasil.xlsx";
//                            try {
//                                savetoXLSX(temp, filepath);} catch (JSONException e) {}
//                        }
                    }
                });
                dialog.show();
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ShowFullData.this, sort);
                popupMenu.getMenuInflater().inflate(R.menu.sortmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        sort.setText("Sorting: "+ menuItem.getTitle());
                        if (menuItem.getTitle().equals("Pengeluaran terbesar")){
                            try {
                                Listviewadapter listviewadapter = new Listviewadapter(ShowFullData.this, sortterbesar(data.getJSONArray("history")), true
                                ,getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false));
                                history.setAdapter(listviewadapter);
                                listviewadapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (menuItem.getTitle().equals("Pengeluaran terkecil")) {
                            try {
                                Listviewadapter listviewadapter = new Listviewadapter(ShowFullData.this, sortterkecil(data.getJSONArray("history")), true,
                                        getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false));
                                history.setAdapter(listviewadapter);
                                listviewadapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (menuItem.getTitle().equals("Terbaru")) {
                            Listviewadapter listviewadapter = new Listviewadapter(ShowFullData.this, data, true,
                                    getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false));
                            history.setAdapter(listviewadapter);
                            listviewadapter.notifyDataSetChanged();
                        } else if (menuItem.getTitle().equals("Terlama")) {
                            try {
                                Listviewadapter listviewadapter = new Listviewadapter(ShowFullData.this, sortterlama(data.getJSONArray("history")), true,
                                        getSharedPreferences("BETATEST", MODE_PRIVATE).getBoolean("showeval", false));
                                history.setAdapter(listviewadapter);
                                listviewadapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void savetoXLSX(JSONArray jsonArray, String filePath) throws JSONException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Membuat header dari key di JSON
        if (jsonArray.length() > 0) {
            JSONObject firstObject = jsonArray.getJSONObject(0);
            Row headerRow = sheet.createRow(0);

            Iterator<String> keys = firstObject.keys();
            int cellIndex = 0;
            while (keys.hasNext()) {
                String key = keys.next();
                Cell cell = headerRow.createCell(cellIndex++);
                cell.setCellValue(key);
            }

            // Memasukkan data JSON ke baris selanjutnya
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Row row = sheet.createRow(i + 1);
                cellIndex = 0;

                keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Cell cell = row.createCell(cellIndex++);
                    cell.setCellValue(jsonObject.get(key).toString());
                }
            }
        }

        // Menyimpan workbook ke file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    JSONObject sortterbesar(JSONArray history) throws JSONException {
        String key = "jumlah";
        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            for (int i = 0; i < history.length(); i++) {
                list.add(history.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(list, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    int compare = Integer.compare(Integer.parseInt(b.getString(key)), Integer.parseInt(a.getString(key)));
                    return compare;
                } catch (JSONException e) {
                    return  0;
                }
            }
        });
        JSONArray baru = new JSONArray(list);
        return new JSONObject().put("history", baru);
    }

    JSONObject sortterkecil(JSONArray history) throws JSONException {
        String key = "jumlah";
        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            for (int i = 0; i < history.length(); i++) {
                list.add(history.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(list, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    int compare = Integer.compare(Integer.parseInt(a.getString(key)), Integer.parseInt(b.getString(key)));
                    return compare;
                } catch (JSONException e) {
                    return 0;
                }
            }
        });

        JSONArray baru = new JSONArray(list);
        return new JSONObject().put("history", baru);
    }

    JSONObject sortterlama(JSONArray history) throws JSONException {
        JSONArray baru = new JSONArray();
        try {
            for (int i = history.length()-1; i >= 0; i--) {
                baru.put(history.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject().put("history", baru);
    }

    private void savetoPDF(JSONArray temp, String filepath, String sortt) {
        PdfDocument pdf = null;
        Document document = null;
        try {
            PdfWriter pdfWriter = new PdfWriter(filepath);
            pdf = new PdfDocument(pdfWriter);
            document = new Document(pdf);
            document.add(new Paragraph("Data pengeluaran keuangan").setTextAlignment(TextAlignment.CENTER));
            try {
                document.add(new Paragraph("Nama pengguna: " + data.getString("nama")));} catch (JSONException e) {}
            try {
                document.add(new Paragraph("Jenjang pengguna: " + data.getString("jenjang")));} catch (JSONException e) {}
            try {
                document.add(new Paragraph("Wilayah yang ditempati: " + data.getString("wilayah")));} catch (JSONException e) {}
            document.add(new Paragraph("Sort Bedasarkan: "+sortt));
            for (int i = 0; i < temp.length(); i++) {
                Table table = new Table(3);
                JSONObject temp1 = null;
                try {
                    temp1 = temp.getJSONObject(i);} catch (JSONException e) {}
                try {
                    table.addCell("Judul: "+temp1.getString("judul"));} catch (JSONException e) {}
                try {
                    table.addCell("Jumlah: " + temp1.getString("jumlah"));} catch (JSONException e) {}
                try {
                    table.addCell("Tipe: "+ temp1.getString("tipe"));} catch (JSONException e) {}
                try {
                    table.addCell("Tanggal: "+temp1.getString("tanggal")+temp1.getString("fulldate"));} catch (JSONException e) {}
                try {
                    table.addCell("Subkategori: "+temp1.getString("subkategori"));} catch (JSONException e) {}
                table.setBorder(new SolidBorder(1));
                document.add(table);
            }
            document.close();
            pdf.close();
        } catch (FileNotFoundException e) {}
    }
}