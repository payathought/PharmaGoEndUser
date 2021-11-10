package com.example.pharmagoenduser.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmagoenduser.FunctionAndMethod.FunctionMethod;
import com.example.pharmagoenduser.Model.MyOrderModel;
import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.Model.SignUpModel;
import com.example.pharmagoenduser.Model.UserModel;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.Dialog.ViewUserInfoDialog;
import com.example.pharmagoenduser.View.ViewOrderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryLogsAdapter extends RecyclerView.Adapter<HistoryLogsAdapter.ViewHolder>  {
    private Context mContext;
    ArrayList<MyOrderModel> mOrderModel = new ArrayList();
    private static final String TAG = "OrderListAdpater";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();;

    public HistoryLogsAdapter(Context mContext, ArrayList<MyOrderModel> mOrderModel) {
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
                                    SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
                                    holder.tv_medName.setText("Date Ordered: " + formatter.format(orderModel.getDateOrdered()));


                                    holder.tv_driverStatus.setText("Order Status: " + orderModel.getStatus());

                                    if(orderModel.getPayment_method().equals("cod")){
                                        holder.tv_payment_method.setText("Payment Method: COD");

                                    }else {
                                        holder.tv_payment_method.setText("Payment Method: Credit/Debit (Paid)");

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
            holder.btn_call.setVisibility(View.GONE);
            holder.btn_msg.setVisibility(View.GONE);
            holder.tv_orderStatus.setVisibility(View.VISIBLE);


        }else {
            holder.btn_call.setVisibility(View.VISIBLE);
            holder.btn_msg.setVisibility(View.VISIBLE);
            holder.btn_olAccept.setVisibility(View.VISIBLE);
            holder.btn_olCancel.setVisibility(View.VISIBLE);
            holder.tv_orderStatus.setVisibility(View.GONE);

        }


        holder.btn_olAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyOrderModel order = mOrderModel.get(position);
                order.setStatus("done");
                order.setDriver_id(firebaseUser.getUid());
                db.collection(mContext.getString(R.string.COLLECTION_MY_ORDERLIST))
                        .document(order.getMyOrder_id())
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
                MyOrderModel order = mOrderModel.get(position);
                order.setDriver_status("pending");
                order.setDriver_id(firebaseUser.getUid());
                db.collection(mContext.getString(R.string.COLLECTION_MY_ORDERLIST))
                        .document(order.getMyOrder_id())
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

        holder.btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    MyOrderModel order = mOrderModel.get(position);

                    db.collection(mContext.getString(R.string.COLLECTION_USER_INFORMATION))
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable  QuerySnapshot queryDocumentSnapshots, @Nullable  FirebaseFirestoreException e) {

                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                                    {
                                        SignUpModel userModel = document.toObject(SignUpModel.class);
                                        if(order.getUser_id().equals(userModel.getUser_id())) {


                                            Log.d(TAG, "order USer: " +order.getUser_id());

                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse("tel:" + userModel.getPhonenumber()));
                                            mContext.startActivity(intent);
                                        }
                                    }
                                }
                            });
                }else{
                    FunctionMethod functionMethod = new FunctionMethod();

                    functionMethod.callPermission(mContext);
                }

            }
        });


        holder.btn_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    MyOrderModel order = mOrderModel.get(position);

                    db.collection(mContext.getString(R.string.COLLECTION_USER_INFORMATION))
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable  QuerySnapshot queryDocumentSnapshots, @Nullable  FirebaseFirestoreException e) {

                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                                    {
                                        SignUpModel userModel = document.toObject(SignUpModel.class);
                                        if(order.getUser_id().equals(userModel.getUser_id())) {


                                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.setData(Uri.parse("smsto:" + userModel.getPhonenumber())); // This ensures only SMS apps respond
                                            intent.putExtra("sms_body", "Good day, " + userModel.getFirstname());
                                            mContext.startActivity(intent);
                                        }
                                    }
                                }
                            });
                }else{
                    FunctionMethod functionMethod = new FunctionMethod();

                    functionMethod.callPermission(mContext);
                }

            }
        });
        holder.card_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("What do you want to do? ");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "View Customer Profile",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                ViewUserInfoDialog viewUserInfoDialog = new ViewUserInfoDialog(orderModel.getUser_id());
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
        });
    }

    @Override
    public int getItemCount() {
        return mOrderModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout parent_layout;
        TextView tv_pharmaName,tv_driverStatus,tv_payment_method,tv_medName,tv_orderStatus;
        Button btn_olAccept,btn_olCancel;
        ImageButton btn_msg,btn_call;
        CardView card_parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_orderStatus = itemView.findViewById(R.id.tv_orderStatus);
            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            tv_payment_method = itemView.findViewById(R.id.tv_payment_method);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_driverStatus = itemView.findViewById(R.id.tv_driverStatus);
            btn_olAccept = itemView.findViewById(R.id.btn_olAccept);
            btn_olCancel = itemView.findViewById(R.id.btn_olCancel);
            btn_msg = itemView.findViewById(R.id.btn_msg);
            btn_call = itemView.findViewById(R.id.btn_call);
            card_parent = itemView.findViewById(R.id.card_parent);







        }
    }


}
