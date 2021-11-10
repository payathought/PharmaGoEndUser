package com.example.pharmagoenduser.View.Dialog;

import android.app.Activity;
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

import com.example.pharmagoenduser.FunctionAndMethod.FunctionMethod;
import com.example.pharmagoenduser.MainActivity;
import com.example.pharmagoenduser.Model.SignUpModel;
import com.example.pharmagoenduser.R;
import com.example.pharmagoenduser.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import es.dmoral.toasty.Toasty;

public class UpdateUserInfoDialog extends AppCompatDialogFragment {
    FirebaseFirestore db;
    EditText et_FirstName,et_LastName,et_address,et_userName,et_phoneNumber;
    String user_id = "";
    String document_id;
    Button btn_updateInfo;
    SignUpModel signUpModel;
    Activity mActivty;
    FunctionMethod functionMethod = new FunctionMethod();
    private static final String TAG = "ViewUserInfoDialog";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_update_info_dialog,null);

        et_FirstName  = view.findViewById(R.id.et_FirstName);
        et_LastName  = view.findViewById(R.id.et_LastName);
        et_address  = view.findViewById(R.id.et_address);
        et_userName  = view.findViewById(R.id.et_userName);
        et_phoneNumber  = view.findViewById(R.id.et_phoneNumber);
        btn_updateInfo  = view.findViewById(R.id.btn_updateInfo);


        db = FirebaseFirestore.getInstance();
        builder.setView(view);
        CollectionReference userinfo = db.collection(getString(R.string.COLLECTION_USER_INFORMATION));
        Query userInfoQuery = userinfo.whereEqualTo("user_id", user_id);
        userInfoQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    document_id = querySnapshot.getDocuments().get(0).getId();
                    signUpModel = querySnapshot.getDocuments().get(0).toObject(SignUpModel.class);
                    et_FirstName.setText(signUpModel.getFirstname());
                    et_LastName.setText(signUpModel.getLastname());
                    et_address.setText(signUpModel.getAddress());
                    et_phoneNumber.setText(signUpModel.getPhonenumber());
                    et_userName.setText(signUpModel.getUsername());



                }
            }
        });


        btn_updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (functionMethod.haveNetworkConnected(getActivity()))
                {


                    if (et_FirstName.getText().toString().trim().isEmpty())
                    {
                        setErrors(et_FirstName);
                    }else if (et_LastName.getText().toString().trim().isEmpty())
                    {
                        setErrors(et_LastName);
                    }else if (et_userName.getText().toString().trim().isEmpty())
                    {
                        setErrors(et_userName);
                    }else if (et_phoneNumber.getText().toString().trim().isEmpty())
                    {
                        setErrors(et_phoneNumber);
                    }else if (et_address.getText().toString().trim().isEmpty())
                    {
                        setErrors(et_address);
                    } else
                    {
                            updateUser();
                    }
                }else{
                    Toasty.error(getActivity(),
                            "Please check your internet connection.", Toast.LENGTH_LONG)
                            .show();
                }

                dismiss();
            }
        });





        return builder.create();

    }
    private void setErrors(EditText txt)
    {
        txt.setError("Required Field");
        txt.requestFocus();
    }

    public void updateUser(){

        if(signUpModel.getUser_id() != null) {
            Log.e("If", "savedDatatoFS: Success");
            signUpModel.setFirstname(et_FirstName.getText().toString());
            signUpModel.setLastname(et_LastName.getText().toString());
            signUpModel.setUsername(et_userName.getText().toString());
            signUpModel.setPhonenumber(et_phoneNumber.getText().toString());
            signUpModel.setAddress(et_address.getText().toString());



            db.collection(getString(R.string.COLLECTION_USER_INFORMATION))
                    .document(document_id)
                    .set(signUpModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toasty.success(mActivty,
                                        "Updated Successfully", Toast.LENGTH_LONG)
                                        .show();
                                dismiss();
                            }
                        }
                    });


        }else{
            Log.e("Else", "savedDatatoFS: Failed");

        }

    }
    public UpdateUserInfoDialog(String user_id,Activity mActivty) {

        this.user_id = user_id;
        this.mActivty = mActivty;

    }
}
