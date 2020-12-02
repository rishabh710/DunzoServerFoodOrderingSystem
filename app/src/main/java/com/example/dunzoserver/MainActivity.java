package com.example.dunzoserver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dunzoserver.Common.Common;
import com.example.dunzoserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn;

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

        setContentView(R.layout.activity_main);

        btnSignIn=(Button)findViewById(R.id.btnSignIn);


        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn= new Intent(MainActivity.this,SignIn.class);
                startActivity(signIn);
            }
        });

        String user= Paper.book().read(Common.USER_KEY);
        String pwd= Paper.book().read(Common.PWD_KEY);
        if (user!=null && pwd != null){
            if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pwd)){
                login(user,pwd);
            }
        }
    }

    private void login(final String phone, final String pwd) {

        final FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())){

            final ProgressDialog mDialog= new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //check if user not exist in database
                    if (dataSnapshot.child(phone).exists()) {
                        //Get user information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)) {
                            Intent homeIntent= new Intent(MainActivity.this,Home.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Common.currentUser=user;
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password !!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }
}