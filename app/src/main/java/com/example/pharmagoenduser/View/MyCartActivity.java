package com.example.pharmagoenduser.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmagoenduser.Adapter.MyCartItemListAdapter;
import com.example.pharmagoenduser.Adapter.MyOrderListAdapter;
import com.example.pharmagoenduser.Model.CartModel;
import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.Dialog.ProceedToCheckOutDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyCartActivity extends AppCompatActivity {
    private ArrayList<CartModel> mCartModel;

    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_orderList;
    ProgressDialog progressDialog;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    TextView tv_total,tv_pharma_label,tv_subtotal,tv_vat;
    CardView cv_total,cv_proceed,cv_label;
    int total= 0;
    int codTotal= 0;
    double vat = 0.0;

    Button btn_proceedToCheckout;

    ImageView iv_empty;
    ConstraintLayout parent_layout;
    private static final String TAG = "MyCartActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        rv_orderList = findViewById(R.id.rv_orderList);

        tv_total = findViewById(R.id.tv_total);
        cv_total = findViewById(R.id.cv_total);
        cv_proceed = findViewById(R.id.cv_proceed);
        btn_proceedToCheckout = findViewById(R.id.btn_proceedToCheckout);
        cv_label = findViewById(R.id.cv_label);
        tv_pharma_label = findViewById(R.id.tv_pharma_label);
        tv_subtotal = findViewById(R.id.tv_subtotal);
        tv_vat = findViewById(R.id.tv_vat);


        rv_orderList.setHasFixedSize(true);
        rv_orderList.setLayoutManager(new LinearLayoutManager(MyCartActivity.this));

        iv_empty = findViewById(R.id.iv_empty);
        parent_layout = findViewById(R.id.parent_layout);
        user = FirebaseAuth.getInstance().getCurrentUser();

        mCartModel = new ArrayList<>();


        progressDialog = new ProgressDialog(MyCartActivity.this);
        progressDialog.setTitle("Pharma Go");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        db.collection(getString(R.string.COLLECTION_CART))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mCartModel.clear();
                        total= 0;
                        codTotal= 0;
                        Log.d(TAG, "onEvent: pasok sa db");
                        if(mCartModel.size() == 0){
                            cv_total.setVisibility(View.GONE);
                        }else {
                            cv_total.setVisibility(View.VISIBLE);
                        }

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            CartModel cartModel = document.toObject(CartModel.class);

                            cartModel.setCart_id(document.getId());

                            Log.d(TAG, "onEvent: " + document.getId());
//
                            if(firebaseUser.getUid().equals(cartModel.getUser_id())){


                                mCartModel.add(cartModel);
                                codTotal += Integer.parseInt(cartModel.getMedecine_price());



                                tv_subtotal.setText("₱" + codTotal);
                                Log.d(TAG, "onEvent: " + codTotal);

                            }
                            DecimalFormat dec = new DecimalFormat("#0.00");
                            vat = (codTotal*.12);
                            tv_vat.setText("₱" + dec.format(vat));
                            tv_total.setText("₱" + dec.format(codTotal+100+vat));
                            Log.d(TAG, "vat: " + vat);

                            if(mCartModel.size() == 0){
                                cv_total.setVisibility(View.GONE);
                            }else {
                                cv_total.setVisibility(View.VISIBLE);
                            }

                        }


                        if(mCartModel.size() == 0){
                            iv_empty.setVisibility(View.VISIBLE);
                            rv_orderList.setVisibility(View.GONE);
                            cv_total.setVisibility(View.GONE);
                            cv_proceed.setVisibility(View.GONE);
                            btn_proceedToCheckout.setVisibility(View.GONE);
                            cv_label.setVisibility(View.GONE);
                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                        }else {
                            iv_empty.setVisibility(View.GONE);
                            rv_orderList.setVisibility(View.VISIBLE);
                            cv_total.setVisibility(View.VISIBLE);
                            cv_proceed.setVisibility(View.VISIBLE);
                            btn_proceedToCheckout.setVisibility(View.VISIBLE);
                            cv_label.setVisibility(View.VISIBLE);

                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }

                        if (mCartModel != null){
                            db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()) {

                                                if (mCartModel.size()>0){
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if(document.getId().equals(mCartModel.get(0).getPharmacy_id())){

                                                            PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                                            tv_pharma_label.setText(pharmacyModel.getPharmacy_name());



                                                        }
                                                    }
                                                }

                                            }


                                        }
                                    });
                        }

                        MyCartItemListAdapter myCartItemListAdapter = new MyCartItemListAdapter(MyCartActivity.this, mCartModel);
                        rv_orderList.setAdapter(myCartItemListAdapter);


                    }
                });

        btn_proceedToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProceedToCheckOutDialog dialog = new ProceedToCheckOutDialog(mCartModel);
                dialog.show(getSupportFragmentManager(), "PharmaGo");
            }
        });
    }
}