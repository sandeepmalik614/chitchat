package chat.chitchat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import chat.chitchat.R;
import chat.chitchat.adapter.ShowParticipantsAdapter;
import chat.chitchat.model.ParticipantList;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupSecoundActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView userImage;
    private EditText edt_groupName;
    private Button btn_create;
    private RecyclerView rv_participant;
    private ShowParticipantsAdapter participantsAdapter;
    private ArrayList<ParticipantList> participantLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_secound);

        toolbar = findViewById(R.id.toolbar_createGroupSecound);
        userImage = findViewById(R.id.circleImageView2);
        edt_groupName = findViewById(R.id.editText4);
        btn_create = findViewById(R.id.button3);
        rv_participant = findViewById(R.id.rv_participant);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rv_participant.setLayoutManager(new GridLayoutManager(this, 3));
        participantLists = (ArrayList<ParticipantList>) getIntent().getSerializableExtra("participantList");
        for (int i = 0; i < participantLists.size(); ) {
            if(!participantLists.get(i).isSelected()){
                participantLists.remove(i);
            }else{
                i++;
            }
        }
        participantsAdapter = new ShowParticipantsAdapter(this, participantLists);
        rv_participant.setAdapter(participantsAdapter);
        
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_groupName.getText().toString().isEmpty()){
                    Toast.makeText(CreateGroupSecoundActivity.this, "Please enter your group subject, it can't be empty", 
                            Toast.LENGTH_SHORT).show();
                }else{
                    createGroup(edt_groupName.getText().toString());
                }
            }
        });
    }

    private void createGroup(String toString) {
    }
}
