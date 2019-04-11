package chat.chitchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import chat.chitchat.R;
import chat.chitchat.model.ParticipantList;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static chat.chitchat.helper.AppConstant.groupMemberTable;
import static chat.chitchat.helper.AppConstant.profileNameTable;
import static chat.chitchat.helper.AppConstant.userTableName;

public class GroupMessageActvity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView groupName, groupStatus;
    private ImageView groupInfo;
    private String groupId;
    private ArrayList<String> mamberList;
    private CircleImageView img_toolbarGroup;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message_actvity);

        toolbar = findViewById(R.id.toolbarGroupMessage);
        groupName = findViewById(R.id.tv_groupName);
        groupStatus = findViewById(R.id.tv_groupStatus);
        groupInfo = findViewById(R.id.img_groupInfo);
        img_toolbarGroup = findViewById(R.id.img_toolbarGroup);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mamberList = new ArrayList<>();
        groupId = getIntent().getStringExtra("groupId");
        groupName.setText(getIntent().getStringExtra("groupName"));
        Glide.with(this).load(getIntent().getStringExtra("groupImage")).into(img_toolbarGroup);

        groupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMessageActvity.this, GroupProfile.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });
        getGroupInfo();

    }

    private void getGroupInfo() {
        mDatabaseReference.child(groupMemberTable).child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mamberList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mDatabaseReference.child(profileNameTable).child(snapshot.getKey())
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mamberList.add(dataSnapshot.child("userName").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
