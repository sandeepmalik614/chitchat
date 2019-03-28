package chat.chitchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import chat.chitchat.R;
import chat.chitchat.model.ParticipantList;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShowParticipantsAdapter extends RecyclerView.Adapter<ShowParticipantsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ParticipantList> participantLists;

    public ShowParticipantsAdapter(Context context, ArrayList<ParticipantList> participantLists) {
        this.context = context;
        this.participantLists = participantLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participants, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userName.setText(participantLists.get(position).getName());
        Glide.with(context).load(participantLists.get(position).getImage()).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.img_itemSelected);
            userName = itemView.findViewById(R.id.textView31);
        }
    }
}
