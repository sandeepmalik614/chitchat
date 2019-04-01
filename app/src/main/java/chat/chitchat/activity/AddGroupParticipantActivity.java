
package chat.chitchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;
import chat.chitchat.adapter.AddParticipantAdapter;
import chat.chitchat.listner.FriendClickListner;
import chat.chitchat.model.GroupDetails;
import chat.chitchat.model.ItemSelectedInGroup;
import chat.chitchat.model.ParticipantList;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static chat.chitchat.helper.AppConstant.userFriendListTableName;

public class AddGroupParticipantActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tv_selectedCount , tv_next, tv_noFriend;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private ArrayList<ItemSelectedInGroup> friendIdList;
    private ArrayList<String> alreadyInGroup;
    private DatabaseReference mReference;
    private AddParticipantAdapter participantAdapter;
    private FirebaseUser firebaseUser;

    private FriendClickListner friendClickListner = new FriendClickListner() {
        @Override
        public void onClick(String type, String id) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_participant);

        recyclerView = findViewById(R.id.rv_addParticipant);
        tv_selectedCount = findViewById(R.id.tv_toolbarFriendsCountAdd);
        tv_next = findViewById(R.id.tv_nextCreateGroupAdd);
        tv_noFriend = findViewById(R.id.textView47);
        toolbar = findViewById(R.id.toolbar_addParticipant);
        progressBar = findViewById(R.id.progressBar4);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        alreadyInGroup = (ArrayList<String>) getIntent().getSerializableExtra("alreadyInGroup");
        friendIdList = new ArrayList<>();
        mReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getFriendsList();
    }

    private void getFriendsList() {
        mReference.child(userFriendListTableName).child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                friendIdList.clear();
                if(dataSnapshot.getChildren() != null){
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_noFriend.setVisibility(View.GONE);
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ItemSelectedInGroup selectedInGroup = new ItemSelectedInGroup();
                        selectedInGroup.setId(snapshot.child("friend_id").getValue().toString());
                        selectedInGroup.setSelected(false);
                        friendIdList.add(selectedInGroup);
                    }
                    participantAdapter = new AddParticipantAdapter(AddGroupParticipantActivity.this, mReference,
                            alreadyInGroup, friendIdList, friendClickListner);
                    recyclerView.setAdapter(participantAdapter);

                }else{
                    recyclerView.setVisibility(View.GONE);
                    tv_noFriend.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
