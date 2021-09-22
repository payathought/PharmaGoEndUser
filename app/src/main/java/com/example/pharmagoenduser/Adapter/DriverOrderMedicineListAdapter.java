package com.example.pharmagoenduser.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmagoenduser.Model.MyOrderModel;
import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.Dialog.ViewUserInfoDialog;
import com.example.pharmagoenduser.View.ForgotPasswordDialog;
import com.example.pharmagoenduser.View.ViewOrderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class DriverOrderMedicineListAdapter  extends RecyclerView.Adapter<DriverOrderMedicineListAdapter.ViewHolder>  {
    private Context mContext;
    ArrayList<MyOrderModel> mOrderModel = new ArrayList();
    private static final String TAG = "OrderListAdpater";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();;

    public DriverOrderMedicineListAdapter(Context mContext, ArrayList<MyOrderModel> mOrderModel) {
        this.mContext = mContext;
        this.mOrderModel = mOrderModel;
    }

    @NonNull
    @Override
    public DriverOrderMedicineListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_driver_medecine_list_item, parent,false);
        DriverOrderMedicineListAdapter.ViewHolder holder = new DriverOrderMedicineListAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DriverOrderMedicineListAdapter.ViewHolder holder, int position) {

        MyOrderModel orderModel = mOrderModel.get(position);

        firebaseUser = mFirebaseAuth.getCurrentUser();


        db.collection(mContext.getString(R.string.COLLECTION_PHARMACYLIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(orderModel.getPharmacy_id())){

                                    PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                    holder.tv_medName.setText("Ordder Id: " + orderModel.getMyOrder_id());

                                    holder.tv_driverStatus.setText(orderModel.getStatus());
                                    holder.tv_pharmaName.setText(pharmacyModel.getPharmacy_name());


                                    if(orderModel.getPayment_method().equals("cod")){
                                        holder.tv_payment_method.setText("COD");

                                    }else {
                                        holder.tv_payment_method.setText("Paid");

                                    }



                                }
                            }
                        }


                    }
                });


        holder.btn_olAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyOrderModel order = mOrderModel.get(position);
                order.setDriver_status("accepted");
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
        TextView tv_pharmaName,tv_driverStatus,tv_payment_method,tv_medName;
        Button btn_olAccept;
        CardView card_parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            tv_payment_method = itemView.findViewById(R.id.tv_payment_method);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_driverStatus = itemView.findViewById(R.id.tv_driverStatus);
            btn_olAccept = itemView.findViewById(R.id.btn_olAccept);
            card_parent = itemView.findViewById(R.id.card_parent);








        }
    }


}
