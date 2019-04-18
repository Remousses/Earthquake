package com.example.earthquakeandroidapp.activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.earthquakeandroidapp.beans.EarthquakeBean;
import com.example.earthquakeandroidapp.interfaces.VolleyCallback;
import com.example.earthquakeandroidapp.R;
import com.example.earthquakeandroidapp.adapters.RecyclerViewAdapter;
import com.example.earthquakeandroidapp.api.EarthquakeApi;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static EditText editStartDate;
    private static EditText editEndDate;

    private EarthquakeApi earthquakeApi;
    private List<EarthquakeBean> earthquakeList = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private Boolean net = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        earthquakeApi = new EarthquakeApi(MainActivity.this);

        editStartDate = (EditText) findViewById(R.id.editStartDate);
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePickerDialog(v);
            }
        });

        editEndDate = (EditText) findViewById(R.id.editEndDate);
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDatePickerDialog(v);
            }
        });

        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);*/
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (!net) {
            earthquakeApi.readFromJson(callVolleyCallback(), null, null);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Callback pour récupérer la liste des oeuvres d'un artiste
     * @return le callback
     */
    private VolleyCallback callVolleyCallback(){
            System.out.println("Récupération de la liste des tremblements de terre");
            return new VolleyCallback() {
                @Override
                public void onSuccess(final JSONObject response) {
                    setLoaderVisibility();
                    earthquakeApi.getEarthquake(earthquakeList, response);

                    net = true;
                    setLoaderGone();
                }
            };
    }

    public void setLoaderGone(){
        ProgressBar loader = (ProgressBar) findViewById(R.id.loader);
        loader.setVisibility(View.GONE);
    }

    public void setLoaderVisibility(){
        ProgressBar loader = (ProgressBar) findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
    }

    public void setUpdateRecyclerView(List<EarthquakeBean> earthquakeList) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this, earthquakeList);
        recyclerView.setAdapter(myAdapter);
    }

    private void showStartDatePickerDialog(View v) {
        editStartDate = (EditText) findViewById(R.id.editStartDate);
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert, mDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker dataPicker, int year, int month, int day) {
                editStartDate.setText(day + "/" + (month + 1) + "/" + year);
            }
        };
    }

    private void showEndDatePickerDialog(View v) {
        editEndDate = (EditText) findViewById(R.id.editEndDate);
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert, mDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker dataPicker, int year, int month, int day) {
                editEndDate.setText(day + "/" + (month + 1) + "/" + year);
                earthquakeApi.readFromJson(callVolleyCallback(), editStartDate.getText().toString(), editEndDate.getText().toString());
            }
        };
    }
}