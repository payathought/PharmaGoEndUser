package com.example.pharmagoenduser.View.Dialog;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pharmagoenduser.MainActivity;
import com.example.pharmagoenduser.Model.SignUpModel;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.View.DriverHomeActivity;
import com.example.pharmagoenduser.View.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import es.dmoral.toasty.Toasty;

public class ViewUserInfoDialog extends AppCompatDialogFragment {
    FirebaseFirestore db;
    TextView tv_name,tv_address,tv_phonenumber;
    Button btn_ok;
    String user_id = "";

    private static final String TAG = "ViewUserInfoDialog";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_view_user_dialog,null);
        tv_name = view.findViewById(R.id.tv_name);
        tv_address = view.findViewById(R.id.tv_address);
        tv_phonenumber = view.findViewById(R.id.tv_phonenumber);
        btn_ok = view.findViewById(R.id.btn_ok);
        db = FirebaseFirestore.getInstance();
        builder.setView(view);
        CollectionReference userinfo = db.collection(getString(R.string.COLLECTION_USER_INFORMATION));
        Query userInfoQuery = userinfo.whereEqualTo("user_id", user_id);
        userInfoQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {

                    SignUpModel signUpModel = querySnapshot.getDocuments().get(0).toObject(SignUpModel.class);

                    tv_name.setText(signUpModel.getFirstname() + " " + signUpModel.getLastname());
                    tv_address.setText(signUpModel.getAddress());
                    tv_phonenumber.setText(signUpModel.getPhonenumber());


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

    public ViewUserInfoDialog(String user_id) {
        this.user_id = user_id;
    }
}
