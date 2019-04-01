package chat.chitchat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddParticipantAdapter extends RecyclerView.Adapter<AddParticipantAdapter.ViewHolder> {

    private Context context;
    private DatabaseReference mReference;



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
