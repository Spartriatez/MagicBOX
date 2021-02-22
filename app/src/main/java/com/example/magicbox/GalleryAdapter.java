package com.example.magicbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

class GridAdapter extends BaseAdapter {
    private List<String> paths;
    private List<String> name;
    private Context context;
    private LayoutInflater li;

    public GridAdapter(List<String> paths, List<String> name, Context context) {
        this.paths = paths;
        this.name = name;
        this.context = context;
        this.li = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return paths.size();
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
            view = li.inflate(R.layout.row_items, viewGroup, false);
        }

        TextView tx = view.findViewById(R.id.txName);
        tx.setText(name.get(i));

        ImageView iv = view.findViewById(R.id.imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i), options);
        iv.setImageBitmap(bitmap);
            /*ImageView imageView=new ImageView(getApplicationContext());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i), options);
            imageView.setImageBitmap(bitmap);*/
        return view;
    }
}