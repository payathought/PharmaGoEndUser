package com.example.pharmagoenduser.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryLogsAdapter extends RecyclerView.Adapter<HistoryLogsAdapter.ViewHolder>  {
    private Context mContext;
    ArrayList<OrderModel> mOrderModel = new ArrayList();
    private static final String TAG = "OrderListAdpater";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();;

    public HistoryLogsAdapter(Context mContext, ArrayList<OrderModel> mOrderModel) {
        this.mContext = mContext;
        this.mOrderModel = mOrderModel;
    }

    @NonNull
    @Override
    public HistoryLogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_logs_list_item, parent,false);
        HistoryLogsAdapter.ViewHolder holder = new HistoryLogsAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryLogsAdapter.ViewHolder holder, int position) {

        OrderModel orderModel = mOrderModel.get(position);
        db.collection(mContext.getString(R.string.COLLECTION_PHARMACYLIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(orderModel.getPharmacy_id())){
                                    Log.d(TAG, "onComplete: on DB inside if");
                                    PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                    holder.tv_medName.setText(orderModel.getMedecine_name());
                                    holder.tv_price.setText("₱"+orderModel.getMedecine_price());
                                    holder.tv_driverStatus.setText(orderModel.getStatus());
                                    holder.tv_quantity.setText(orderModel.getQuantity());
                                    holder.tv_pharmaName.setText(pharmacyModel.getPharmacy_name());

                                    if(orderModel.getPaymant_method().equals("cod")){
                                        holder.tv_payment_method.setText("COD");

                                    }else {
                                        holder.tv_payment_method.setText("Paid");

                                    }



                                }
                            }
                        }


                    }
                });

        firebaseUser = mFirebaseAuth.getCurrentUser();


        if (orderModel.getStatus().equals("done")){
            holder.btn_olAccept.setVisibility(View.GONE);
            holder.btn_olCancel.setVisibility(View.GONE);
            holder.tv_orderStatus.setVisibility(View.VISIBLE);


        }else {
            holder.btn_olAccept.setVisibility(View.VISIBLE);
            holder.btn_olCancel.setVisibility(View.VISIBLE);
            holder.tv_orderStatus.setVisibility(View.GONE);

        }


        holder.btn_olAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderModel order = mOrderModel.get(position);
                order.setStatus("done");
                order.setDriver_id(firebaseUser.getUid());
                db.collection(mContext.getString(R.string.COLLECTION_ORDERLIST))
                        .document(order.getOrder_id())
                        .set(order)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                notifyDataSetChanged();
                            }
                        });
            }
        });
        holder.btn_olCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderModel order = mOrderModel.get(position);
                order.setDriver_status("pending");
                order.setDriver_id(firebaseUser.getUid());
                db.collection(mContext.getString(R.string.COLLECTION_ORDERLIST))
                        .document(order.getOrder_id())
                        .set(order)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                notifyDataSetChanged();
                            }
                        });
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
        return mOrderModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout parent_layout;
        TextView tv_pharmaName,tv_price,tv_driverStatus,tv_payment_method,tv_medName,tv_orderStatus,tv_quantity;
        Button btn_olAccept,btn_olCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_orderStatus = itemView.findViewById(R.id.tv_orderStatus);
            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            tv_payment_method = itemView.findViewById(R.id.tv_payment_method);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_driverStatus = itemView.findViewById(R.id.tv_driverStatus);
            btn_olAccept = itemView.findViewById(R.id.btn_olAccept);
            btn_olCancel = itemView.findViewById(R.id.btn_olCancel);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);






        }
    }


}
