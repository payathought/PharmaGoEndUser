package com.example.pharmagoenduser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pharmagoenduser.Model.MedicineModel;
import com.example.pharmagoenduser.OrderMedicineActivity;
import com.example.pharmagoenduser.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<MedicineModel>  mMedecineModel = new ArrayList();
    private static final String TAG = "MedicineListAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MedicineListAdapter(Context context, ArrayList<MedicineModel> mMedecineModel) {
        this.mContext = context;
        this.mMedecineModel = mMedecineModel;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_medecine_list_item, parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        mMedecineModel.forEach(item -> Log.d(TAG, "onBindViewHolder: " + item.getMedecine_name()));
        MedicineModel medicineModel = mMedecineModel.get(position);
        holder.tv_medecineName.setText(medicineModel.getMedecine_name());
        holder.tv_price.setText("₱"+medicineModel.getMedecine_price());
        holder.tv_description.setText(medicineModel.getDescription());
        holder.tv_category.setText(medicineModel.getCategory());

        holder.btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Order");
                MedicineModel medicineModel = mMedecineModel.get(position);
                Intent intent = new Intent(mContext, OrderMedicineActivity.class);
                intent.putExtra("medicine_id", medicineModel.getMedicine_id());
                intent.putExtra("pharmacy_id", medicineModel.getPharmacy_id());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });


        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return mMedecineModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout parent_layout;
        TextView tv_medecineName,tv_price,tv_description,tv_category;
        Button btn_order;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_medecineName = itemView.findViewById(R.id.tv_medecineName);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_category = itemView.findViewById(R.id.tv_category);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            btn_order = itemView.findViewById(R.id.btn_order);





        }
    }



}