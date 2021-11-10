package com.example.pharmagoenduser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmagoenduser.FunctionAndMethod.FunctionMethod;
import com.example.pharmagoenduser.Model.SignUpModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {
    Button btn_Register;
    EditText et_FirstName,et_address, et_LastName,et_EmailAddress,et_userName,et_Password,et_confirmPassword,et_phoneNumber;
    TextView tv_matcher, tv_rbRequired;
    ProgressDialog progressDialog;
    FirebaseAuth mFirebaseAuth;
    SignUpModel signupModel;
    FirebaseFirestore db;
    FunctionMethod functionMethod = new FunctionMethod();
    RadioButton rb_user, rb_driver;
    RadioGroup radio_group;
    private static final String TAG = "SignUp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signupModel = new SignUpModel();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        btn_Register = findViewById(R.id.btn_Register);
        et_FirstName  = findViewById(R.id.et_FirstName);
        et_LastName  = findViewById(R.id.et_LastName);
        et_EmailAddress  = findViewById(R.id.et_EmailAddress);
        et_userName = findViewById(R.id.et_userName);
        et_Password = findViewById(R.id.et_Password);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        tv_matcher = findViewById(R.id.tv_matcher);
        et_phoneNumber = findViewById(R.id.et_phoneNumber);
        radio_group = findViewById(R.id.radio_group);
        rb_driver = findViewById(R.id.rb_driver);
        rb_user = findViewById(R.id.rb_user);
        tv_rbRequired = findViewById(R.id.tv_rbRequired);
        et_address = findViewById(R.id.et_address);

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.rb_user:
                        Log.d(TAG, "onCheckedChanged: " + rb_user.getText().toString());
                        signupModel.setType(rb_user.getText().toString());
                        tv_rbRequired.setVisibility(View.GONE);
                        break;
                    case R.id.rb_driver:
                        Log.d(TAG, "onCheckedChanged: " + rb_driver.getText().toString());
                        signupModel.setType(rb_driver.getText().toString());
                        tv_rbRequired.setVisibility(View.GONE);
                        break;

                }
            }
        });

        et_confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pw1 = et_Password.getText().toString();
                String pw2 = et_confirmPassword.getText().toString();
                if(pw1.equals(pw2))
                {
                    tv_matcher.setVisibility(View.GONE);

                }else
                {
                    tv_matcher.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                String pw1 = et_Password.getText().toString();
                String pw2 = et_confirmPassword.getText().toString();
                if(pw1.equals(pw2))
                {
                    tv_matcher.setVisibility(View.GONE);

                }else
                {
                    tv_matcher.setVisibility(View.VISIBLE);
                }

            }
        });

        et_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pw1 = et_Password.getText().toString();
                String pw2 = et_confirmPassword.getText().toString();
                if(pw1.equals(pw2))
                {
                    tv_matcher.setVisibility(View.GONE);

                }else
                {
                    tv_matcher.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                String pw1 = et_Password.getText().toString();
                String pw2 = et_confirmPassword.getText().toString();
                if(pw1.equals(pw2))
                {
                    tv_matcher.setVisibility(View.GONE);

                }else
                {
                    tv_matcher.setVisibility(View.VISIBLE);
                }

            }
        });
        btn_Register.setOnClickListener(v -> {

            if (functionMethod.haveNetworkConnected(SignUp.this))
            {
                String pw1 = et_Password.getText().toString().trim();
                String pw2 = et_confirmPassword.getText().toString().trim();

                if (et_FirstName.getText().toString().trim().isEmpty())
                {
                    setErrors(et_FirstName);
                }else if (et_LastName.getText().toString().trim().isEmpty())
                {
                    setErrors(et_LastName);
                }else if (et_userName.getText().toString().trim().isEmpty())
                {
                    setErrors(et_userName);
                }else if (et_EmailAddress.getText().toString().trim().isEmpty())
                {
                    setErrors(et_EmailAddress);
                }else if (et_phoneNumber.getText().toString().trim().isEmpty())
                {
                    setErrors(et_phoneNumber);
                }else if (et_address.getText().toString().trim().isEmpty())
                {
                    setErrors(et_address);
                }
                else if ((!rb_user.isChecked() && !rb_driver.isChecked()))
                {
                  tv_rbRequired.setVisibility(View.VISIBLE);
                }else
                {

                    if (!pw1.equals(pw2)) {
                        et_confirmPassword.setError("Password doesn't match");
                    } else if (pw2.isEmpty()) {
                        et_confirmPassword.setError("Required Field");
                    } else {
                        progressDialog.show();
                        fireBaseAuthSignUp(et_EmailAddress.getText().toString(), et_Password.getText().toString());


                    }
                }
            }else{
                Toasty.error(SignUp.this,
                        "Please check your internet connection.", Toast.LENGTH_LONG)
                        .show();
            }




        });
    }

    public void fireBaseAuthSignUp(String email, String password)
    {
        Log.d("fireBaseAuthSignUp", "fireBaseAuthSignUp: On function fireBaseAuthSignUp");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with tcreateUserWithEmailhe signed-in user's information
                            Log.d("mFirebaseAuth", "createUserWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Log.d("authResult", "onSuccess: " + task);

                            String user_id = user.getUid();
                            signupModel.setUser_id(user_id);
                            Log.d("UserId", "onSuccess: user_id " + user_id);
                            savedDatatoFS();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("mFirebaseAuth", "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toasty.error(SignUp.this,
                                    "Failed.",Toast.LENGTH_LONG)
                                    .show();


                        }
                    }
                });


    }

    public void savedDatatoFS(){

        if(signupModel.getUser_id() != null) {
            Log.e("If", "savedDatatoFS: Success");
            signupModel.setFirstname(et_FirstName.getText().toString());
            signupModel.setLastname(et_LastName.getText().toString());
            signupModel.setUsername(et_userName.getText().toString());
            signupModel.setPhonenumber(et_phoneNumber.getText().toString());
            signupModel.setPassword(et_Password.getText().toString());
            signupModel.setAddress(et_address.getText().toString());
            signupModel.setCreated(null);


            db.collection(getString(R.string.COLLECTION_USER_INFORMATION))
                    .add(signupModel)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "onComplete: " + task.getResult().getId());
                                progressDialog.dismiss();
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();

                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Toasty.success(SignUp.this,
                                                            "Check your email for Email Verification.", Toast.LENGTH_LONG)
                                                            .show();
                                                    startActivity(new Intent(SignUp.this, MainActivity.class));
                                                    finish();
                                                }else{
                                                    Toasty.warning(SignUp.this,
                                                            "Failed to send verification email.",Toast.LENGTH_LONG)
                                                            .show();
                                                }

                                            }
                                        });



                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Exception", "onFailure: " + e);
                }
            });
        }else{
            Log.e("Else", "savedDatatoFS: Failed");
            progressDialog.dismiss();
        }

    }
    private void setErrors(EditText txt)
    {
        txt.setError("Required Field");
        txt.requestFocus();
    }
}