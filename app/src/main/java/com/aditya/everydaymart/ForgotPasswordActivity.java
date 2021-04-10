package com.aditya.everydaymart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView backBtn;
    private EditText emailEt;
    private Button ResetBtn;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        backBtn= findViewById(R.id.backBtn);
        emailEt= findViewById(R.id.emailEt);
        ResetBtn= findViewById(R.id.ResetBtn);

        firebaseAuth =FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPassword();

            }
        });
    }
    private String email;
    private void recoverPassword() {
            email= emailEt.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                Toast.makeText(this, "Invalid Email...", Toast.LENGTH_SHORT).show();
                return;
            }
             progressDialog.setMessage("Reseting Password");
            progressDialog.show();

            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, "Reset Pass sent on your mail...", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, ""+e.getMessage() , Toast.LENGTH_SHORT).show();

                }
            });
        }
}