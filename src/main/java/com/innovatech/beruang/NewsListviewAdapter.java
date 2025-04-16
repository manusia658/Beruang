package com.innovatech.beruang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewsListviewAdapter extends BaseAdapter {

    ArrayList<JSONObject> news;
    LayoutInflater inflater;
    Context context;
    ImageView image;

    public NewsListviewAdapter(Context context, ArrayList<JSONObject> news){
        this.news = news;
        this.context = context;
        inflater = (LayoutInflater.from(context));
    }
    @Override
    public int getCount() {
        return news.size();
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
        view = inflater.inflate(R.layout.newsviewstyle, viewGroup, false);

        TextView judul = view.findViewById(R.id.judul);
        TextView source = view.findViewById(R.id.source);
        TextView date = view.findViewById(R.id.date);
        image = view.findViewById(R.id.photo);

        try {
            JSONObject data = news.get(i);
            judul.setText(data.getString("title"));
            source.setText(data.getString("source"));
            date.setText(data.getString("date"));
            Glide.with(context).load(data.getString("imageUrl")).into(image);
        } catch (JSONException e) {

        }
        return view;
    }
}
