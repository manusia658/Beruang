package com.innovatech.beruang;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class SubkategoriAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> item;
    LayoutInflater inflater;
    ImageView deletebutton;
    SharedPreferences shrd;
    String listmode;
    SharedPreferences.Editor editor;

    public SubkategoriAdapter(Context context, ArrayList<String> item, String listmode){
        this.context = context;
        this.item = item;
        this.listmode = listmode;
        inflater = (LayoutInflater.from(context));
        SharedPreferences sharedPreferences = context.getSharedPreferences("Listdata", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.simplesubkategorilist, viewGroup, false);
        TextView textView = view.findViewById(R.id.katergori);
        deletebutton = view.findViewById(R.id.delete);
        textView.setText(item.get(i));
        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.remove(i);
                editor.putString(listmode, new Gson().toJson(item));
                editor.apply();
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
