package com.tripmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class SignUp extends AppCompatActivity {
    private Button signupButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    EditText username, password, confirmPassword,firstName,lastName;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView image;
    Bitmap bitmapUpload = null;
    boolean imageCaptured = false;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String gender = null;
    public static String userID = null;
    RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.confirm_Password);
        mAuth = FirebaseAuth.getInstance();
        signupButton = findViewById(R.id.btn_signUp);
        image=findViewById(R.id.imageView2);
        firstName=findViewById(R.id.editTextFirstName);
        lastName=findViewById(R.id.editTextLastName);
        rg=findViewById(R.id.radioGroup);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoIntent();
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.add_edit_view_rb_male:
                        gender = "MALE";
                        break;
                    case R.id.add_edit_view_rb_female:
                        gender = "FEMALE";
                        break;
                }
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String userName = username.getText().toString();
                    String pass = password.getText().toString();
                    String confirmPass = confirmPassword.getText().toString();
                    String first_name=firstName.getText().toString();
                    String last_name=lastName.getText().toString();

                    Log.d("demo", "after rg"+gender);
                    if (validateFormData(userName, pass, confirmPass,first_name,last_name,gender,imageCaptured)) {
                        Log.d("demo", "after validate");
                        if (isConnected()) {
                            loader = ProgressDialog.show(SignUp.this, "", "Logging IN", true);
                            mAuth.createUserWithEmailAndPassword(userName, pass)
                                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("demo", "task success");
                                                Toast.makeText(SignUp.this, "User created YO", Toast.LENGTH_SHORT).show();
                                                onSubmitClick();
                                                Log.d("demo", "after click");
                                                Intent intent = new Intent(SignUp.this, DashBoardActivity.class);
                                                loader.dismiss();
                                                startActivity(intent);
                                                finish();

                                            } else {
                                                loader.dismiss();
                                                Toast.makeText(SignUp.this, "Sign up process failed", Toast.LENGTH_SHORT).show();
                                                Log.d("demo", " failure", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUp.this, "XXXXXXXXXXXX no connection  XXXXXX", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(SignUp.this, "failure"+ex, Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(SignUp.this, UserProfile.class);
            startActivity(intent);
            finish();
        }

    }

    private void takePhotoIntent() {
        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photo.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(photo, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
            bitmapUpload = imageBitmap;
            imageCaptured = true;
        }

    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                && networkInfo.getType() != connectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public boolean validateFormData(String username, String password, String confirmPassword,String first_name,String last_name,String gender,boolean imageCaptured) {
        if (first_name.equals(null) || first_name.equals("") || last_name.equals(null) || last_name.equals("")) {
            Toast.makeText(SignUp.this, "Enter first name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (username.equals(null) || username.equals("") || password.equals(null) || password.equals("")) {
            Toast.makeText(SignUp.this, "Enter last name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(password.length() < 6) {
            Toast.makeText(SignUp.this, "Password must be atleast 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (confirmPassword.equals(null) || confirmPassword.equals("") || !confirmPassword.equals(password)) {
            Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(gender.equals(null)|| gender.equals("") ){
            Toast.makeText(SignUp.this, "Select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!imageCaptured){
            Toast.makeText(SignUp.this, "Choose a pic", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    private void storeUserProfileToServer(@Nullable Uri uri) {
        try {
            userID = mAuth.getCurrentUser().getUid();
            UserProfile user = new UserProfile();
            user.setFirstName(firstName.getText().toString());
            user.setLastName(lastName.getText().toString());
            user.setUserGender(gender);
            user.setImageUrl(uri == null ? null : uri.toString());
            user.setUserUID(userID);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName.getText().toString() + " " + lastName.getText().toString())
                    .setPhotoUri(uri)
                    .build();

            mAuth.getCurrentUser().updateProfile(profileUpdates);
            db.collection("Users").document(userID).set(user);
            loader.dismiss();
            Intent intent = new Intent(SignUp.this, DashBoardActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception ex) {
            int t = 0;
        }
    }

    private void onSubmitClick() {
        if (isConnected()) {
            loader = ProgressDialog.show(SignUp.this, "", "Saving user information ...", true);
            if (imageCaptured) {
                uploadImage(getBitmapCamera(), userID);
            } else {
                storeUserProfileToServer(null);
            }
        } else {
            Toast.makeText(SignUp.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImage(Bitmap photoBitmap, final String UID) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        String path = "UserImages/" + UID + ".png";
        final StorageReference storageReference = firebaseStorage.getReference(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    storeUserProfileToServer(task.getResult());
                }
            }
        });
    }

    private Bitmap getBitmapCamera() {
        if (bitmapUpload == null) {
            return ((BitmapDrawable) image.getDrawable()).getBitmap();
        }
        return bitmapUpload;
    }
}
