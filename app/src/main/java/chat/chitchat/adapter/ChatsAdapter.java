package chat.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import chat.chitchat.activity.MessageActivity;
import chat.chitchat.model.Chat;
import de.hdodenhof.circleimageview.CircleImageView;

import static chat.chitchat.helper.AppConstant.chatTableName;
import static chat.chitchat.helper.AppConstant.onlineStatusTable;
import static chat.chitchat.helper.AppConstant.profileImageTable;
import static chat.chitchat.helper.AppConstant.profileNameTable;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> users;
    private DatabaseReference mDatabaseReference;
    private String theLastMessage;

    public ChatsAdapter(Context context, ArrayList<String> users, DatabaseReference mDatabaseReference) {
        this.context = context;
        this.users = users;
        this.mDatabaseReference = mDatabaseReference;
    }

    @NonNull
    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsAdapter.ViewHolder holder, final int position) {

        mDatabaseReference.child(profileNameTable).child(users.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.userName.setText(dataSnapshot.child("userName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference.child(profileImageTable).child(users.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("imageUrl").getValue().toString().equals("default")){
                    holder.userImage.setBackgroundResource(R.drawable.ic_user);
                }else{
                    Glide.with(context).load(dataSnapshot.child("imageUrl").getValue().toString()).into(holder.userImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference.child(onlineStatusTable).child(users.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.child("status").getValue().toString().equals("online")
                            || dataSnapshot.child("status").getValue().toString().equals("typing...")) {
                        holder.status.setVisibility(View.VISIBLE);
                    } else {
                        holder.status.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lastMessage(users.get(position), holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", users.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName, lastMsg, unreadText;
        private ImageView status;
        private View unreadView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.img_usersItem);
            userName = itemView.findViewById(R.id.tv_usersItem);
            status = itemView.findViewById(R.id.img_usersItemStatus);
            lastMsg = itemView.findViewById(R.id.tv_lastMsg);
            unreadView = itemView.findViewById(R.id.view3);
            unreadText = itemView.findViewById(R.id.textView32);
        }
    }

    private void lastMessage(final String userId, final ViewHolder holder) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference.child(chatTableName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unreadMsg = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    assert firebaseUser != null;
                    try {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                                chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            if (chat.getReceiver().equals(firebaseUser.getUid())) {
                                if (chat.isIsseen()) {
                                    holder.unreadView.setVisibility(View.GONE);
                                    holder.unreadText.setVisibility(View.GONE);
                                    holder.userName.setTypeface(holder.userName.getTypeface(), Typeface.NORMAL);
                                    holder.lastMsg.setTypeface(holder.lastMsg.getTypeface(), Typeface.NORMAL);
                                } else {
                                    holder.unreadView.setVisibility(View.VISIBLE);
                                    holder.unreadText.setVisibility(View.VISIBLE);
                                    unreadMsg = (unreadMsg+1);
                                    holder.unreadText.setText(String.valueOf(unreadMsg));
                                    holder.userName.setTypeface(holder.userName.getTypeface(), Typeface.BOLD);
                                    holder.lastMsg.setTypeface(holder.lastMsg.getTypeface(), Typeface.BOLD);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                switch (theLastMessage) {
                    case "default":
                        holder.lastMsg.setText("No Message");
                        break;
                    default:
                        holder.lastMsg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
