package com.example.pharmagoenduser.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmagoenduser.Adapter.MyCartItemListAdapter;
import com.example.pharmagoenduser.Adapter.ViewMyOrderListAdapter;
import com.example.pharmagoenduser.Model.CartModel;
import com.example.pharmagoenduser.Model.MyOrderItemsModel;
import com.example.pharmagoenduser.Model.MyOrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.R;
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

public class ViewOrderActivity extends AppCompatActivity {
    private ArrayList<MyOrderItemsModel> mMyOrderItemsModel;

    Intent intent;
    String id = "";
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_orderList;
    ProgressDialog progressDialog;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    TextView tv_total,tv_payment_method,tv_pharma_label,tv_subtotal,tv_vat;;
    CardView cv_total;
    ImageView iv_empty;
    ConstraintLayout parent_layout;
    int total= 0;
    int codTotal= 0;
    double vat = 0.0;
    private static final String TAG = "ViewOrderActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        intent = getIntent();
        id = intent.getStringExtra("order_id");
        rv_orderList = findViewById(R.id.rv_orderList);
        tv_total = findViewById(R.id.tv_total);
        cv_total = findViewById(R.id.cv_total);
        tv_payment_method = findViewById(R.id.tv_payment_method);
        tv_pharma_label = findViewById(R.id.tv_pharma_label);

        tv_subtotal = findViewById(R.id.tv_subtotal);
        tv_vat = findViewById(R.id.tv_vat);

        iv_empty = findViewById(R.id.iv_empty);
        parent_layout = findViewById(R.id.parent_layout);
        user = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(ViewOrderActivity.this);
        progressDialog.setTitle("Pharma Go");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        mMyOrderItemsModel = new ArrayList<>();

        rv_orderList.setHasFixedSize(true);
        rv_orderList.setLayoutManager(new LinearLayoutManager(ViewOrderActivity.this));

        db.collection(getString(R.string.COLLECTION_MY_ORDERLIST))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable  QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){

                            if(document.getId().equals(id)){
                                MyOrderModel myOrderModel = document.toObject(MyOrderModel.class);

                                db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            if(document.getId().equals(myOrderModel.getPharmacy_id())){
                                                                PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                                                tv_pharma_label.setText(pharmacyModel.getPharmacy_name());
                                                            }
                                                        }


                                                }


                                            }
                                        });
                                Log.d(TAG, "payment: " + myOrderModel.getPayment_method());
                                if(myOrderModel.getPayment_method().equals("cod")){
                                    tv_payment_method.setText(myOrderModel.getPayment_method().toUpperCase());
                                }else{
                                    tv_payment_method.setText("Debit/Credit Card");
                                }

                                Log.d(TAG, "onEvent: " + myOrderModel.getPayment_method());
                            }



                        }

                    }
                });
        db.collection(getString(R.string.COLLECTION_MY_ORDERLIST_ITEMS))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mMyOrderItemsModel.clear();
                        total= 0;
                        codTotal= 0;
                        Log.d(TAG, "onEvent: pasok sa db");
                        if(mMyOrderItemsModel.size() == 0){
                            cv_total.setVisibility(View.GONE);
                        }else {
                            cv_total.setVisibility(View.VISIBLE);
                        }

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            MyOrderItemsModel orderItems = document.toObject(MyOrderItemsModel.class);



                            Log.d(TAG, "onEvent: " + document.getId());
//
                            if(id.equals(orderItems.getMyOrder_id())){
                                mMyOrderItemsModel.add(orderItems);
                                codTotal += Integer.parseInt(orderItems.getMedecine_price());
                                tv_total.setText("₱" + (codTotal+100));
                                tv_subtotal.setText("₱" + codTotal);
                                Log.d(TAG, "onEvent: " + codTotal);

                            }
                            DecimalFormat dec = new DecimalFormat("#0.00");
                            vat = (codTotal*.12);
                            tv_vat.setText("₱" + dec.format(vat));
                            tv_total.setText("₱" + dec.format(codTotal+100+vat));
                            Log.d(TAG, "vat: " + vat);

                            if(mMyOrderItemsModel.size() == 0){
                                cv_total.setVisibility(View.GONE);
                            }else {
                                cv_total.setVisibility(View.VISIBLE);
                            }

                        }


                        if(mMyOrderItemsModel.size() == 0){
                            iv_empty.setVisibility(View.VISIBLE);
                            rv_orderList.setVisibility(View.GONE);
                            cv_total.setVisibility(View.GONE);
                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                        }else {
                            iv_empty.setVisibility(View.GONE);
                            rv_orderList.setVisibility(View.VISIBLE);
                            cv_total.setVisibility(View.VISIBLE);
                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }

                        ViewMyOrderListAdapter viewMyOrderListAdapter = new ViewMyOrderListAdapter( ViewOrderActivity.this, mMyOrderItemsModel);
                        rv_orderList.setAdapter(viewMyOrderListAdapter);


                    }
                });

    }
}