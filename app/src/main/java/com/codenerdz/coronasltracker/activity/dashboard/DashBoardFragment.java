package com.codenerdz.coronasltracker.activity.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codenerdz.coronasltracker.R;
import com.codenerdz.coronasltracker.toolkit.ConstantToolkit;

import org.json.JSONObject;

public class DashBoardFragment extends Fragment
{
    View view;

    private TextView totalCases;
    private TextView deaths;
    private TextView activeCases;
    private TextView recovers;
    private TextView lastUpdate;
    private TextView newCases;
    private TextView newDeaths;
    private TextView suspectedCases;
    private CardView cardViewTotalCases;
    private CardView cardViewTotalDeaths;
    private CardView cardViewTotalRecovers;
    private CardView cardViewActiveCases;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.dashboard_layout, container, false);

        totalCases = view.findViewById(R.id.total_cases);
        deaths = view.findViewById(R.id.total_deaths);
        activeCases = view.findViewById(R.id.active_cases);
        recovers = view.findViewById(R.id.total_recovers);
        lastUpdate = view.findViewById(R.id.last_update);
        newCases = view.findViewById(R.id.new_cases);
        newDeaths = view.findViewById(R.id.new_deaths);
        suspectedCases = view.findViewById(R.id.suspected_cases);

        cardViewTotalCases = view.findViewById(R.id.card_total_cases);
        cardViewTotalDeaths = view.findViewById(R.id.card_total_deaths);
        cardViewTotalRecovers = view.findViewById(R.id.card_total_recovers);
        cardViewActiveCases = view.findViewById(R.id.card_active_cases);

        requestCoronaUpdate();
        addTouchEvent();
        return view;
    }

    private void requestCoronaUpdate()
    {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://hpb.health.gov.lk/api/get-current-statistical";
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if (null != response)
                        {
                            try
                            {
                                totalCases.setText(response.getJSONObject("data").get(
                                        "local_total_cases").toString());
                                deaths.setText(response.getJSONObject("data").get("local_deaths").toString());
                                activeCases.setText(response.getJSONObject("data").get(
                                        "local_active_cases").toString());
                                recovers.setText(response.getJSONObject("data").get(
                                        "local_recovered").toString());
                                newCases.setText(response.getJSONObject("data").get(
                                        "local_new_cases").toString());
                                newDeaths.setText(response.getJSONObject("data").get(
                                        "local_new_deaths").toString());
                                suspectedCases.setText(response.getJSONObject("data").get(
                                        "local_total_number_of_individuals_in_hospitals").toString());
                                lastUpdate.setText(response.getJSONObject("data").get(
                                        "update_date_time").toString());


                            } catch (Exception e)
                            {
                                //e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener()
        {

            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        });
        queue.add(request);
    }

    private void addTouchEvent()
    {
        cardViewTotalCases.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                openGraphDrawingFragment(ConstantToolkit.DAILY_CONFIRMED_CASES_FRAGMENT);
                return true;
            }
        });

        cardViewTotalDeaths.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                openGraphDrawingFragment(ConstantToolkit.DAILY_DEATH_CASES_FRAGMENT);
                return true;
            }
        });

        cardViewTotalRecovers.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                openGraphDrawingFragment(ConstantToolkit.DAILY_RECOVERS_CASES_FRAGMENT);
                return true;
            }
        });

        cardViewActiveCases.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                openGraphDrawingFragment(ConstantToolkit.DAILY_ACTIVE_CASES_FRAGMENT);
                return true;
            }
        });
    }

    private void openGraphDrawingFragment(String tag)
    {
        FragmentManager fragmentManager = getParentFragmentManager();
        GraphDrawer graphFragment = new GraphDrawer();
        graphFragment.show(fragmentManager, tag);


    }


}
