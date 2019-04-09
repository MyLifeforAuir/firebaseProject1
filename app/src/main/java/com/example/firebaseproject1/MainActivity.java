package com.example.firebaseproject1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final int SIGN = 111;
    EditText id, password;
    Button signupButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButton);

        signupButton.setOnClickListener(new View.OnClickListener() {//회원가입 버튼 눌렀을시
            @Override
            public void onClick(View v) {
                id = findViewById(R.id.userID);
                password = findViewById(R.id.userPW);
                  createAccount(id.getText().toString(), password.getText().toString());
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {//로그인 버튼 눌렀을시
            @Override
            public void onClick(View v) {
                id = findViewById(R.id.userID);
                password = findViewById(R.id.userPW);
                 signIn(id.getText().toString(), password.getText().toString());
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    //    updateUI(currentUser);
    }
    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(getApplicationContext(), "login Success!",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //updateUI(user);
                            Intent intent = new Intent(getApplicationContext(), main.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });
    }


    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Authentication success!",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            // Create a new user with a first and last name
                            Map<String, Object> users = new HashMap<>();
                            users.put("Win", 0);
                            users.put("Lose", 0);
                            users.put("email", user.getEmail());

                            db.collection("users").document(user.getUid())
                                    .set(users)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("success", "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("fail", "Error writing document", e);
                                        }
                                    });


                    Intent intent = new Intent(getApplicationContext(), main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivity(intent);
                    //updateUI(user);
                }else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
