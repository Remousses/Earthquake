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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class EarthquakeApi {
    private static String EARTQUAKE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson";

    private final  MainActivity mainActivity;

    public EarthquakeApi(final MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    /**
     * Lit une url qui renvoie un format JSON.
     * Si artisteName renseigné, lit l'url pour un artiste sinon pour toute la collection.
     * @param callback
     * @return la liste des oeuvres pour un artiste provenant de l'url.
     */
    public void readFromJson(final VolleyCallback callback) {
        LocalDateTime currentTime = LocalDateTime.now();

        int soustractOneMounth = currentTime.getMonth().getValue() - 1;

        String lastMounth = currentTime.getYear() + "-" + soustractOneMounth + "-" +  currentTime.getDayOfMonth();

        //EARTQUAKE_URL += "&starttime=" + lastMounth + "&endtime=" + currentTime.toLocalDate();

        EARTQUAKE_URL += "&starttime=2019-02-02&endtime=2019-02-15";
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, EARTQUAKE_URL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            callback.onSuccess(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Erreur lors de la récupération de l'url : " + error);
                            mainActivity.setLoader();

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

                            //Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            //applicationContext.startActivity(intent);

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
    public List<EarthquakeBean> getLastThirtyDaysEarthquake(List<EarthquakeBean> earthquakeList, JSONObject response){
        String id = null;
        String place = null;
        BigInteger time = null;
        Double magnetude = null;
        String urlMap = null;

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

            mainActivity.setUpdateRecyclerView(earthquakeList);
        } catch (JSONException e) {
            System.out.println("Erreur lors du parsing JSON de features : " + e);
        }

        return earthquakeList;
    }

    /**
     * Récupère la liste des artistes.
     * @param artWorkList
     * @param response
     * @return la liste des artistes
     */
    public List<EarthquakeBean> getArtists(List<EarthquakeBean> artWorkList, JSONObject response){
        try {
            if(response.getInt("count") != 0){
                JSONArray firstFacets = response.getJSONArray("facets");

                for(int i = 0; i < firstFacets.length(); i++) {
                    JSONObject facets = firstFacets.getJSONObject(i);

                    if(facets.getString("name").equals("principalMaker")) {
                        JSONArray facetsList = facets.getJSONArray("facets");
                        System.out.println("*** Nombres d'artistes dans la liste getArtists : " + facetsList.length() + " ***");

                        for(int j = 0, b = 0; j < facetsList.length(); j++) {
                            JSONObject art = facetsList.getJSONObject(j);
                            EarthquakeBean artWork = new EarthquakeBean();

                            /*artWork.setAuthor(art.getString("key"));
                            artWork.setArtWorkNumber(art.getInt("value"));
                            artWork.setIndex(b++);*/

                            artWorkList.add(artWork);
                        }
                    }
                }

                mainActivity.setUpdateRecyclerView(artWorkList);
            } else {
                System.out.println(response.getInt("count"));
                System.out.println("Pas d'image");
            }
        } catch (JSONException e) {
            System.out.println("Erreur lors du parsing JSON : " + e);
        }

        return artWorkList;
    }
}