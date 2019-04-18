package com.example.earthquakeandroidapp.api;

import android.app.Dialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquakeandroidapp.interfaces.VolleyCallback;
import com.example.earthquakeandroidapp.R;
import com.example.earthquakeandroidapp.activities.MainActivity;
import com.example.earthquakeandroidapp.beans.EarthquakeBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

public class EarthquakeApi {
    private final  MainActivity mainActivity;

    public EarthquakeApi(final MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    /**
     * Lit une url qui renvoie un format JSON.
     * Si artisteName renseigné, lit l'url pour un artiste sinon pour toute la collection.
     * @param callback
     * @param startDate
     * @param endDate
     * @return la liste des oeuvres pour un artiste provenant de l'url.
     */
    public void readFromJson(final VolleyCallback callback, String startDate, String endDate) {
        String earthquake_ulr = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson";
        LocalDateTime currentTime = LocalDateTime.now();

        int soustractOneMounth = currentTime.getMonth().getValue() - 1;

        String lastMounth = currentTime.getYear() + "-" + soustractOneMounth + "-" +  currentTime.getDayOfMonth();

        if(startDate == null && endDate == null){
            System.out.println("if");
            earthquake_ulr += "&starttime=" + lastMounth + "&endtime=" + currentTime.toLocalDate();
        } else {

            System.out.println("startDate : " + startDate);

            System.out.println("endDate : " + endDate);
            System.out.println("else");

            // go faire un split XD

            String[] splitStartDate = startDate.split("/");

            startDate = splitStartDate[2] + "-" + splitStartDate[1] + "-" + splitStartDate[0];

            String[] splitEndDate = endDate.split("/");

            endDate = splitEndDate[2] + "-" + splitEndDate[1] + "-" + splitEndDate[0];

            earthquake_ulr += "&starttime=" + startDate + "&endtime=" + endDate;
        }

        System.out.println("earthquake_ulr : " + earthquake_ulr);
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, earthquake_ulr, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            callback.onSuccess(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Erreur lors de la récupération de l'url : " + error);
                            mainActivity.setLoaderGone();

                            final Dialog dialog = new Dialog(mainActivity);
                            dialog.setContentView(R.layout.network_error);

                            Toolbar artistDialToolbar = dialog.findViewById(R.id.network_error_dial_toolbar);
                            artistDialToolbar.setTitle("Activer le réseau");

                            Button artistDialBtnYes = dialog.findViewById(R.id.network_error_dial_btn_yes);
                            artistDialBtnYes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                    mainActivity.startActivity(intent);
                                }
                            });

                            Button artistDialBtnNo = dialog.findViewById(R.id.network_error_dial_btn_no);
                            artistDialBtnNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();

                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            } else if (error instanceof AuthFailureError) {
                                //TODO
                            } else if (error instanceof ServerError) {
                                //TODO
                            } else if (error instanceof NetworkError) {
                                //TODO
                            } else if (error instanceof ParseError) {
                                //TODO
                            }
                        }
                    });

            Volley.newRequestQueue(mainActivity).add(jsonObjectRequest);
            // TODO: Faire un  callback correct
            //Thread.sleep(2000);
        }catch(Exception e){
            System.out.println("Erreur : " + e);
        }
    }

    /**
     * Récupère la liste des tremblement de terre.
     * @param earthquakeList
     * @param response
     * @return la liste des tremblement de terre pour les 30 derniers jours
     */
    public List<EarthquakeBean> getEarthquake(List<EarthquakeBean> earthquakeList, JSONObject response){
        String id = null;
        String place = null;
        BigInteger time = null;
        Double magnetude = null;
        String urlMap = null;

        earthquakeList.clear();

        try {
            JSONArray features = response.getJSONArray("features");
            System.out.println("*** Nombres de tremblements de terre selectionné pour l'interval de temps selectionné : " + features.length() + " ***");

            for(int j = 0; j < features.length(); j++) {
                try {
                    JSONObject feature = features.getJSONObject(j);

                    id = feature.getString("id");
                    JSONObject properties = feature.getJSONObject("properties");

                    place = properties.getString("place");
                    time = new BigInteger(properties.getString("time"));
                    urlMap = properties.getString("url") + "/map";
                    magnetude = properties.getDouble("mag");
                } catch (JSONException e) {
                    System.out.println("Erreur lors du parsing JSON dans features : " + e);
                }

                EarthquakeBean earthquakeBean = new EarthquakeBean(id, place, urlMap, magnetude, time);
                earthquakeList.add(earthquakeBean);
            }

            System.out.println("pro : " + earthquakeList);
            mainActivity.setUpdateRecyclerView(earthquakeList);
        } catch (JSONException e) {
            System.out.println("Erreur lors du parsing JSON de features : " + e);
        }

        return earthquakeList;
    }
}