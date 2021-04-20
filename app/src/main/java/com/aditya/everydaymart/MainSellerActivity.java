package com.aditya.everydaymart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {
    private TextView nameTv,shopNameTv,emailTv;
    private ImageButton logoutBtn,editProfileBtn,addProductBtn;
    private  ImageView profileIv;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        nameTv=findViewById(R.id.nameTv);
        logoutBtn=findViewById(R.id.logoutBtn);
        editProfileBtn=findViewById(R.id.editProfileBtn);
        shopNameTv=findViewById(R.id.shopNameTv);
        emailTv=findViewById(R.id.emailTv);
        addProductBtn=findViewById(R.id.addProductBtn);
        profileIv=findViewById(R.id.profileIv);


        progressDialog= new ProgressDialog(this );
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();

            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to open edit profile
                startActivity(new Intent(MainSellerActivity.this,ProfileEditSellerActivity.class));
            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this,AddProductActivity.class));

            }
        });

    }

    private void makeMeOffline() {
        //after loging in,make user online
        progressDialog.setMessage("Loging out...");
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("online","false");
        //update value to db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //on successful update
                firebaseAuth.signOut();
                checkUser();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // on Unsuceess update

                        progressDialog.dismiss();
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void checkUser() {
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user==null)
        {
            startActivity(new Intent(MainSellerActivity.this,LoginActivity.class));
            finish();
        }
        else
        {
            loadMyInfo();

        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    //get data from db
                    String name =""+ds.child("name").getValue();
                    String accountType =""+ds.child("accountType").getValue();
                    String email =""+ds.child("accountType").getValue();
                    String shopName =""+ds.child("shopName").getValue();
                    String profileImage =""+ds.child("profileImage").getValue();

                    //set data to ui
                    nameTv.setText(name);
                    shopNameTv.setText(shopName);
                    emailTv.setText(email);
                    try {
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_grey).into(profileIv);
                    }
                    catch (Exception e)
                    {
                        profileIv.setImageResource(R.drawable.ic_store_grey);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}