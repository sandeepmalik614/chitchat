package chat.chitchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;
import chat.chitchat.model.Chat;

import static chat.chitchat.helper.AppUtils.getMyPrettyOnlyDate;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<Chat> mChat;
    private FirebaseUser fuser;

    public MessageAdapter(Context context, ArrayList<Chat> chats) {
        this.context = context;
        this.mChat = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, viewGroup, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.message.setText(chat.getMessage());
        holder.time.setText(chat.getTime());

        holder.tv_chatHeader.setText(getMyPrettyOnlyDate(Long.parseLong(chat.getMessageDate())));
        if (position > 0) {
            if (chat.getMessageDate().equalsIgnoreCase(mChat.get(position - 1).getMessageDate())) {
                holder.tv_chatHeader.setVisibility(View.GONE);
            } else {
                holder.tv_chatHeader.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tv_chatHeader.setVisibility(View.VISIBLE);
        }

        if (chat.isIsseen()) {
            holder.seen.setBackgroundResource(R.drawable.ic_done_theme_24dp);
        } else {
            holder.seen.setBackgroundResource(R.drawable.ic_done_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView message, time, tv_chatHeader;
        private ImageView seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.tv_chatMsg);
            seen = itemView.findViewById(R.id.img_seenMsg);
            time = itemView.findViewById(R.id.tv_chatTime);
            tv_chatHeader = itemView.findViewById(R.id.tv_chatHeader);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
