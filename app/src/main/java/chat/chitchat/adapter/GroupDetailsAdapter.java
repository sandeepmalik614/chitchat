package chat.chitchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;
import chat.chitchat.helper.AppConstant;
import chat.chitchat.model.GroupDetails;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupDetailsAdapter extends RecyclerView.Adapter<GroupDetailsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<GroupDetails> groupDetails;
    private DatabaseReference mReference;
    private FirebaseUser firebaseUser;

    public GroupDetailsAdapter(Context context, ArrayList<GroupDetails> groupDetails, DatabaseReference mReference,
                               FirebaseUser firebaseUser) {
        this.context = context;
        this.groupDetails = groupDetails;
        this.mReference = mReference;
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        getUserImage(groupDetails.get(position).getMemberId(), holder);
        getUserName(groupDetails.get(position).getMemberId(), holder);
        getUserAbout(groupDetails.get(position).getMemberId(), holder);

        if(groupDetails.get(position).isAdmin()){
            holder.userAdmin.setVisibility(View.VISIBLE);
        }else{
            holder.userAdmin.setVisibility(View.GONE);
        }
    }

    private void getUserAbout(String memberId, final ViewHolder holder) {
        mReference.child(AppConstant.profileAboutTable).child(memberId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.userStatus.setText(dataSnapshot.child("userStatus").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserName(String memberId, final ViewHolder holder) {
        if(firebaseUser.getUid().equals(memberId)){
            holder.userName.setText("You");
        }else{
            mReference.child(AppConstant.profileNameTable).child(memberId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.userName.setText(dataSnapshot.child("userName").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void getUserImage(String memberId, final ViewHolder holder) {
        mReference.child(AppConstant.profileImageTable).child(memberId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Glide.with(context).load(dataSnapshot.child("imageUrl").getValue().toString()).into(holder.userImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return groupDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName, userStatus, userAdmin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.img_groupDetails);
            userName = itemView.findViewById(R.id.textView44);
            userStatus = itemView.findViewById(R.id.textView45);
            userAdmin = itemView.findViewById(R.id.textView46);

        }
    }
}
