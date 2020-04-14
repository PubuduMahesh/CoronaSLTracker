package com.codenerdz.coronasltracker.activity.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codenerdz.coronasltracker.R;
import com.codenerdz.coronasltracker.toolkit.ConstantToolkit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class GraphDrawer extends DialogFragment
{

    private View view;
    private WebView webView;
    private String graphType;

    JSONArray jsonArraySL = new JSONArray();;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.common_graph_layout, container, false);
        initChart();
        return view;
    }

    private void initChart()
    {
        View stub =view.findViewById(R.id.line_chart_stub);

        if (stub instanceof ViewStub)
        {
            ((ViewStub)stub).setVisibility(View.VISIBLE);
            webView = (WebView)view.findViewById(R.id.daily_confirmed_line_chart_webview);

            WebSettings webSettings =
                    webView.getSettings();

            webSettings.setJavaScriptEnabled(true);

            webView.setWebChromeClient(new WebChromeClient());

            webView.setWebViewClient(new WebViewClient()
            {
                @Override
                public void onPageFinished(
                        WebView view,
                        String url)
                {
                    if(jsonArraySL.length() == 0)
                    {
                        loadLineChartData();
                    }
                }
            });

            selectLoadURL();
            webSettings.setDomStorageEnabled(true);

        }
    }

    private void selectLoadURL() {

        if(getFragmentManager().findFragmentByTag(ConstantToolkit.DAILY_CONFIRMED_CASES_FRAGMENT) != null)
        {
            webView.loadUrl("file:///android_asset/"+"html/dailyConfirmedLineChart.html");
            graphType = ConstantToolkit.DAILY_CONFIRMED_CASES_FRAGMENT;
        }
        else if(getFragmentManager().findFragmentByTag(ConstantToolkit.DAILY_DEATH_CASES_FRAGMENT) != null)
        {
            webView.loadUrl("file:///android_asset/"+"html/dailyDeathsLineChart.html");
            graphType = ConstantToolkit.DAILY_DEATH_CASES_FRAGMENT;
        }
        else if(getFragmentManager().findFragmentByTag(ConstantToolkit.DAILY_RECOVERS_CASES_FRAGMENT) != null)
        {
            webView.loadUrl("file:///android_asset/"+"html/dailyRecoversLineChart.html");
            graphType = ConstantToolkit.DAILY_RECOVERS_CASES_FRAGMENT;
        }
        else if(getFragmentManager().findFragmentByTag(ConstantToolkit.DAILY_ACTIVE_CASES_FRAGMENT) != null)
        {
            webView.loadUrl("file:///android_asset/"+"html/activeCases.html");
            graphType = ConstantToolkit.DAILY_ACTIVE_CASES_FRAGMENT;
        }

    }

    private void loadLineChartData()
    {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://pomber.github.io/covid19/timeseries.json";
        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null != response) {
                            try {
                                jsonArraySL = response.getJSONArray("Sri Lanka");

                                switch (graphType)
                                {
                                    case ConstantToolkit.DAILY_CONFIRMED_CASES_FRAGMENT:
                                        webView.loadUrl("javascript:loadLinearChart" +
                                                "("+modifyStringBuilder(ConstantToolkit.
                                                API_CONFIRMED)+")");
                                        break;
                                    case ConstantToolkit.DAILY_DEATH_CASES_FRAGMENT:
                                        webView.loadUrl("javascript:loadLinearChart" +
                                                "("+modifyStringBuilder(ConstantToolkit.
                                                API_DEATH).toString()+")");
                                        break;
                                    case ConstantToolkit.DAILY_RECOVERS_CASES_FRAGMENT:
                                        webView.loadUrl("javascript:loadLinearChart" +
                                                "("+modifyStringBuilder(ConstantToolkit.
                                                API_RECOVERED).toString()+")");
                                        break;
                                    case ConstantToolkit.DAILY_ACTIVE_CASES_FRAGMENT:
                                        webView.loadUrl("javascript:loadLinearChart" +
                                                "("+modifyStringBuilder(ConstantToolkit.
                                                API_ACTIVE).toString()+","+modifyStringBuilder
                                                (ConstantToolkit.API_CONFIRMED).toString()+","
                                                +modifyStringBuilder(ConstantToolkit.
                                                API_RECOVERED).toString()+")");
                                        break;
                                    default:
                                        break;

                                }

                            } catch (Exception e) {

                            }
                        }
                    }

                    private StringBuilder modifyStringBuilder(String apiSelectionAttribute) throws JSONException
                    {

                        StringBuilder textSL = new StringBuilder();
                        textSL.append("[");
                        if(apiSelectionAttribute.equals(ConstantToolkit.API_ACTIVE))
                        {
                            for(int i=0;i<jsonArraySL.length();i++)
                            {
                                textSL.append("{\"date\":\""+formatDate(jsonArraySL.getJSONObject(i).
                                        get("date").toString())+"\",\"value\":\""+
                                        calculateActiveCases(i)+"\"},");

                            }
                        }
                        else
                        {
                            for(int i=0;i<jsonArraySL.length();i++)
                            {
                                textSL.append("{\"date\":\""+formatDate(jsonArraySL.getJSONObject(i).
                                        get("date").toString())+"\",\"value\":\""+
                                        jsonArraySL.getJSONObject(i).
                                                get(apiSelectionAttribute).toString()
                                        +"\"},");

                            }
                        }
                        textSL.deleteCharAt(textSL.length()-1);
                        textSL.append("]");
                        return textSL;
                    }

                    private String formatDate(String date) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String[] dateMonthArray = date.split("-");
                        return (dateMonthArray[0]+"-"+
                                String.valueOf(formatter.format(Integer.parseInt(dateMonthArray[1])))+"-"+
                                String.valueOf(formatter.format(Integer.parseInt(dateMonthArray[2])))).toString();

                    }

                    private String calculateActiveCases(int i) throws JSONException
                    {
                        int returnValue =  Integer.parseInt(jsonArraySL.getJSONObject(i).
                                get("confirmed").toString())-Integer.
                                parseInt(jsonArraySL.getJSONObject(i).get("recovered").toString());

                        return String.valueOf(returnValue);
                    }
                },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

}
