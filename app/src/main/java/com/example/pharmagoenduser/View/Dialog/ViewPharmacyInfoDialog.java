package com.example.pharmagoenduser.View.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pharmagoenduser.Model.PharmacyModel;
import com.example.pharmagoenduser.Model.SignUpModel;
import com.example.pharmagoenduser.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewPharmacyInfoDialog extends AppCompatDialogFragment {

    FirebaseFirestore db;
    TextView tv_name,tv_address;
    Button btn_ok;
    String pharmacy_id = "";

    private static final String TAG = "ViewUserInfoDialog";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_view_pharmacy_dialog,null);
        tv_name = view.findViewById(R.id.tv_name);
        tv_address = view.findViewById(R.id.tv_address);

        btn_ok = view.findViewById(R.id.btn_ok);
        db = FirebaseFirestore.getInstance();
        builder.setView(view);
        CollectionReference userinfo = db.collection(getString(R.string.COLLECTION_PHARMACYLIST));
        Query userInfoQuery = userinfo.whereEqualTo("pharmacy_id", pharmacy_id);
        userInfoQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {

                    PharmacyModel pharmacyModel = querySnapshot.getDocuments().get(0).toObject(PharmacyModel.class);

                    tv_name.setText(pharmacyModel.getPharmacy_name());
                    tv_address.setText(pharmacyModel.getPharmacy_address());


                }
            }
        });


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });





        return builder.create();

    }

    public ViewPharmacyInfoDialog(String pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
    }
}
