package com.example.dunzoserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dunzoserver.Common.Common;
import com.example.dunzoserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignIn;
    CheckBox ckbRemember;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/UbuntuMedium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_sign_in);

        edtPhone=(MaterialEditText)findViewById(R.id.edtPhone);
        edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);
        ckbRemember=(CheckBox)findViewById(R.id.ckbRemember);

        db=FirebaseDatabase.getInstance();
        users=db.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    //For automatic login
                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());

                    }
                    signInUser(edtPhone.getText().toString(), edtPassword.getText().toString());
                }else{
                    Toast.makeText(SignIn.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signInUser(final String phone, String password) {
        final ProgressDialog mDialog= new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please wait");
        mDialog.show();

        final String localPhone= phone;
        final String localPassword= password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()){
                    mDialog.dismiss();
                    User user=dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff())){
                        if (user.getPassword().equals(localPassword)){
                            Intent login= new Intent(SignIn.this,Home.class);
                            Common.currentUser=user;
                            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(login);


                        }else
                            Toast.makeText(SignIn.this, "Wrong Password !!", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(SignIn.this, "Please Login with Staff Account", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SignIn.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignIn.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}