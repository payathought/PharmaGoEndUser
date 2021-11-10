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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmagoenduser.Model.CartModel;
import com.example.pharmagoenduser.Model.MedicineModel;
import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.SignUp;
import com.example.pharmagoenduser.View.Dialog.OrderUpdateDialog;
import com.example.pharmagoenduser.View.Dialog.ViewUserInfoDialog;
import com.example.pharmagoenduser.View.ViewOrderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MyCartItemListAdapter extends RecyclerView.Adapter<MyCartItemListAdapter.ViewHolder>  {
    private Context mContext;
    ArrayList<CartModel> mCartModel = new ArrayList();
    private static final String TAG = "MedicineListAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyCartItemListAdapter(Context mContext, ArrayList<CartModel> mCartModel) {
        this.mContext = mContext;
        this.mCartModel = mCartModel;
    }

    @NonNull
    @Override
    public MyCartItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_item_list, parent,false);
        MyCartItemListAdapter.ViewHolder holder = new MyCartItemListAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartItemListAdapter.ViewHolder holder, int position) {

        CartModel cartModel = mCartModel.get(position);

        db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(cartModel.getMedicine_id())){
                                    Log.d(TAG, "onComplete: on DB inside if");
                                    MedicineModel medicineModel = document.toObject(MedicineModel.class);

                                    holder.tv_medName.setText(medicineModel.getDescription());
                                    holder.tv_price.setText("₱"+cartModel.getMedecine_price());

                                    holder.tv_pharmaName.setText(medicineModel.getMedecine_name());
                                    holder.tv_quantity.setText(cartModel.getQuantity());



                                }
                            }
                        }


                    }
                });


    holder.btn_update_order.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OrderUpdateDialog dialog = new OrderUpdateDialog(mCartModel.get(position));
            dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "PharmaGo");
        }
    });

        holder.btn_cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("Are you sure to delete this item? ");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                CartModel cartModel = mCartModel.get(position);
                                db.collection(mContext.getString(R.string.COLLECTION_CART))
                                        .document(cartModel.getCart_id())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toasty.success(mContext,
                                                        "Item successfully deleted.", Toast.LENGTH_LONG)
                                                        .show();
                                                dialog.cancel();
                                            }
                                        });
                            }
                        });

                builder1.setNegativeButton(
                        "cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            }
        });





    }

    @Override
    public int getItemCount() {
        return mCartModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        ConstraintLayout parent_layout;
        TextView tv_pharmaName,tv_price,tv_medName,tv_quantity;
        Button btn_cancelOrder,btn_update_order;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            btn_cancelOrder = itemView.findViewById(R.id.btn_cancelOrder);
            btn_update_order = itemView.findViewById(R.id.btn_update_order);

        }
    }


}
