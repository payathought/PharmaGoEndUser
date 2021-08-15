package com.example.pharmagoenduser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.OrderMedicineActivity;
import com.example.pharmagoenduser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyOrderListAdapter extends RecyclerView.Adapter<MyOrderListAdapter.ViewHolder>  {
    private Context mContext;
    ArrayList<OrderModel> mOrderModel = new ArrayList();
    private static final String TAG = "MedicineListAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyOrderListAdapter(Context mContext, ArrayList<OrderModel> mOrderModel) {
        this.mContext = mContext;
        this.mOrderModel = mOrderModel;
    }

    @NonNull
    @Override
    public MyOrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_list_item, parent,false);
        MyOrderListAdapter.ViewHolder holder = new MyOrderListAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderListAdapter.ViewHolder holder, int position) {

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
                                    holder.tv_orderStatus.setText(orderModel.getStatus().toUpperCase());
                                    holder.tv_pharmaName.setText(pharmacyModel.getPharmacy_name());
                                    holder.tv_quantity.setText(orderModel.getQuantity());


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








    }

    @Override
    public int getItemCount() {
        return mOrderModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        ConstraintLayout parent_layout;
        TextView tv_pharmaName,tv_price,tv_driverStatus,tv_payment_method,tv_medName,tv_orderStatus,tv_quantity;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_orderStatus = itemView.findViewById(R.id.tv_orderStatus);
            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            tv_payment_method = itemView.findViewById(R.id.tv_payment_method);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_driverStatus = itemView.findViewById(R.id.tv_driverStatus);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);






        }
    }


}
