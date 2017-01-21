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

public class Adapter extends BaseAdapter {

    ArrayList<String> data;
    ArrayList<String> imagedata;
    Context context;

    public Adapter(Context context, ArrayList<String> data, ArrayList<String> imagedata) {
        this.context = context;
        this.data = data;
        this.imagedata = imagedata;
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
        String textString = data.get(position);
        holder.mTextView.setText(textString);
        String imageString = imagedata.get(position);
        Picasso .with(context)
                .load(imageString)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.mImageView);
        return row;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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
