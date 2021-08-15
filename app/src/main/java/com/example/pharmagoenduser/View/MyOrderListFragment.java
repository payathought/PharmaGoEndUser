package com.example.pharmagoenduser.View;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmagoenduser.Adapter.MedicineListAdapter;
import com.example.pharmagoenduser.Adapter.MyOrderListAdapter;
import com.example.pharmagoenduser.Model.MedicineModel;
import com.example.pharmagoenduser.Model.OrderModel;
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

import java.util.ArrayList;


public class MyOrderListFragment extends Fragment {
    private View view;
    private ArrayList<OrderModel> mOrderModel;

    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_orderList;
    ProgressDialog progressDialog;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    TextView tv_total,tv_codTotal;
    CardView cv_total;
    int total= 0;
    int codTotal= 0;

    ImageView iv_empty;
    ConstraintLayout parent_layout;

    private static final String TAG = "MyOrderListFragment";
    public MyOrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_order_list, container, false);
        rv_orderList = view.findViewById(R.id.rv_orderList);
        tv_codTotal = view.findViewById(R.id.tv_codTotal);
        tv_total = view.findViewById(R.id.tv_total);
        cv_total = view.findViewById(R.id.cv_total);


        rv_orderList.setHasFixedSize(true);
        rv_orderList.setLayoutManager(new LinearLayoutManager(getContext()));

        iv_empty = view.findViewById(R.id.iv_empty);
        parent_layout = view.findViewById(R.id.parent_layout);
        user = FirebaseAuth.getInstance().getCurrentUser();

        mOrderModel = new ArrayList<>();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

//        db.collection(getString(R.string.COLLECTION_ORDERLIST))
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                        if(task.isSuccessful()){
//                            mOrderModel.clear();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.get("medecine_name") + " => " + document.get("medecine_price"));
//
//                                OrderModel orderModel = new OrderModel();
//                                orderModel.setMedicine_id(document.getId());
//                                orderModel.setMedecine_name(document.get("medecine_name").toString());
//                                orderModel.setMedecine_price(document.get("medecine_price").toString());
//                                orderModel.setStatus(document.get("status").toString());
//
//                                mOrderModel.add(orderModel);
//
//                                Log.d(TAG, "onComplete: " + orderModel.getMedecine_name());
//
//                            }
//                            MyOrderListAdapter myOrderListAdapter = new MyOrderListAdapter(getContext(), mOrderModel);
//                            rv_orderList.setAdapter(myOrderListAdapter);
//
//                        }
//                    }
//                });

        db.collection(getString(R.string.COLLECTION_ORDERLIST))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mOrderModel.clear();
                        total= 0;
                        codTotal= 0;
                        Log.d(TAG, "onEvent: pasok sa db");
                        if(mOrderModel.size() == 0){
                            cv_total.setVisibility(View.GONE);
                        }else {
                            cv_total.setVisibility(View.VISIBLE);
                        }

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            OrderModel orderModel = document.toObject(OrderModel.class);
//                            orderModel.setMedicine_id(document.getId());
//                            orderModel.setMedecine_name(document.get("medecine_name").toString());
//                            orderModel.setMedecine_price(document.get("medecine_price").toString());
//                            orderModel.setStatus(document.get("status").toString());
//                            orderModel.setDriver_status(document.get("driver_status").toString());
//                            orderModel.setUser_id(document.get("user_id").toString());
                            if(firebaseUser.getUid().equals(orderModel.getUser_id()) && !(orderModel.getStatus().equals("done"))){
                                mOrderModel.add(orderModel);
                                codTotal += Integer.parseInt(orderModel.getMedecine_price());
                                tv_total.setText("₱" + (codTotal+100));

                                Log.d(TAG, "onEvent: " + codTotal);
                                if (!(orderModel.getStatus().equals("done"))){
                                    if (orderModel.getPaymant_method().equals("cod")){
                                        Log.d(TAG, "onEvent: " + orderModel.getMedecine_price());
                                        total += Integer.parseInt(orderModel.getMedecine_price());

                                    }
                                }
                            }

                            if(mOrderModel.size() == 0){
                                cv_total.setVisibility(View.GONE);
                            }else {
                                cv_total.setVisibility(View.VISIBLE);
                            }







                            Log.d(TAG, "onComplete: " + orderModel.getMedecine_name());
                        }


                        if(total != 0){
                            int cod = total;
                            tv_codTotal.setText("₱" + (cod+100));
                        }

                        if(mOrderModel.size() == 0){
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

                        MyOrderListAdapter myOrderListAdapter = new MyOrderListAdapter(getContext(), mOrderModel);
                        rv_orderList.setAdapter(myOrderListAdapter);


                    }
                });


        return view;
    }
}