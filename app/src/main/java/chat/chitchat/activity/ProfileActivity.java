package chat.chitchat.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import chat.chitchat.R;
import chat.chitchat.helper.AppPrefrences;
import de.hdodenhof.circleimageview.CircleImageView;

import static chat.chitchat.helper.AppConstant.profileAboutTable;
import static chat.chitchat.helper.AppConstant.profileImageTable;
import static chat.chitchat.helper.AppConstant.profileNameTable;
import static chat.chitchat.helper.AppConstant.reportTableName;
import static chat.chitchat.helper.AppConstant.uploadTableName;
import static chat.chitchat.helper.AppConstant.userTableName;
import static chat.chitchat.helper.AppUtils.getMyPrettyDate;
import static chat.chitchat.helper.AppUtils.updateUserImage;
import static chat.chitchat.helper.AppUtils.userStatus;


public class ProfileActivity extends AppCompatActivity {

    private CircleImageView userImage;
    private ImageView editImage, reportIcon;
    private TextView tv_nameProfile, tv_status, tv_mobile, tv_report;
    private DatabaseReference mUserNameReference;
    private DatabaseReference mUserImageReference;
    private DatabaseReference mUserStatusReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mReportReference;
    private FirebaseUser firebaseUser;
    private StorageReference mImageStorage;
    private static final int IMAGE_REQUEST = 1;
    private ProgressDialog pd;
    private Toolbar toolbar;
    private boolean isProfileImage = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.circularImageView);
        tv_nameProfile = findViewById(R.id.tv_nameProfile);
        tv_status = findViewById(R.id.textView4);
        tv_mobile = findViewById(R.id.textView5);
        toolbar = findViewById(R.id.toolbar_profile);
        tv_report = findViewById(R.id.textView27);
        editImage = findViewById(R.id.imageView5);
        reportIcon = findViewById(R.id.imageView7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserNameReference = FirebaseDatabase.getInstance().getReference(profileNameTable);
        mUserImageReference = FirebaseDatabase.getInstance().getReference(profileImageTable);
        mUserStatusReference = FirebaseDatabase.getInstance().getReference(profileAboutTable);
        mUserReference = FirebaseDatabase.getInstance().getReference(userTableName);
        mReportReference = FirebaseDatabase.getInstance().getReference(reportTableName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.setCanceledOnTouchOutside(false);
        getUserProfile();
        getReport();

        tv_nameProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEdit("userName", tv_nameProfile.getText().toString());
            }
        });

        tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEdit("userStatus", tv_status.getText().toString());
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence[] items = {"Gallery", "Remove profile pic"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Select one");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        if (pos == 0) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), IMAGE_REQUEST);
                        }else if(pos == 1){
                            removeProfilePic();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void getUserProfile() {
        mUserReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_mobile.setText(dataSnapshot.child("mobile").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserNameReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_nameProfile.setText(dataSnapshot.child("userName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserImageReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(getApplicationContext()).load(dataSnapshot.child("imageUrl").getValue().toString())
                        .into(userImage);
                AppPrefrences.setUserImage(ProfileActivity.this, dataSnapshot.child("imageUrl").getValue().toString());
                if (dataSnapshot.child("imageUrl").getValue().toString().equals("default")) {
                    isProfileImage = false;
                } else {
                    isProfileImage = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserStatusReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_status.setText(dataSnapshot.child("userStatus").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserReference.keepSynced(true);
    }

    private void getReport() {
        mReportReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> reportList = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        reportList.add(snapshot.getKey());
                    }

                    if (reportList.size() == 0) {
                        tv_report.setText("No spam report against you");
                    } else {
                        tv_report.setVisibility(View.VISIBLE);
                        reportIcon.setVisibility(View.VISIBLE);
                        tv_report.setText(reportList.size() + " person reported as spam");
                    }
                } else {
                    tv_report.setText("No spam report against you");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeProfilePic() {

        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child(uploadTableName).child(firebaseUser.getUid() + ".jpg");

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(profileImageTable)
                        .child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageUrl", "default");
                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userImage.setImageDrawable(null);
                            isProfileImage = false;
                            Toast.makeText(ProfileActivity.this, "Profile pic is removed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            isProfileImage = true;
                            Toast.makeText(ProfileActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileActivity.this, "Oops something went wrong, Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goToEdit(String key, String value) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("value", value);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            pd.show();
            Uri imageUri = data.getData();
            final StorageReference filePath = mImageStorage.child(uploadTableName)
                    .child(firebaseUser.getUid() + ".jpg");

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
                        updateUserImage(downloadUrl);
                        pd.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
