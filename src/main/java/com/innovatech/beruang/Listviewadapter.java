package com.innovatech.beruang;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Listviewadapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    JSONObject jsonObjectRead;
    JSONArray history;
    String date, newdate;
    boolean allshow = false;
    NumberFormat numberFormat;
    String rndate="";
    ImageView update, delete, close;
    int itemposition, banyak = 0;
    TEST test;

    public Listviewadapter(Context appcontext, JSONObject jsonObjectRead, boolean allshow, boolean showeval){
        this.context = appcontext;
        this.jsonObjectRead = jsonObjectRead;
        this.allshow = allshow;
        this.numberFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        try {
            this.history = jsonObjectRead.getJSONArray("history");
        } catch (JSONException e) {

        }
        if (showeval){
            test = new TEST(true);
        } else {
            test = new TEST(false);
        }
        inflater = (LayoutInflater.from(appcontext));
    }

    @Override
    public int getCount() {
        if (banyak==0&&!allshow){
            date = new SimpleDateFormat("dd").format(test.getcurrentdate());
            Calendar calendar = test.getcurrenttime();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = calendar.getTime();
            newdate = new SimpleDateFormat("dd").format(yesterday);
            try {
                for (int i = 0; i < history.length(); i++) {
                    if (history.getJSONObject(i).getString("tanggal").equals(date)||history.getJSONObject(i).getString("tanggal").equals(newdate)){
                        banyak = banyak+1;
                    } else {
                        itemposition++;
                    }
                }
                banyak = banyak*2+1;
                return banyak;
            } catch (Exception e) {

                return 0;
            }
        } else if (allshow) {
            banyak = history.length()*2;
            return banyak;
        }
        return banyak;
    }

    @Override
    public Object getItem(int i) {
        try {
            JSONObject object = history.getJSONObject(i);
            return object;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view1, ViewGroup viewGroup) {

        if (i % 2 == 1){
            view1 = inflater.inflate(R.layout.listviewstyle, viewGroup, false);
            TextView jumlah = view1.findViewById(R.id.jumlah);
            TextView judul = view1.findViewById(R.id.judul);
            update = view1.findViewById(R.id.updatebutton);
            delete = view1.findViewById(R.id.deletebutton);
            close = view1.findViewById(R.id.closebutton);

            try {
                JSONObject object = history.getJSONObject(i/2);
                itemposition += 1;
                String tipe = object.getString("tipe");
                int jumlahint = Integer.parseInt(object.getString("jumlah"));
                if (tipe.equals("pemasukan")){
                    jumlah.setTextColor(ContextCompat.getColor(context, R.color.md_theme_primaryContainer));
                    jumlah.setText("+Rp"+numberFormat.format(jumlahint));
                } else {
                    jumlah.setTextColor(Color.RED);
                    jumlah.setText("-Rp"+numberFormat.format(jumlahint));
                }
                String textt = "* |"+object.getString("subkategori");
                if (!object.getString("judul").isEmpty()){
                    textt.replace("*", "object.getString()");
                }
                judul.setText(textt);
                View finalView = view1;
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finalView.animate().scaleY(1f).scaleX(1f).setDuration(300).start();
                        hideButton();
                    }
                });
            } catch (JSONException e) {

            }
        } else if (i == banyak-1 && !allshow) {
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setBackgroundResource(R.drawable.border_portofolio);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50, 35, 50, 0);
            ll.setLayoutParams(layoutParams);
            ll.setPadding(30, 20, 30, 20);

            TextView textView = new TextView(context);
            textView.setText("Lebih detail->");
            textView.setTextSize(20f);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.robotomedium));
            ll.addView(textView);
            frameLayout.addView(ll);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ShowFullData.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });

            view1 = frameLayout;
        } else {
            view1 = inflater.inflate(R.layout.layout_divider, viewGroup, false);
            TextView textView = view1.findViewById(R.id.textViewDivider);
            LinearLayout box1 = view1.findViewById(R.id.box1);
            String tanggal, tanggaltarget = "";
            try {
                tanggaltarget = history.getJSONObject((i/2)-1).getString("tanggal");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject object = history.getJSONObject(i/2);
                tanggal = object.getString("tanggal");
                if (!tanggal.equals(tanggaltarget)){
                    textView.setText(tanggal+object.getString("fulldate"));
                    rndate = tanggal;
                } else {
                    box1.setVisibility(View.GONE);
                }
            } catch (JSONException e) {

            }
        }
        return view1;
    }
    public void hideButton(){
        delete.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        close.setVisibility(View.GONE);
    }
}
