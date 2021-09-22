package com.example.pharmagoenduser.View.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pharmagoenduser.Model.CartModel;
import com.example.pharmagoenduser.Model.MyOrderItemsModel;
import com.example.pharmagoenduser.Model.MyOrderModel;
import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ProceedToCheckOutDialog extends AppCompatDialogFragment {

    CartModel cartModel;
    RadioButton rb_card, rb_cod;
    RadioGroup radio_group;
    EditText et_fn, et_ln, et_expiry,et_account_number;
    Button btn_cancel,btn_submit;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tv_rbRequired;
    ArrayList<CartModel> mCartModel;
    MyOrderModel myOrderModel;
    private static final String TAG = "ProceedToOrderDialog";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layou_proceed_to_order_dialog,null);

        et_fn = view.findViewById(R.id.et_fn);
        et_ln = view.findViewById(R.id.et_ln);
        et_expiry = view.findViewById(R.id.et_expiry);
        et_account_number = view.findViewById(R.id.et_account_number);
        radio_group = view.findViewById(R.id.radio_group);

        btn_submit = view.findViewById(R.id.btn_submit);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        rb_card = view.findViewById(R.id.rb_card);
        rb_cod = view.findViewById(R.id.rb_cod);
        tv_rbRequired = view.findViewById(R.id.tv_rbRequired);
        FirebaseAuth mFbAuth = FirebaseAuth.getInstance();
        builder.setView(view);
        myOrderModel = new MyOrderModel();
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.rb_card:

                        et_fn.setVisibility(View.VISIBLE);
                        et_expiry.setVisibility(View.VISIBLE);
                        et_ln.setVisibility(View.VISIBLE);
                        et_ln.setVisibility(View.VISIBLE);
                        et_account_number.setVisibility(View.VISIBLE);
                        tv_rbRequired.setVisibility(View.GONE);
                        myOrderModel.setPayment_method("card");
                        break;
                    case R.id.rb_cod:
                        et_fn.setVisibility(View.GONE);
                        et_expiry.setVisibility(View.GONE);
                        et_ln.setVisibility(View.GONE);
                        et_ln.setVisibility(View.GONE);
                        et_account_number.setVisibility(View.GONE);
                        tv_rbRequired.setVisibility(View.GONE);
                        myOrderModel.setPayment_method("cod");
                        break;

                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 if((!rb_card.isChecked() && !rb_cod.isChecked()))
                {
                    tv_rbRequired.setVisibility(View.VISIBLE);
                }else{


                     myOrderModel.setDateOrdered(null);
                     myOrderModel.setDriver_id("");
                     myOrderModel.setDriver_status("pending");
                     myOrderModel.setPharmacy_id(mCartModel.get(0).getPharmacy_id());
                     myOrderModel.setUser_id(mCartModel.get(0).getUser_id());
                     myOrderModel.setMyOrder_id("");
                     myOrderModel.setStatus("pending");
                    
                        
                         
                         db.collection(getString(R.string.COLLECTION_MY_ORDERLIST))
                                 .add(myOrderModel)
                                 .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    
                                     @Override
                                     public void onComplete(@NonNull Task<DocumentReference> task) {
                                         if (task.isSuccessful()) {
                                             Log.d("TAG", "onComplete: " + task.getResult().getId());
                                             for(CartModel myCart : mCartModel){
                                                 MyOrderItemsModel itemsModel = new MyOrderItemsModel();
                                                 itemsModel.setMyOrder_id(task.getResult().getId());
                                                 itemsModel.setMedecine_name(myCart.getMedecine_name());
                                                 itemsModel.setMedecine_price(myCart.getMedecine_price());
                                                 itemsModel.setMedicine_id(myCart.getMedicine_id());
                                                 itemsModel.setPharmacy_id(myCart.getPharmacy_id());
                                                 itemsModel.setQuantity(myCart.getQuantity());
                                                 itemsModel.setUser_id(myCart.getUser_id());
                                                 db.collection("My Order List Items")
                                                         .add(itemsModel)
                                                         .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                             @Override
                                                             public void onComplete(@NonNull  Task<DocumentReference> task) {
                                                                 if (task.isSuccessful()) {
                                                                     Log.d(TAG, "onComplete: added Item");
                                                                 }
                                                                 
                                                             }
                                                         });

                                                 db.collection("Cart")
                                                         .document(myCart.getCart_id())
                                                         .delete()
                                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                             @Override
                                                             public void onSuccess(Void unused) {
                                                                 Log.d(TAG, "onSuccess: deleted");
                                                             }});
                                             }
                                          
                                                     
                                         }
                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {

                                 Log.d("Exception", "onFailure: " + e);
                             }
                         });
                     dismiss();
                     Toasty.success(getActivity(),
                             "Ordered Successfully.", Toast.LENGTH_LONG)
                             .show();
                     startActivity(new Intent(getActivity(), HomeActivity.class));

                 }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder.create();

    }

    public ProceedToCheckOutDialog(ArrayList<CartModel> mCartModel) {
        this.mCartModel = mCartModel;
    }
}
