package com.theoc.metufood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter2 extends BaseAdapter {
    ArrayList<String> data2;
    ArrayList<String> imagedata2;
    Context context;

    public Adapter2(Context context, ArrayList<String> data2, ArrayList<String> imagedata2) {
        this.context = context;
        this.data2 = data2;
        this.imagedata2 = imagedata2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_yemekhane, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        String textString = data2.get(position);
        holder.mTextView.setText(textString);
        String imageString = imagedata2.get(position);
        Picasso.with(context)
                .load(imageString)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.mImageView);
        return row;
    }

    @Override
    public int getCount() {
        return data2.size();
    }

    @Override
    public Object getItem(int position) {
        return data2.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        ImageView mImageView;
        TextView mTextView;

        public ViewHolder(View v) {
            mImageView = (ImageView) v.findViewById(R.id.imageView);
            mTextView = (TextView) v.findViewById(R.id.textView);
        }
    }
}
