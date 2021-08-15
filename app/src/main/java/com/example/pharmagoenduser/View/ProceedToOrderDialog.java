package com.example.pharmagoenduser.View;

import android.annotation.SuppressLint;
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

import com.example.pharmagoenduser.Model.OrderModel;
import com.example.pharmagoenduser.OrderMedicineActivity;
import com.example.pharmagoenduser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class ProceedToOrderDialog extends AppCompatDialogFragment {

    OrderModel orderModel;
    RadioButton rb_card, rb_cod;
    RadioGroup radio_group;
    EditText et_fn, et_ln, et_expiry,et_account_number;
    Button btn_cancel,btn_submit;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tv_rbRequired;
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
                        orderModel.setPaymant_method("card");
                        tv_rbRequired.setVisibility(View.GONE);


                        break;
                    case R.id.rb_cod:
                        et_fn.setVisibility(View.GONE);
                        et_expiry.setVisibility(View.GONE);
                        et_ln.setVisibility(View.GONE);
                        et_ln.setVisibility(View.GONE);
                        et_account_number.setVisibility(View.GONE);
                        orderModel.setPaymant_method("cod");
                        tv_rbRequired.setVisibility(View.GONE);
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

                     db.collection(getString(R.string.COLLECTION_ORDERLIST))
                             .add(orderModel)
                             .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                 @Override
                                 public void onComplete(@NonNull Task<DocumentReference> task) {
                                     if (task.isSuccessful()) {
                                         Log.d("TAG", "onComplete: " + task.getResult().getId());

                                         Toasty.success(getActivity(),
                                                 "Ordered Successfully.", Toast.LENGTH_LONG)
                                                 .show();
                                         startActivity(new Intent(getActivity(), HomeActivity.class));

                                     }
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {

                             Log.d("Exception", "onFailure: " + e);
                         }
                     });
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

    public ProceedToOrderDialog(OrderModel orderModel) {
        this.orderModel = orderModel;
    }
}
