package com.example.pharmagoenduser.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pharmagoenduser.R;

public class TermsAndPrivacyLandingPage extends AppCompatActivity {

    TextView tv_dp,tv_tnc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_privacy_landing_page);

        tv_dp = findViewById(R.id.tv_dp);
        tv_tnc = findViewById(R.id.tv_tnc);

        tv_tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TermsAndPrivacyLandingPage.this,TermsAndConditionActivity.class));
            }
        });
        tv_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TermsAndPrivacyLandingPage.this,TermsAndPrivacyActivity.class));
            }
        });
    }
}