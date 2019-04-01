
package chat.chitchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddGroupParticipantActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tv_selectedCount , tv_next, tv_noFriend;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private ArrayList<String> alreadyInGroupList;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_participant);

        recyclerView = findViewById(R.id.rv_addParticipant);
        tv_selectedCount = findViewById(R.id.tv_toolbarFriendsCountAdd);
        tv_next = findViewById(R.id.tv_nextCreateGroupAdd);
        tv_noFriend = findViewById(R.id.textView47);
        toolbar = findViewById(R.id.toolbar_addParticipant);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        alreadyInGroupList = new ArrayList<>();
        mReference = FirebaseDatabase.getInstance().getReference();


    }
}
