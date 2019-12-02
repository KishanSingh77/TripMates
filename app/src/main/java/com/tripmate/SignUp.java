package com.tripmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUp extends AppCompatActivity {
    private Button signupButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    EditText username, password, confirmPassword,firstName,lastName;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView image;
    Bitmap bitmapUpload = null;
    boolean imageCaptured = false;

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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoIntent();
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
                    if (validateFormData(userName, pass, confirmPass,first_name,last_name)) {
                        if (isConnected()) {
                            loader = ProgressDialog.show(SignUp.this, "", "Logging IN", true);
                            mAuth.createUserWithEmailAndPassword(userName, pass)
                                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUp.this, "User created YO", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SignUp.this, "failture", Toast.LENGTH_SHORT).show();
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

    public boolean validateFormData(String username, String password, String confirmPassword,String first_name,String last_name) {
        if (first_name.equals(null) || first_name.equals("") || last_name.equals(null) || last_name.equals("")) {
            Toast.makeText(SignUp.this, "Form fields cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (username.equals(null) || username.equals("") || password.equals(null) || password.equals("")) {
            Toast.makeText(SignUp.this, "Form fields cannot be blank", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(password.length() < 6) {
            Toast.makeText(SignUp.this, "Password must be atleast 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (confirmPassword.equals(null) || confirmPassword.equals("") || !confirmPassword.equals(password)) {
            Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
