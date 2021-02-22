package com.example.magicbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class BoxAdapter extends BaseAdapter {
    private List<String> name;
    private Context context;
    private LayoutInflater li;

    public BoxAdapter(List<String> name, Context context) {
        this.name = name;
        this.context = context;
        this.li = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = li.inflate(R.layout.row_items_box, viewGroup, false);
        }

        TextView tx = view.findViewById(R.id.idBoxNameTx);
        tx.setText(name.get(i));

        return view;
    }
}