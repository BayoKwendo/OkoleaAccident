package com.okolea.MInsurance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.okolea.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insurance);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        Toast.makeText(this, "This page will be available soon!!", Toast.LENGTH_SHORT).show();
        tool.setText("Medical Insurance");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        GridLayout mainGrid = (GridLayout) findViewById(R.id.main_grid);
        int childCount = mainGrid.getChildCount();

        gridClickEvent(mainGrid);
    }

    private void gridClickEvent(GridLayout mainGrid) {
        for (int i=0; i<mainGrid.getChildCount(); i++){
            CardView mCardView  = (CardView) mainGrid.getChildAt(i);
            final int index = i;
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(index){
                        case 0:
                            Intent openHospitalActivity = new Intent(MainActivity.this, Hospitals.class);
                            startActivity(openHospitalActivity);
                        break;

                        case 1:
                            Intent openServicesActivity = new Intent(MainActivity.this, Services.class);
                            startActivity(openServicesActivity);

                            break;

                        case 2:
                            Intent openApplyActivity = new Intent(MainActivity.this, Apply.class);
                            startActivity(openApplyActivity);
                            break;

                        case 3:
                            Intent openClaimsActivity = new Intent(MainActivity.this, Claims.class);
                            startActivity(openClaimsActivity);
                            break;

                        case 4:
                            Intent openMohActivity = new Intent(MainActivity.this, moh.class);
                            startActivity(openMohActivity);
                            break;

                        case 5:
                            Intent openAboutActivity = new Intent(MainActivity.this, about.class);
                            startActivity(openAboutActivity);
                            break;
                    }
                }
            });

        }
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
