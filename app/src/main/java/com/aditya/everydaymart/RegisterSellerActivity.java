package com.aditya.everydaymart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class RegisterSellerActivity extends AppCompatActivity implements LocationListener {

    private ImageButton backBtn, gpsBtn;
    private ImageView profileIv;
    private EditText nameEt, ShopNameEt, phoneEt, deliveryFee, countryEt, stateEt, cityEt, addressEt, emailEt, passwordEt, ConfirmpasswordEt;
    private Button registerBtn;

    //    permission constant
    private static final int Location_Request_code = 100;
    private static final int Camera_Request_code = 200;
    private static final int Storage_Request_code = 300;


    private static final int Image_Pick_Gallery_code = 400;
    private static final int Image_Pick_Camera_code = 500;


    //    permission array
    private String[] LocationPermission;
    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri image_uri;

    private double latitude = 0.0, longitude = 0.0;

    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);


        backBtn = findViewById(R.id.backBtn);
        gpsBtn = findViewById(R.id.gpsBtn);
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        ShopNameEt = findViewById(R.id.ShopNameEt);
        phoneEt = findViewById(R.id.phoneEt);
        deliveryFee = findViewById(R.id.deliveryFee);
        countryEt = findViewById(R.id.countryEt);
        stateEt = findViewById(R.id.stateEt);
        cityEt = findViewById(R.id.cityEt);
        addressEt = findViewById(R.id.addressEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        ConfirmpasswordEt = findViewById(R.id.ConfirmpasswordEt);
        registerBtn = findViewById(R.id.registerBtn);

        LocationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkLocationPermission()) {
                    detectLocation();
                } else
                    requestLocationPermission();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDilog();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();

            }
        });

    }

    private String fullName, shopName, phoneNumber, country, state, city, address, email, password, confirmPassword, deliveryfee;

    private void inputData() {
        fullName = nameEt.getText().toString().trim();
        shopName = ShopNameEt.getText().toString().trim();
        phoneNumber = phoneEt.getText().toString().trim();
        country = countryEt.getText().toString().trim();
        city = cityEt.getText().toString().trim();
        address = addressEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        confirmPassword = ConfirmpasswordEt.getText().toString().trim();
        deliveryfee = deliveryFee.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Enter Full Name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(shopName)) {
            Toast.makeText(this, "Enter Shop Name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Enter Phone Number ", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(deliveryfee)) {
            Toast.makeText(this, "Enter Delivery Fee", Toast.LENGTH_SHORT).show();
        }
        if (latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(this, "Press GPS Button To Detect Location", Toast.LENGTH_SHORT).show();

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password Must Be Greater Than 6", Toast.LENGTH_SHORT).show();
        }
        if (password.equals(confirmPassword)) {
            Toast.makeText(this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
        }

    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account......");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saverFirebaseData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Saving Account Info...");

        final String timestamp = "" + System.currentTimeMillis();
        if (image_uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid", "" + firebaseAuth.getUid());
            hashMap.put("email", "" + email);
            hashMap.put("name", "" + fullName);
            hashMap.put("shopName", "" + shopName);
            hashMap.put("phone", "" + phoneNumber);
            hashMap.put("deliveryFee", "" + deliveryfee);
            hashMap.put("country", "" + country);
            hashMap.put("state", "" + state);
            hashMap.put("city", "" + city);
            hashMap.put("address", "" + address);
            hashMap.put("latitude", "" + latitude);
            hashMap.put("longitude", "" + longitude);
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("accountType", "Seller");
            hashMap.put("online", "true");
            hashMap.put("shopOpen", "true");
            hashMap.put("profileImage", "");

            //DataBase Saving
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                    finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                            finish();
                        }
                    });
        } else {
            //save image

            //save image path &name
            String filePathAndName = "profile_images/" + "" + firebaseAuth.getUid();
            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //get url of uploaded image
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) {
                        Uri downloadImageUri = uriTask.getResult();
                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", "" + firebaseAuth.getUid());
                            hashMap.put("email", "" + email);
                            hashMap.put("name", "" + fullName);
                            hashMap.put("shopName", "" + shopName);
                            hashMap.put("phone", "" + phoneNumber);
                            hashMap.put("deliveryFee", "" + deliveryfee);
                            hashMap.put("country", "" + country);
                            hashMap.put("state", "" + state);
                            hashMap.put("city", "" + city);
                            hashMap.put("address", "" + address);
                            hashMap.put("latitude", "" + latitude);
                            hashMap.put("longitude", "" + longitude);
                            hashMap.put("timestamp", "" + timestamp);
                            hashMap.put("accountType", "Seller");
                            hashMap.put("online", "true");
                            hashMap.put("shopOpen", "true");
                            hashMap.put("profileImage", downloadImageUri);// url of uploaded images


                            //DataBase Saving
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(firebaseAuth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                                    finish();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                                            finish();
                                        }
                                    });

                        }
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void showImageDilog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0) {
                    //camera clicked
                    if (checkCameraPermission()) {
                        pickFromCamera();
                    } else {
                        requestCameraPermissions();
                    }
                } else {
                    // gallery clicked
                    if (checkStoragePermission()) {
                        pickFromGallery();
                    } else {
                        requestStoragePermissions();
                    }
                }

            }
        }).show();

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Image_Pick_Gallery_code);
    }


    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, Image_Pick_Camera_code);

    }


    private void detectLocation() {
        Toast.makeText(this, "fetching......", Toast.LENGTH_LONG).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    private boolean checkLocationPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);
    return result;

    }

private void requestLocationPermission()
{
    ActivityCompat.requestPermissions(this,LocationPermission,Location_Request_code);
}

private boolean checkStoragePermission()
{
    boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ==(PackageManager.PERMISSION_GRANTED);

    return result;
}

private void requestStoragePermissions()
{
    ActivityCompat.requestPermissions(this,storagePermission,Storage_Request_code);
}

    private boolean checkCameraPermission()
    {
        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);

        boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermissions()
    {
        ActivityCompat.requestPermissions(this, cameraPermission, Camera_Request_code);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude =location.getLatitude();
        longitude= location.getLongitude();
        
        findAddress();
    }

    private void findAddress() {
    //find address ,city,state,country
        Geocoder geocoder;
        List<Address> addresses;
        geocoder =new  Geocoder(this, Locale.getDefault());

        try{
            addresses=geocoder.getFromLocation(latitude,longitude,1);

            String address=addresses.get(0).getAddressLine(0);
            String city=addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();
            String country=addresses.get(0).getCountryName();

            addressEt.setText(address);
            countryEt.setText(country);
            stateEt.setText(state);
            cityEt.setText(city);

        }
        catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Enable Location", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    switch (requestCode)
    {
        case Location_Request_code:
            {
                if(grantResults.length>0)
                {
                    boolean LocationAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(LocationAccepted)
                    {
                        detectLocation();
                    }
                    else
                    {
                        Toast.makeText(this, "Allow Location Permission", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        case Camera_Request_code:
        {
            if(grantResults.length>0)
            {
                boolean CameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean StorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if(CameraAccepted && StorageAccepted)
                {
                    pickFromCamera();
                }
                else
                {
                    Toast.makeText(this, "Allow Camera Permission", Toast.LENGTH_SHORT).show();
                }
            }

        }
        break;
        case Storage_Request_code:
        {
            if(grantResults.length>0)
            {
                boolean StorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(StorageAccepted)
                {
                 pickFromGallery();
                }
                else
                {
                    Toast.makeText(this, "Storage Camera Permission", Toast.LENGTH_SHORT).show();
                }
            }

        }
        break;
    }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if(resultCode==RESULT_OK)
    {
        if(requestCode==Image_Pick_Gallery_code)
        {
            image_uri=data.getData();
            profileIv.setImageURI(image_uri);
        }
        else if(requestCode==Image_Pick_Camera_code)
        {

            profileIv.setImageURI(image_uri);
        }
    }

    super.onActivityResult(requestCode, resultCode, data);
    }
}