package chat.chitchat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import chat.chitchat.R;

import static chat.chitchat.helper.AppUtils.getMyPrettyDate;
import static chat.chitchat.helper.AppUtils.updateUserAbout;
import static chat.chitchat.helper.AppUtils.updateUserName;
import static chat.chitchat.helper.AppUtils.userStatus;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.btn_EditUpdate);
        toolbar = findViewById(R.id.toolbar_edit);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final String key = getIntent().getStringExtra("key");
        String value = getIntent().getStringExtra("value");


        editText.setText(value);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "This field is required", Toast.LENGTH_SHORT).show();
                } else {
                    updateUserProfile(key, editText.getText().toString());
                }
            }
        });
    }

    private void updateUserProfile(String key, String value) {
        if(key.equals("userName")){
            updateUserName(value);
        }else{
            updateUserAbout(value);
        }
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onStart() {
        userStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        userStatus(String.valueOf(System.currentTimeMillis()));
        super.onPause();
    }
}
