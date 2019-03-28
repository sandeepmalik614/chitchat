package chat.chitchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import chat.chitchat.R;
import chat.chitchat.helper.AppUtils;

import static chat.chitchat.helper.AppConstant.mobileTableName;
import static chat.chitchat.helper.AppConstant.userTableName;
import static chat.chitchat.helper.AppPrefrences.setFirebaseUserID;
import static chat.chitchat.helper.AppPrefrences.setMobileNumber;
import static chat.chitchat.helper.AppPrefrences.setUserLoggedOut;
import static chat.chitchat.helper.AppUtils.updateFirebaseToken;

public class CompleteProfileActivity extends AppCompatActivity {

    private EditText edt_userName, edt_userStatus;
    private Button btn_finish;
    private DatabaseReference mMobileReference;
    private DatabaseReference mUserReference;
    private FirebaseUser mAuth;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        edt_userName = findViewById(R.id.editText6);
        edt_userStatus = findViewById(R.id.editText7);
        btn_finish = findViewById(R.id.button2);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mobile = getIntent().getStringExtra("mobile");

        mMobileReference = FirebaseDatabase.getInstance().getReference(mobileTableName);
        mUserReference = FirebaseDatabase.getInstance().getReference(userTableName).child(mAuth.getUid());

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_userName.getText().toString().isEmpty()) {
                    Toast.makeText(CompleteProfileActivity.this, "Please set your nick name", Toast.LENGTH_SHORT).show();
                } else {
                    if (edt_userStatus.getText().toString().isEmpty()) {
                        registerUser(edt_userName.getText().toString(), "Hi, I am using Chit chat app");
                    } else {
                        registerUser(edt_userName.getText().toString(), edt_userStatus.getText().toString());
                    }
                }
            }
        });

    }

    private void registerUser(String name, String userStatus) {
        HashMap<String, String> registerHash = new HashMap<>();
        registerHash.put("id", mAuth.getUid());
        registerHash.put("mobile", mobile);
        registerHash.put("accountCreatedDate", String.valueOf(System.currentTimeMillis()));
        AppUtils.userStatus("login");
        AppUtils.updateUserName(name);
        AppUtils.updateUserImage("default");
        AppUtils.updateUserAbout(userStatus);
        mUserReference.setValue(registerHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    enterMobile();
                } else {
                    Toast.makeText(CompleteProfileActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void enterMobile() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mobile", mobile);
        hashMap.put("status", "login");
        hashMap.put("uId", mAuth.getUid());
        mMobileReference.child(mAuth.getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateFirebaseToken(CompleteProfileActivity.this);
                    setUserLoggedOut(CompleteProfileActivity.this, false);
                    setMobileNumber(CompleteProfileActivity.this, mobile);
                    setFirebaseUserID(CompleteProfileActivity.this, mAuth.getUid());
                    Intent intent = new Intent(CompleteProfileActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(CompleteProfileActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
