package com.example.pharmagoenduser.View;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmagoenduser.Adapter.PharmacyListAdapter;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import java.util.ArrayList;


public class PharmacyListFragment extends Fragment {

    View view;


    private ArrayList<PharmacyModel> mPharmacyModel;

    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_pharmacyList;
    Button btn_addPharmacy;
    ProgressDialog progressDialog;

    ImageView iv_empty;
    TextView tv_empty;
    ConstraintLayout parent_layout;
    ListenerRegistration eventListener;
    private static final String TAG = "PharmacyListFragment";
    public PharmacyListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pharmacy_list, container, false);

        rv_pharmacyList = view.findViewById(R.id.rv_pharmacyList);

        rv_pharmacyList.setHasFixedSize(true);
        rv_pharmacyList.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        mPharmacyModel = new ArrayList<>();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Pharma Go");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        tv_empty = view.findViewById(R.id.tv_empty);
        iv_empty =  view.findViewById(R.id.iv_empty);
        parent_layout =  view.findViewById(R.id.parent_layout);


        getPharmaList();

        return view;
    }

    public void getPharmaList(){
        eventListener =   db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mPharmacyModel.clear();

                        if(queryDocumentSnapshots != null){
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                            {
                                PharmacyModel pharmacyModel = new PharmacyModel();
                                pharmacyModel.setPharmacy_id(document.getId());
                                pharmacyModel.setPharmacy_name(document.get("pharmacy_name").toString());
                                pharmacyModel.setPharmacy_address(document.get("pharmacy_address").toString());
                                mPharmacyModel.add(pharmacyModel);


                            }
                            if(mPharmacyModel.size() == 0){
                                iv_empty.setVisibility(View.VISIBLE);
                                tv_empty.setVisibility(View.VISIBLE);
                                rv_pharmacyList.setVisibility(View.GONE);
                                parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                            }else {
                                iv_empty.setVisibility(View.GONE);
                                tv_empty.setVisibility(View.GONE);
                                rv_pharmacyList.setVisibility(View.VISIBLE);
                                parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            }
                            PharmacyListAdapter pharmacyListAdapter = new PharmacyListAdapter(getContext(), mPharmacyModel);
                            rv_pharmacyList.setAdapter(pharmacyListAdapter);
                        }



                    }
                });
    }
}