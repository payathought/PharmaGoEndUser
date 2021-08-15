package com.example.pharmagoenduser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.View.ForgotPasswordDialog;
import com.example.pharmagoenduser.View.HomeActivity;
import com.example.pharmagoenduser.View.ProceedToOrderDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import es.dmoral.toasty.Toasty;

public class OrderMedicineActivity extends AppCompatActivity {
    private static final String TAG = "OrderMedicineActivity";

    Button btn_minus, btn_plus,btn_addOrder;
    TextView tv_medicineName,tv_medicinePrice,tv_quantity,txtUserToolbar,tv_pharma;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    Intent intent;
    String id = "";
    int quantity = 1;
    int price = 0;
    int total = 0;
    String pharmacy_id = "";
    Toolbar toolbar;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    FirebaseUser fUser =  FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_medicine);
        intent = getIntent();
        id = intent.getStringExtra("medicine_id");

        btn_minus = findViewById(R.id.btn_minus);
        btn_plus = findViewById(R.id.btn_plus);
        btn_addOrder = findViewById(R.id.btn_addOrder);
        tv_medicineName = findViewById(R.id.tv_medicineName);
        tv_medicinePrice = findViewById(R.id.tv_medicinePrice);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_pharma = findViewById(R.id.tv_pharma);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        toolbar  = findViewById(R.id.toolBar);
        txtUserToolbar  = findViewById(R.id.txtUserToolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        sharedpreferences = getSharedPreferences(getString(R.string.USERPREF), Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        Log.d(TAG, "onCreate: ");

        Log.d(TAG, "onCreate: + shared pref " + sharedpreferences.getAll().get(getString(R.string.FIRSTNAME)));
        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            txtUserToolbar.setText(sharedpreferences.getAll().get(getString(R.string.USERNAME)).toString());
        }


        if(!id.equals("")){

            progressDialog.show();
            db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals(id)){

                                        OrderModel med = document.toObject(OrderModel.class);
                                        pharmacy_id = med.getPharmacy_id();
                                        price = Integer.parseInt(med.getMedecine_price());
                                        total = (price*quantity);
                                        tv_medicineName.setText(med.getMedecine_name());
                                        tv_medicinePrice.setText("₱" + total);
                                        tv_quantity.setText(String.valueOf(quantity));

                                        db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()) {

                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                if(document.getId().equals(pharmacy_id)){

                                                                    PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                                                    tv_pharma.setText(pharmacyModel.getPharmacy_name());


                                                                }
                                                            }
                                                        }
                                                        progressDialog.dismiss();

                                                    }
                                                });

                                    }
                                }
                            }
                            progressDialog.dismiss();

                        }
                    });
        }
        tv_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(quantity == 1){
                    Drawable d = getResources().getDrawable(R.drawable.shape_button_order_disable);
                    btn_minus.setBackground(d);
                    btn_minus.setEnabled(false);

                }else {
                    Drawable d = getResources().getDrawable(R.drawable.shape_button_order);
                    btn_minus.setBackground(d);
                    btn_minus.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(quantity == 1){
                    Drawable d = getResources().getDrawable(R.drawable.shape_button_order_disable);
                    btn_minus.setBackground(d);
                    btn_minus.setEnabled(false);

                }else {
                    Drawable d = getResources().getDrawable(R.drawable.shape_button_order);
                    btn_minus.setBackground(d);
                    btn_minus.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(quantity == 1){
                    Drawable d = getResources().getDrawable(R.drawable.shape_button_order_disable);
                    btn_minus.setBackground(d);
                    btn_minus.setEnabled(false);

                }else {
                    Drawable d = getResources().getDrawable(R.drawable.shape_button_order);
                    btn_minus.setBackground(d);
                    btn_minus.setEnabled(true);
                }
            }
        });

        if(quantity == 1){
            Drawable d = getResources().getDrawable(R.drawable.shape_button_order_disable);
            btn_minus.setBackground(d);
            btn_minus.setEnabled(false);

        }else if(quantity >= 1){
            Drawable d = getResources().getDrawable(R.drawable.shape_button_order);
            btn_minus.setBackground(d);
            btn_minus.setEnabled(true);
        }


        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                total = (price*quantity);
                tv_medicinePrice.setText("₱" + total);
                tv_quantity.setText(String.valueOf(quantity));
                Log.d(TAG, "onClick: " + quantity);

            }
        });
        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                quantity--;
                total = (price*quantity);
                tv_medicinePrice.setText("₱" + total);
                tv_quantity.setText(String.valueOf(quantity));

            }
        });
        btn_addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderModel orderModel = new OrderModel();
                orderModel.setMedecine_name(tv_medicineName.getText().toString().trim());
                orderModel.setMedecine_price(String.valueOf(total));
                orderModel.setUser_id(fUser.getUid());
                orderModel.setStatus("pending");
                orderModel.setMedicine_id(id);
                orderModel.setDriver_status("pending");
                orderModel.setDriver_id("");
                orderModel.setPharmacy_id(pharmacy_id);
                orderModel.setQuantity(String.valueOf(quantity));
                ProceedToOrderDialog dialog = new ProceedToOrderDialog(orderModel);
                dialog.show(getSupportFragmentManager(), "PharmaGo");

//                AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderMedicineActivity.this);
//                builder1.setMessage("Is your order finalized?");
//                builder1.setCancelable(true);
//
//                builder1.setPositiveButton(
//                        "Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int Dialog_id) {
//
//                                OrderModel orderModel = new OrderModel();
//                                orderModel.setMedecine_name(tv_medicineName.getText().toString().trim());
//                                orderModel.setMedecine_price(String.valueOf(total));
//                                orderModel.setUser_id(fUser.getUid());
//                                orderModel.setStatus("pending");
//                                orderModel.setMedicine_id(id);
//                                orderModel.setDriver_status("pending");
//                                orderModel.setDriver_id("");
//
//                                progressDialog.dismiss();
////                                db.collection(getString(R.string.COLLECTION_ORDERLIST))
////                                        .add(orderModel)
////                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
////                                            @Override
////                                            public void onComplete(@NonNull Task<DocumentReference> task) {
////                                                if (task.isSuccessful()) {
////                                                    Log.d("TAG", "onComplete: " + task.getResult().getId());
////                                                    progressDialog.dismiss();
////                                                    Toasty.success(OrderMedicineActivity.this,
////                                                            "Ordered Successfully.", Toast.LENGTH_LONG)
////                                                            .show();
////                                                    startActivity(new Intent(OrderMedicineActivity.this, HomeActivity.class));
////
////                                                }
////                                            }
////                                        }).addOnFailureListener(new OnFailureListener() {
////                                    @Override
////                                    public void onFailure(@NonNull Exception e) {
////                                        progressDialog.dismiss();
////                                        Log.d("Exception", "onFailure: " + e);
////                                    }
////                                });
//
//                                dialog.cancel();
//                            }
//                        });
//
//                builder1.setNegativeButton(
//                        "No",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//                AlertDialog alert11 = builder1.create();
//                alert11.show();

            }
        });

    }


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
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            txtUserToolbar.setText(sharedpreferences.getAll().get(getString(R.string.USERNAME)).toString());
        }
    }
}