package com.example.whatappclone.corona;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.whatappclone.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CoronaMainActivity extends AppCompatActivity {
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.corona_activity_corona_main);

        pieChart = findViewById(R.id.coronalPieChart);

        String url = "https://corona.lmao.ninja/v2/all";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response.toString());
                    int cases  = Integer.parseInt(jsonObject.getString("cases"));
                    int todayCases = Integer.parseInt(jsonObject.getString("todayCases"));
//                    int deaths = Integer.parseInt(jsonObject.getString("Deaths"));
                    int todayDeaths = Integer.parseInt(jsonObject.getString("todayDeaths"));
                    int recovered = Integer.parseInt(jsonObject.getString("recovered"));
                    //int active = Integer.parseInt(jsonObject.getString("Active"));

                    int critical = Integer.parseInt(jsonObject.getString("critical"));


                    int affectedCountries = Integer.parseInt(jsonObject.getString("affectedCountries"));

                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
                    pieEntries.add(new PieEntry(affectedCountries,"Affected Countries"));
                    pieEntries.add(new PieEntry(cases,"Cases"));
                    pieEntries.add(new PieEntry(recovered,"Recovered"));
//                    pieEntries.add(new PieEntry(todayCases,"Today Cases"));
//                    pieEntries.add(new PieEntry(todayDeaths,"Today Deaths"));


                    PieDataSet pieDataSet = new PieDataSet(pieEntries,"Statistics");

                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    pieDataSet.setValueTextColor(Color.BLACK);
                    pieDataSet.setValueTextSize(16f);

                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);
                    pieChart.getDescription().setEnabled(true);
                    pieChart.setCenterText("Statistics");
                    pieChart.animate();




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CoronaMainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

}
