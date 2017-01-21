package com.theoc.metufood;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TabHost;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import android.provider.Settings.Secure;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    ListView lv2;
    ArrayList<String> data;
    ArrayList<String> imagedata;
    ArrayList<String> data2;
    ArrayList<String> imagedata2;
    Adapter adapter;
    Adapter2 adapter2;
    ProgressDialog pd;
    int yearx, monthx, dayx;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, "b5c6f183bcfab631e631c094b5e4fe6a");
        lv = (ListView) findViewById(R.id.listView);
        lv2 = (ListView) findViewById(R.id.listView2);
        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec specs = tabHost.newTabSpec("Öğle Yemeği");
        specs.setContent(R.id.tab1);
        specs.setIndicator("Öğle Yemeği");
        tabHost.addTab(specs);
        TabHost.TabSpec specs2 = tabHost.newTabSpec("Akşam Yemeği");
        specs2.setContent(R.id.tab2);
        specs2.setIndicator("Akşam Yemeği");
        tabHost.addTab(specs2);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading menu...");
        pd.setCanceledOnTouchOutside(false);
        cal = Calendar.getInstance();
        new AsyncTask2().execute();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });
        JSONObject json  = new JSONObject();
        try {
            json.put("Device ID:", Secure.getString(this.getContentResolver(),
                    Secure.ANDROID_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Impression", json);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0){
            DatePickerDialog dpd = new DatePickerDialog(this,dpickerListener, yearx, monthx, dayx);
            dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
            long daysInMonth = cal.getActualMaximum(Calendar.DATE);
            long millisInMonth = daysInMonth * 86400000L;
            dpd.getDatePicker().setMaxDate(cal.getTimeInMillis() + (millisInMonth - (dayx * 86400000L)));
            return dpd;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            yearx = year;
            monthx = monthOfYear;
            dayx = dayOfMonth;
            new AsyncTask2().execute();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                        | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                        | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            }
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        } else if (id == R.id.refresh){
            new AsyncTask2().execute();
        } else {
            showDialog(0);
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncTask2 extends android.os.AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            pd.show();
            if (yearx == 0) {
                yearx = cal.get(Calendar.YEAR);
                monthx = cal.get(Calendar.MONTH);
                dayx = cal.get(Calendar.DAY_OF_MONTH);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            Document doc = null;
            try {
                data = new ArrayList<>();
                imagedata = new ArrayList<>();
                data2 = new ArrayList<>();
                imagedata2 = new ArrayList<>();
                if (monthx + 1 >= 10) {
                    doc = Jsoup.connect("http://kafeterya.metu.edu.tr/tarih/" + dayx + "-" + (monthx + 1) + "-" + yearx).timeout(20*1000).get();
                } else {
                    doc = Jsoup.connect("http://kafeterya.metu.edu.tr/tarih/" + dayx + "-0" + (monthx + 1) + "-" + yearx).timeout(20*1000).get();
                }
                Element body = doc.body();
                Elements e = body.getElementsByAttributeValue("class", "yemek-listesi");
                for (int i =0;i<e.size();i++) {
                    Element linkk = e.get(i);
                    String check = linkk.getElementsByTag("h3").text();
                    if (check.equals("Öğle Yemeği")) {
                        Elements l = linkk.getElementsByAttributeValue("class", "yemek");
                        for (int m = 0; m < l.size(); m++) {
                            Element linkk2 = l.get(m);
                            String text = linkk2.getElementsByTag("p").text();
                            Element imageE = linkk2.getElementsByTag("img").first();
                            String image = imageE.absUrl("src");
                            data.add(text);
                            imagedata.add(image);
                        }
                    } else {
                        Elements f = linkk.getElementsByAttributeValue("class", "yemek");
                        for (int z = 0; z < f.size(); z++) {
                            Element linkk2 = f.get(z);
                            String text = linkk2.getElementsByTag("p").text();
                            Element imageE = linkk2.getElementsByTag("img").first();
                            String image = imageE.absUrl("src");
                            data2.add(text);
                            imagedata2.add(image);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new Adapter(MainActivity.this, data, imagedata);
            adapter2 = new Adapter2(MainActivity.this, data2, imagedata2);
            lv.setAdapter(adapter);
            lv2.setAdapter(adapter2);
            pd.cancel();
        }
    }
}
