package chat.chitchat.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;
import chat.chitchat.adapter.ChatsAdapter;
import chat.chitchat.model.ChatList;

import static chat.chitchat.helper.AppConstant.chatListTableName;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatsAdapter adapter;
    private ArrayList<ChatList> userList;
    private FirebaseUser firebaseUser;
    private DatabaseReference mChatReference;
    private DatabaseReference mDatabaseReference;
    private TextView tv_noChat;
    private View view;
    private ProgressBar pb_chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        return view;
    }

    private void initView() {
        recyclerView = view.findViewById(R.id.rv_chats);
        tv_noChat = view.findViewById(R.id.tv_noChat);
        pb_chatList = view.findViewById(R.id.pb_chatList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mChatReference = FirebaseDatabase.getInstance().getReference(chatListTableName)
                .child(firebaseUser.getUid());
        Query query = mChatReference.orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                pb_chatList.setVisibility(View.GONE);
                ArrayList<String> idList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    if (!userList.contains(chatList.getId())) {
                        userList.add(chatList);
                        idList.add(chatList.getId());
                    }
                }

                if(idList.size() != 0) {
                    tv_noChat.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Collections.reverse(idList);
                    adapter = new ChatsAdapter(getContext(), idList, mDatabaseReference);
                    recyclerView.setAdapter(adapter);
                }else{
                    tv_noChat.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            }, 20);
        }
    }
}
