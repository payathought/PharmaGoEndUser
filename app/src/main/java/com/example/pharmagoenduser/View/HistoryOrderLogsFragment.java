package com.example.pharmagoenduser.View;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pharmagoenduser.Adapter.DriverOrderMedicineListAdapter;
import com.example.pharmagoenduser.Adapter.HistoryLogsAdapter;
import com.example.pharmagoenduser.FunctionAndMethod.FunctionMethod;
import com.example.pharmagoenduser.Model.MyOrderModel;
import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class HistoryOrderLogsFragment extends Fragment {
    private View view;
    private ArrayList<MyOrderModel> mOrderModel;
    private static final String TAG = "OrderListFragment";
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_historylogs;
    ProgressDialog progressDialog;
    ImageView iv_empty;
    ConstraintLayout parent_layout;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    FunctionMethod functionMethod = new FunctionMethod();
    public HistoryOrderLogsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_history_order_logs, container, false);


        rv_historylogs = view.findViewById(R.id.rv_historylogs);

        rv_historylogs.setHasFixedSize(true);
        rv_historylogs.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        mOrderModel = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Pharma GO");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        iv_empty = view.findViewById(R.id.iv_empty);
        parent_layout = view.findViewById(R.id.parent_layout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        functionMethod.callPermission(getActivity());
        functionMethod.sendSMSPermission(getActivity());

        db.collection(getString(R.string.COLLECTION_MY_ORDERLIST))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        mOrderModel.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            MyOrderModel orderModel = document.toObject(MyOrderModel.class);
                            orderModel.setMyOrder_id(document.getId());
                            if ((orderModel.getDriver_status().equals("accepted")) && firebaseUser.getUid().equals(orderModel.getDriver_id())) {

                                mOrderModel.add(orderModel);
                            }
                        }

                        if(mOrderModel.size() == 0){
                            iv_empty.setVisibility(View.VISIBLE);
                            rv_historylogs.setVisibility(View.GONE);
                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                        }else {
                            iv_empty.setVisibility(View.GONE);
                            rv_historylogs.setVisibility(View.VISIBLE);
                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }


                        HistoryLogsAdapter adapter = new HistoryLogsAdapter(getContext(), mOrderModel);
                        adapter.notifyDataSetChanged();
                        rv_historylogs.setAdapter(adapter);


                    }
                });

//        db.collection(getString(R.string.COLLECTION_ORDERLIST))
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                        mOrderModel.clear();
//                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
//                        {
//
//                            if ((document.get("driver_status").toString().equals("accepted"))) {
//                                OrderModel orderModel = document.toObject(OrderModel.class);
////                                orderModel.setOrder_id(document.getId());
////                                orderModel.setMedicine_id(document.get("medicine_id").toString());
////                                orderModel.setMedecine_name(document.get("medecine_name").toString());
////                                orderModel.setMedecine_price(document.get("medecine_price").toString());
////                                orderModel.setDriver_status(document.get("driver_status").toString());
////                                orderModel.setUser_id(document.get("user_id").toString());
////                                orderModel.setDriver_id(document.get("driver_id").toString());
////                                orderModel.setStatus(document.get("status").toString());
//
//                                if(firebaseUser.getUid().equals(orderModel.getDriver_id())){
//                                    mOrderModel.add(orderModel);
//                                }
//
//                            }
//                        }
//                        if(mOrderModel.size() == 0){
//                            iv_empty.setVisibility(View.VISIBLE);
//                            rv_historylogs.setVisibility(View.GONE);
//                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));
//
//                        }else {
//                            iv_empty.setVisibility(View.GONE);
//                            rv_historylogs.setVisibility(View.VISIBLE);
//                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        }
//
//
//                        HistoryLogsAdapter adapter = new HistoryLogsAdapter(getContext(), mOrderModel);
//                        adapter.notifyDataSetChanged();
//                        rv_historylogs.setAdapter(adapter);
//
//                    }
//                });
//        db.collection(getString(R.string.COLLECTION_ORDERLIST))
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                        mOrderModel.clear();
//                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
//                        {
//
//                            if ((document.get("driver_status").toString().equals("accepted"))) {
//                                OrderModel orderModel = document.toObject(OrderModel.class);
////                                orderModel.setOrder_id(document.getId());
////                                orderModel.setMedicine_id(document.get("medicine_id").toString());
////                                orderModel.setMedecine_name(document.get("medecine_name").toString());
////                                orderModel.setMedecine_price(document.get("medecine_price").toString());
////                                orderModel.setDriver_status(document.get("driver_status").toString());
////                                orderModel.setUser_id(document.get("user_id").toString());
////                                orderModel.setDriver_id(document.get("driver_id").toString());
////                                orderModel.setStatus(document.get("status").toString());
//
//                                if(firebaseUser.getUid().equals(orderModel.getDriver_id())){
//                                    mOrderModel.add(orderModel);
//                                }
//
//                            }
//                        }
//                        if(mOrderModel.size() == 0){
//                            iv_empty.setVisibility(View.VISIBLE);
//                            rv_historylogs.setVisibility(View.GONE);
//                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));
//
//                        }else {
//                            iv_empty.setVisibility(View.GONE);
//                            rv_historylogs.setVisibility(View.VISIBLE);
//                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        }
//
//
//                        HistoryLogsAdapter adapter = new HistoryLogsAdapter(getContext(), mOrderModel);
//                        adapter.notifyDataSetChanged();
//                        rv_historylogs.setAdapter(adapter);
//
//                    }
//                });

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        functionMethod.callPermission(getActivity());
        functionMethod.sendSMSPermission(getActivity());
    }
}