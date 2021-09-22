package com.example.pharmagoenduser.View;

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
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.Dialog.ProceedToCheckOutDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyCartActivity extends AppCompatActivity {
    private ArrayList<CartModel> mCartModel;

    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_orderList;
    ProgressDialog progressDialog;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    TextView tv_total;
    CardView cv_total,cv_proceed;
    int total= 0;
    int codTotal= 0;

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
                                tv_total.setText("₱" + (codTotal+100));

                                Log.d(TAG, "onEvent: " + codTotal);

                            }

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
                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                        }else {
                            iv_empty.setVisibility(View.GONE);
                            rv_orderList.setVisibility(View.VISIBLE);
                            cv_total.setVisibility(View.VISIBLE);
                            cv_proceed.setVisibility(View.VISIBLE);
                            btn_proceedToCheckout.setVisibility(View.VISIBLE);

                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
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