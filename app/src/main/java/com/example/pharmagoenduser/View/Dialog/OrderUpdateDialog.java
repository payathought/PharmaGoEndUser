package com.example.pharmagoenduser.View.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;

import com.example.pharmagoenduser.Model.CartModel;
import com.example.pharmagoenduser.Model.MedicineModel;
import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.OrderMedicineActivity;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class OrderUpdateDialog extends AppCompatDialogFragment {

    TextView tv_medicineName,tv_description,tv_medicinePrice,tv_medQty,tv_total;
    EditText et_addqty;
    Button btn_addToCart;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    Intent intent;
    String id = "";
    int quantity = 1;
    int price = 0;
    int total = 0;
    String pharmacy_id = "";
    int getQty = 1;
    int medicine_quantity = 0;
    CartModel cartModel;

    FirebaseUser fUser =  FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "OrderUpdateDialog";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_order_update_dialog,null);
        btn_addToCart = view.findViewById(R.id.btn_addToCart);

        tv_medicineName = view.findViewById(R.id.tv_medicineName);
        tv_description = view.findViewById(R.id.tv_description);
        tv_medicinePrice = view.findViewById(R.id.tv_medicinePrice);
        tv_medQty = view.findViewById(R.id.tv_medQty);
        tv_total = view.findViewById(R.id.tv_total);
        et_addqty = view.findViewById(R.id.et_addqty);

        builder.setView(view);






            db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                Log.d(TAG, "onComplete: " + cartModel.getCart_id());
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals(cartModel.getMedicine_id())){

                                        MedicineModel med = document.toObject(MedicineModel.class);
                                        pharmacy_id = med.getPharmacy_id();
                                        price = Integer.parseInt(med.getMedecine_price());
                                        total = (price*quantity);
                                        tv_total.setText("Total: ₱" + total);
                                        tv_medicineName.setText(med.getMedecine_name());
                                        tv_medicinePrice.setText("Price: ₱" + total);
//                                        tv_quantity.setText(String.valueOf(quantity));
                                        medicine_quantity = med.getMedicine_quantity();
                                        tv_medQty.setText("QTY: " + medicine_quantity);
                                        tv_description.setText(med.getDescription());

                                        getQty = Integer.parseInt(cartModel.getQuantity());
                                        et_addqty.setText(String.valueOf(getQty));
                                        et_addqty.setSelection(et_addqty.getText().length());
                                    }
                                }
                            }
                       

                        }
                    });


        et_addqty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String enteredString = charSequence.toString();
                if (enteredString.startsWith("0")) {
                    Toast.makeText(getActivity(),
                            "You can't Enter Zero (0)",
                            Toast.LENGTH_SHORT).show();
                    et_addqty.setText("1");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (!et_addqty.getText().toString().equals("")){
                    getQty = Integer.parseInt( et_addqty.getText().toString());
                    total = (getQty*price);
                    tv_total.setText("Total: ₱" + total);
                    if(getQty <= medicine_quantity){
                        Drawable d = getResources().getDrawable(R.drawable.shape_button_create_account);
                        btn_addToCart.setBackground(d);
                        btn_addToCart.setEnabled(true);

                    }
                    else{
                        Drawable d = getResources().getDrawable(R.drawable.shape_button_create_account_disable);
                        btn_addToCart.setBackground(d);
                        btn_addToCart.setEnabled(false);
                    }
                }else{

                    Drawable d = getResources().getDrawable(R.drawable.shape_button_create_account_disable);
                    btn_addToCart.setBackground(d);
                    btn_addToCart.setEnabled(false);
                    tv_total.setText("Total: ₱0");
                }

            }
        });

        btn_addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    cartModel.setMedecine_price(String.valueOf(total));
                    cartModel.setQuantity(String.valueOf(getQty));
                    db.collection(getString(R.string.COLLECTION_CART))
                            .document(cartModel.getCart_id())
                            .set(cartModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toasty.success(getActivity(),
                                            "Order Updated", Toast.LENGTH_LONG)
                                            .show();
                                    dismiss();
                                }
                            });


                }






        });

        return builder.create();

    }

    public OrderUpdateDialog() {
    }

    public OrderUpdateDialog(CartModel cartModel) {

        this.cartModel = cartModel;
    }
}
