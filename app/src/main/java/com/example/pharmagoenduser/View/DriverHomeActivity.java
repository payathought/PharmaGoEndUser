package com.example.pharmagoenduser.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmagoenduser.FunctionAndMethod.FunctionMethod;
import com.example.pharmagoenduser.MainActivity;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.Dialog.UpdateUserInfoDialog;
import com.example.pharmagoenduser.View.Dialog.ViewUserInfoInToolBarDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class DriverHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavDriver;

    Toolbar toolbar;
    TextView txtUserToolbar;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    FunctionMethod functionMethod = new FunctionMethod();

    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;

    Button btn_cart;
    ImageView iv_cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        bottomNavDriver = findViewById(R.id.bottomNavDriver);

        toolbar  = findViewById(R.id.toolBar);
        btn_cart  = findViewById(R.id.btn_cart);
        iv_cart  = findViewById(R.id.iv_cart);
        txtUserToolbar  = findViewById(R.id.txtUserToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        sharedpreferences = getSharedPreferences(getString(R.string.USERPREF), Context.MODE_PRIVATE);

        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            txtUserToolbar.setText(sharedpreferences.getAll().get(getString(R.string.USERNAME)).toString());
        }

        btn_cart.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);

        txtUserToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUserInfoInToolBarDialog dialog = new ViewUserInfoInToolBarDialog(firebaseUser.getUid());
                dialog.show(getSupportFragmentManager(), "PharmaGo");
            }
        });

        bottomNavDriver.setOnNavigationItemSelectedListener(navlistener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragmentContainerDriver, new DriverMedicineListFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_availableMedecineList:
                            selectedFragment = new DriverMedicineListFragment();
                            break;
                        case R.id.nav_history:
                            selectedFragment = new HistoryOrderLogsFragment();
                            break;

                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_fragmentContainerDriver, selectedFragment).commit();
                    return true;
                }

            };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            Log.d("Logout", "logout: " + editor);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toasty.info(getApplicationContext(), "Logging Out", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.tnp){
            startActivity(new Intent(this, TermsAndPrivacyLandingPage.class));
        }

        else{
            UpdateUserInfoDialog dialog = new UpdateUserInfoDialog(firebaseUser.getUid(),this);
            dialog.show(getSupportFragmentManager(), "PharmaGo");
        }
        return false;
    }

    public void onBackPressed() {
        super.onBackPressed();

        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        else if (!sharedpreferences.getAll().isEmpty())
        {
            if(sharedpreferences.getAll().get(getString(R.string.USER_TYPE)).toString().equals("Customer")){
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));

            }else {
                startActivity(new Intent(getApplicationContext(),DriverHomeActivity.class));
            }
        }
        moveTaskToBack(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        functionMethod.sendSMSPermission(getApplicationContext());
        functionMethod.callPermission(getApplicationContext());
        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            txtUserToolbar.setText(sharedpreferences.getAll().get(getString(R.string.USERNAME)).toString());
        }
    }
}