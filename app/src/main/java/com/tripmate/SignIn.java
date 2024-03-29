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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignIn extends AppCompatActivity {
    public static FirebaseAuth mAuth;
    public static GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 7777;
    private Button loginButton;
    private TextView username;
    private TextView password;
    private TextView signup;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.btn_login_activity_main);
        username = findViewById(R.id.la_et_user_name);
        password = findViewById(R.id.la_et_password);
        signup = findViewById(R.id.btn_signUp);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(SignIn.this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setText("");
                password.setText("");
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String userName = username.getText().toString().trim();
                    String pass = password.getText().toString().trim();
                    if (validateFormData(userName, pass)) {
                        if (isConnected()) {
                            loader = ProgressDialog.show(SignIn.this, "", "Signing in...", true);
                            mAuth.signInWithEmailAndPassword(userName, pass)
                                    .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(SignIn.this, DashBoardActivity.class);
                                                startActivity(intent);
                                                loader.dismiss();
                                                finish();
                                            } else {
                                                loader.dismiss();
                                                Log.d("demo", "signInWithEmail:failure", task.getException());
                                                Toast.makeText(SignIn.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignIn.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SignIn.this, "Fields cannot be blank", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception ex){
                    Toast.makeText(SignIn.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateFormData(String username, String password) {
        if (username.equals(null) || username.equals("")) {
            return false;
        } else if (password.equals(null) || password.equals("")) {
            return false;
        } else {
            return true;
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        if (isConnected()) {
            loader = ProgressDialog.show(SignIn.this, "", "Signing in...", true);
            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("demo", "signInWithCredential:success");
                            Intent intent = new Intent(SignIn.this, UserActivity.class);
                            startActivity(intent);
                            loader.dismiss();
                            finish();
                        } else {
                            loader.dismiss();
                            Log.w("demo", "sign in failure", task.getException());
                            Toast.makeText(SignIn.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                loader.dismiss();
                Log.w("demo", "failed sign in" + e.toString());
                Toast.makeText(SignIn.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignIn.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
