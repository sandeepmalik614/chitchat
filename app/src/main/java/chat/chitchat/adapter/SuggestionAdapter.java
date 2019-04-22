package chat.chitchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;
import chat.chitchat.helper.AppConstant;
import chat.chitchat.helper.AppUtils;
import chat.chitchat.listner.BlockClickListner;
import chat.chitchat.listner.FriendClickListner;


public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> idList;
    private DatabaseReference mDatabaseReference;
    private FriendClickListner friendClickListner;
    private BlockClickListner sendRequestClickListner;
    private ArrayList<String> imageList = new ArrayList<>();

    public SuggestionAdapter(Context context, ArrayList<String> idList, DatabaseReference mDatabaseReference,
                             FriendClickListner friendClickListner, BlockClickListner sendRequestClickListner) {
        this.context = context;
        this.idList = idList;
        this.mDatabaseReference = mDatabaseReference;
        this.friendClickListner = friendClickListner;
        this.sendRequestClickListner = sendRequestClickListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        mDatabaseReference.child(AppConstant.profileNameTable).child(idList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.userName.setText(dataSnapshot.child("userName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference.child(AppConstant.profileImageTable).child(idList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("imageUrl").getValue().toString().equals("default")) {
                    holder.userImage.setBackgroundResource(R.drawable.ic_user_for_suggestion);
                } else {
                    Glide.with(context).load(dataSnapshot.child("imageUrl").getValue().toString()).into(holder.userImage);
                }

                imageList.add(dataSnapshot.child("imageUrl").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendClickListner != null) {
                    friendClickListner.onClick("suggestion", idList.get(position));
                }
            }
        });

        holder.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendRequestClickListner != null) {
                    sendRequestClickListner.onClick(idList.get(position));
                }
            }
        });

        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageList.get(position).equals("default")){
                    if (friendClickListner != null) {
                        friendClickListner.onClick("suggestion", idList.get(position));
                    }
                }else{
                    AppUtils.seeFullImage(context, null, imageList.get(position), holder.userImage);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return idList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private TextView userName, sendRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.img_suggestion);
            userName = itemView.findViewById(R.id.textView39);
            sendRequest = itemView.findViewById(R.id.textView33);

        }
    }
}
