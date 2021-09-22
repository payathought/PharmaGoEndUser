package com.example.pharmagoenduser.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmagoenduser.Model.CartModel;
import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

        db.collection(mContext.getString(R.string.COLLECTION_PHARMACYLIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(cartModel.getPharmacy_id())){
                                    Log.d(TAG, "onComplete: on DB inside if");
                                    PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                    holder.tv_medName.setText(cartModel.getMedecine_name());
                                    holder.tv_price.setText("₱"+cartModel.getMedecine_price());

                                    holder.tv_pharmaName.setText(pharmacyModel.getPharmacy_name());
                                    holder.tv_quantity.setText(cartModel.getQuantity());



                                }
                            }
                        }


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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);

        }
    }


}
