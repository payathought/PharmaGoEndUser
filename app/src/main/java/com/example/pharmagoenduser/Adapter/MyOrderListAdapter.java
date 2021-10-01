package com.example.pharmagoenduser.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmagoenduser.Model.MyOrderModel;
import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.OrderMedicineActivity;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.Dialog.OrderCanceledDialog;
import com.example.pharmagoenduser.View.Dialog.ViewUserInfoDialog;
import com.example.pharmagoenduser.View.ViewOrderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyOrderListAdapter extends RecyclerView.Adapter<MyOrderListAdapter.ViewHolder>  {
    private Context mContext;
    ArrayList<MyOrderModel> mOrderModel = new ArrayList();
    private static final String TAG = "MedicineListAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyOrderListAdapter(Context mContext, ArrayList<MyOrderModel> mOrderModel) {
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

        MyOrderModel orderModel = mOrderModel.get(position);

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

                                    holder.tv_pharmaName.setText(pharmacyModel.getPharmacy_name());
                                    holder.tv_medName.setText("Order Id: " + orderModel.getMyOrder_id());



                                    if(orderModel.getPayment_method().equals("cod")){
                                        holder.tv_payment_method.setText("COD");

                                    }else {
                                        holder.tv_payment_method.setText("Paid");

                                    }

                                    if(orderModel.getDriver_status().equals("pending")){

                                        if(orderModel.getStatus().equals("accepted")){
                                            holder.tv_orderStatus.setText("Accepted By The Pharmacy");
                                            holder.tv_orderStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                                        }else {
                                            holder.tv_orderStatus.setText(orderModel.getStatus().toUpperCase());
                                        }

                                    }else {
                                        if(orderModel.getStatus().equals("done")){
                                            holder.tv_orderStatus.setText(orderModel.getStatus().toUpperCase());
                                        }else {
                                            holder.tv_orderStatus.setText("Accepted By The driver");
                                            holder.tv_orderStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                                        }
                                    }

                                }
                            }
                        }


                    }
                });


        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderModel.getStatus().equals("cancel")){
                    OrderCanceledDialog dialog = new OrderCanceledDialog();
                    dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "PharmaGo");
                }else{
                    if (orderModel.getDriver_id().isEmpty()){
                        Intent i = new Intent(mContext, ViewOrderActivity.class);
                        i.putExtra("order_id", orderModel.getMyOrder_id());
                        mContext.startActivity(i);
                    }else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                        builder1.setMessage("What do you want to do? ");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "View Driver Profile",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        ViewUserInfoDialog viewUserInfoDialog = new ViewUserInfoDialog(orderModel.getDriver_id());
                                        viewUserInfoDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "PharmaGo");
                                    }
                                });

                        builder1.setNegativeButton(
                                "View Order",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                        Intent i = new Intent(mContext, ViewOrderActivity.class);
                                        i.putExtra("order_id", orderModel.getMyOrder_id());
                                        mContext.startActivity(i);
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
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
        TextView tv_pharmaName,tv_payment_method,tv_orderStatus,tv_medName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_orderStatus = itemView.findViewById(R.id.tv_orderStatus);
            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_payment_method = itemView.findViewById(R.id.tv_payment_method);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            parent_layout = itemView.findViewById(R.id.parent_layout);







        }
    }


}
