package com.tripmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText username, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.sa_et_username);
        password = findViewById(R.id.sa_et_password);
        confirmPassword = findViewById(R.id.sa_et_confirmPassword);
        mAuth = FirebaseAuth.getInstance();
        signupButton = findViewById(R.id.sa_btn_signUp);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String userName = username.getText().toString();
                    String pass = password.getText().toString();
                    String confirmPass = confirmPassword.getText().toString();
                    if (validateFormData(userName, pass, confirmPass)) {
                        if (isConnected()) {
                            loader = ProgressDialog.show(SignUp.this, "", "Signing in...", true);
                            mAuth.createUserWithEmailAndPassword(userName, pass)
                                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUp.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignUp.this, UserProfile.class);
                                                loader.dismiss();
                                                startActivity(intent);
                                                finish();

                                            } else {
                                                loader.dismiss();
                                                Toast.makeText(SignUp.this, "Sign up process failed", Toast.LENGTH_SHORT).show();
                                                Log.d("demo", "createUserWithEmail:failure", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUp.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(SignUp.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
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

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                && networkInfo.getType() != connectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public boolean validateFormData(String username, String password, String confirmPassword) {
        if (username.equals(null) || username.equals("") || password.equals(null) || password.equals("")) {
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