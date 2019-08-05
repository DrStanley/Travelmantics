package com.stanleyj.android.travelmantics;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText editTextEm, editTextPa;
    String em, pa;
    Button login;
    TextView paswrd, regis,signup;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mdatabaseReference;
    FirebaseAuth firebaseAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Shared preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        Shared preference to get the extra data stored if user has logged in before
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean check_log = sharedPref.getBoolean("Login", false);
        String piss = sharedPref.getString("Username", null);
        editor.apply();


        getSupportActionBar().setTitle("Login Page");
        editTextEm = (EditText) findViewById(R.id.usn);
        pd = new ProgressDialog(this);
        editTextPa = (EditText) findViewById(R.id.lpwd);
        login = (Button) findViewById(R.id.log);
        signup = (TextView) findViewById(R.id.regAct);
        paswrd = (TextView) findViewById(R.id.f_pass);
        regis = (TextView) findViewById(R.id.regAct);
        firebaseAuth = FirebaseAuth.getInstance();
        if (check_log) {
            editTextEm.setText(piss);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SignupActivity.class));
            }
        });
    }

    private void LoginUser() {
        em = editTextEm.getText().toString();
        pa = editTextPa.getText().toString();
        if (TextUtils.isEmpty(em)) {
            //email is empty
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
            return;

        }
        if (TextUtils.isEmpty(pa)) {
            //passwordc is empty
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        pd.setMessage("Logging User in...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);


        firebaseAuth.signInWithEmailAndPassword(em, pa).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
//                    Toast.makeText(LoginActivity.this, "Login Successfull !!!", Toast.LENGTH_SHORT).show();
//                    Shared preference
                    editTextPa.setText(null);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        Shared preference to get the extra data stored;
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Login", true);
                    editor.putString("Username", em);
                    editor.apply();
                    pd.dismiss();
                    startActivity(new Intent(MainActivity.this, DealActivity.class));

                } else {
                    pd.dismiss();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                    alertDialog.setMessage("Couldn't Login User \n"
                            + task.getException().getMessage());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }

}
