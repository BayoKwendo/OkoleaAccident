package com.okolea.police.activities;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.okolea.R;
import com.okolea.police.models.PlaceList;
import com.okolea.police.models.SinglePlace;
import com.okolea.police.recycler.HospitalListRecycler;
import com.okolea.police.rest_api.GooglePlacesApi;
import com.okolea.police.rest_api.HospitalListClient;
import com.wang.avi.AVLoadingIndicatorView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.okolea.hospital.utils.LoadingUtil.enableDisableView;


public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerHospital;
    ArrayList<SinglePlace> itemList;
    FrameLayout fader, listFrame;
    AVLoadingIndicatorView avi;
    TextView tvDisplayResult, NoResult;

    GooglePlacesApi googlePlacesApi;
    HospitalListClient hospitalListClient;

    public static final String TAG = "list";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        recyclerHospital = (RecyclerView) findViewById(R.id.recyclerHospital);
        recyclerHospital.setLayoutManager(new LinearLayoutManager(this));

        fader = (FrameLayout) findViewById(R.id.fader);
        listFrame = (FrameLayout) findViewById(R.id.content_main);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        tvDisplayResult = findViewById(R.id.tvDisplayResult);
        NoResult = findViewById(R.id.NoResult);
        NoResult.setVisibility(View.INVISIBLE);


        stopLoadingAnimation();
        tvDisplayResult.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
//            Log.d(TAG, "onCreate: search started");

            setLoadingAnimation();
            String query = intent.getStringExtra(SearchManager.QUERY);

            toolbar.setTitle("Search results for '"+query+"'");

            googlePlacesApi = new GooglePlacesApi(getApplicationContext());
            hospitalListClient = googlePlacesApi.getHospitalListClient();

            HashMap<String, String > params = googlePlacesApi.getQueryParams(MainActivity.curLocation, GooglePlacesApi.TYPE_HOSPITAL, GooglePlacesApi.RANKBY_PROMINENCE);
            params.put("radius","50000");
            params.put("name", query);

            hospitalListClient.getNearbyHospitals(params).enqueue(new Callback<PlaceList>() {
                @Override
                public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
//                    Log.d(TAG, "onResponse: resp received");
                    PlaceList placeList = response.body();

                    if(placeList != null){
                        stopLoadingAnimation();
                        itemList = placeList.places;
                        if(itemList.size() == 0)
                            tvDisplayResult.setVisibility(View.VISIBLE);
                        else
                            bindRecyclerView();

                    }

                }

                @Override
                public void onFailure(Call<PlaceList> call, Throwable t) {
//                    Log.d(TAG, "onFailure: cannot access places api");
                    Toast.makeText(getApplicationContext(),"Unable to access server. Please try again later",Toast.LENGTH_SHORT).show();
                    tvDisplayResult.setVisibility(View.VISIBLE);
                }
            });
        }
        else {
            itemList = Parcels.unwrap(intent.getParcelableExtra("itemList"));
            if(itemList.size() == 0)
                NoResult.setVisibility(View.VISIBLE);
            else
                bindRecyclerView();

        }

    }




    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    void bindRecyclerView(){
        HospitalListRecycler hospitalListRecycler = new HospitalListRecycler(itemList,this);
        recyclerHospital.setAdapter(hospitalListRecycler);
    }

    void setLoadingAnimation(){
        enableDisableView(listFrame, false);
        tvDisplayResult.setVisibility(View.INVISIBLE);
        fader.setVisibility(View.VISIBLE);
        avi.show();
    }

    void stopLoadingAnimation(){
        enableDisableView(listFrame, true);
        fader.setVisibility(View.GONE);
        avi.hide();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main2, menu);
        SearchManager search = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView view = (SearchView) menu.findItem(R.id.action_share).getActionView();
        view.setLayoutParams(new ActionBar.LayoutParams(Gravity.RIGHT));
        assert search != null;
        view.setSearchableInfo(search.getSearchableInfo(getComponentName()));


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
