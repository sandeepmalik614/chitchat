package chat.chitchat.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;
import chat.chitchat.adapter.GroupDetailsAdapter;
import chat.chitchat.helper.AppConstant;
import chat.chitchat.helper.AppUtils;
import chat.chitchat.model.GroupDetails;
import chat.chitchat.notification.Data;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView userImage;
    private ImageView editUserImage;
    private TextView groupName, createdBy, groupDesc;
    private DatabaseReference mDatabaseReference;
    private String groupId;
    private RecyclerView rv_groupDetails;
    private GroupDetailsAdapter groupDetailsAdapter;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userImage = findViewById(R.id.circleImageView3);
        editUserImage = findViewById(R.id.imageView10);
        groupName = findViewById(R.id.textView13);
        createdBy = findViewById(R.id.textView36);
        groupDesc = findViewById(R.id.textView42);
        rv_groupDetails = findViewById(R.id.rv_groupDetails);
        rv_groupDetails.setLayoutManager(new LinearLayoutManager(this));

        groupId = getIntent().getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        getGroupInfo();
    }

    private void getGroupInfo(){
        /*getting group image*/
        mDatabaseReference.child(AppConstant.profileGroupImageTable).child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(GroupProfile.this).load(dataSnapshot.child("groupImageUrl")
                        .getValue()).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*getting group name*/
        mDatabaseReference.child(AppConstant.profileGroupNameTable).child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               groupName.setText(dataSnapshot.child("groupName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*getting group created by*/
        mDatabaseReference.child(AppConstant.groupTableName).child(groupId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                long timeInMilli = Long.parseLong(dataSnapshot.child("groupCreateDate").getValue().toString());
                final String dateString = formatter.format(new Date(timeInMilli));

                /*getting user name*/
                mDatabaseReference.child(AppConstant.profileNameTable).child(dataSnapshot.child("createdBy")
                        .getValue().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        createdBy.setText("created by "+dataSnapshot.child("userName").getValue()+" on "+dateString);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*getting description*/
        mDatabaseReference.child(AppConstant.profileGroupDescTable).child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupDesc.setText(dataSnapshot.child("groupDesc").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*getting memeber List*/
        mDatabaseReference.child(AppConstant.profileGroupMemberTable).child(groupId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<GroupDetails> idList = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupDetails groupDetails = snapshot.getValue(GroupDetails.class);
                    if(firebaseUser.getUid().equals(groupDetails.getMemberId())){
                        idList.add(0, groupDetails);
                    }else{
                        idList.add(groupDetails);
                    }

                }

                groupDetailsAdapter = new GroupDetailsAdapter(GroupProfile.this, idList, mDatabaseReference, firebaseUser);
                rv_groupDetails.setAdapter(groupDetailsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
