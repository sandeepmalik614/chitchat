package chat.chitchat.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;
import chat.chitchat.adapter.GroupDetailsAdapter;
import chat.chitchat.helper.AppConstant;
import chat.chitchat.helper.AppUtils;
import chat.chitchat.listner.BlockClickListner;
import chat.chitchat.model.GroupDetails;
import chat.chitchat.notification.Data;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static chat.chitchat.helper.AppConstant.profileGroupImageTable;
import static chat.chitchat.helper.AppConstant.profileImageTable;
import static chat.chitchat.helper.AppConstant.uploadTableName;
import static chat.chitchat.helper.AppUtils.updateGroupImage;
import static chat.chitchat.helper.AppUtils.updateUserImage;

public class GroupProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView userImage;
    private ImageView editUserImage;
    private RelativeLayout rl_addParticipant;
    private TextView groupName, createdBy, groupDesc;
    private DatabaseReference mDatabaseReference;
    private String groupId, selectedUserId;
    private RecyclerView rv_groupDetails;
    private GroupDetailsAdapter groupDetailsAdapter;
    private FirebaseUser firebaseUser;
    private ArrayList<GroupDetails> memberIdList;
    private ArrayList<String> alreadyMamberList;
    private static final int IMAGE_REQUEST = 1;
    private StorageReference mImageStorage;

    private BlockClickListner clickListner = new BlockClickListner() {
        @Override
        public void onClick(String id) {
            selectedUserId = id;
        }
    };

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
        rl_addParticipant = findViewById(R.id.rl_addParticipant);
        rv_groupDetails.setLayoutManager(new LinearLayoutManager(this));
        memberIdList = new ArrayList<>();
        alreadyMamberList = new ArrayList<>();

        groupId = getIntent().getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        rl_addParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupProfile.this, AddGroupParticipantActivity.class);
                intent.putExtra("alreadyInGroup", alreadyMamberList);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });

        editUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfileImageDialog();
            }
        });

        getGroupInfo();
    }

    private void getGroupInfo() {
        /*getting group image*/
        mDatabaseReference.child(profileGroupImageTable).child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (userImage != null) {
                    Glide.with(GroupProfile.this).load(dataSnapshot.child("groupImageUrl")
                            .getValue()).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*getting group name*/
        mDatabaseReference.child(AppConstant.profileGroupNameTable).child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (groupName != null) {
                    groupName.setText(dataSnapshot.child("groupName").getValue().toString());
                }
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
                        if (createdBy != null) {
                            createdBy.setText("created by " + dataSnapshot.child("userName").getValue() + " on " + dateString);
                        }
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
                if (groupDesc != null) {
                    groupDesc.setText(dataSnapshot.child("groupDesc").getValue().toString());
                }
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
                        memberIdList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GroupDetails groupDetails = snapshot.getValue(GroupDetails.class);
                            if (firebaseUser.getUid().equals(groupDetails.getMemberId())) {
                                memberIdList.add(0, groupDetails);
                                if (groupDetails.isAdmin()) {
                                    rl_addParticipant.setVisibility(View.VISIBLE);
                                }
                            } else {
                                alreadyMamberList.add(groupDetails.getMemberId());
                                memberIdList.add(groupDetails);
                            }

                        }
                        groupDetailsAdapter = new GroupDetailsAdapter(GroupProfile.this, memberIdList, mDatabaseReference, firebaseUser, clickListner);
                        rv_groupDetails.setAdapter(groupDetailsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void changeProfileImageDialog() {
        CharSequence[] items = {"Gallery", "Remove profile pic"};

        AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
        builder.setTitle("Select one");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), IMAGE_REQUEST);
                } else if (pos == 1) {
                    removeProfilePic();
                }
            }
        });
        builder.show();
    }

    private void removeProfilePic() {

        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child(uploadTableName).child(groupId + ".jpg");

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(profileGroupImageTable)
                        .child(groupId);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("groupImageUrl", "default");
                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userImage.setImageDrawable(null);
                            Toast.makeText(GroupProfile.this, "Group profile pic is removed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GroupProfile.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(GroupProfile.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final StorageReference filePath = mImageStorage.child(uploadTableName)
                    .child(groupId + ".jpg");

            filePath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        String downloadUrl = downUri.toString();
                        updateGroupImage(downloadUrl, groupId);
                    }
                }
            });
        }
    }
}