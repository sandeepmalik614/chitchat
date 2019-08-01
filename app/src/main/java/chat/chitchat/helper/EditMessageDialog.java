package chat.chitchat.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import chat.chitchat.R;
import chat.chitchat.model.Chat;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.fabric.sdk.android.services.common.SafeToast;

import static chat.chitchat.helper.AppConstant.chatTableName;
import static chat.chitchat.helper.AppConstant.profileAboutTable;

public class EditMessageDialog extends Dialog implements View.OnClickListener {

    public Context context;
    public Dialog dialog;
    public Chat chat;
    public Button send, cancel;
    public EmojiconEditText edt_message;
    public ImageView img_emoji;
    public ConstraintLayout rootViewDialog;
    private EmojIconActions emojIcon;

    public EditMessageDialog(Context context, Chat chat) {
        super(context);
        this.context = context;
        this.chat = chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_edit_message);
        send = findViewById(R.id.btn_msgSend);
        cancel = findViewById(R.id.btn_msgCancel);
        edt_message = findViewById(R.id.editText9);
        img_emoji = findViewById(R.id.imageView12);
        rootViewDialog = findViewById(R.id.rootViewDialog);

        edt_message.setText(chat.getMessage());

        send.setOnClickListener(this);
        cancel.setOnClickListener(this);

        emojIcon = new EmojIconActions(context, rootViewDialog, edt_message, img_emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_msgCancel:
                dismiss();
                break;
            case R.id.btn_msgSend:
                sendMessage();
                break;
        }
    }

    private void sendMessage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(chatTableName).child(chat.getMessageId());
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = (simpleDateFormat.format(new Date()));
            Date mDate = null;
            try {
                mDate = simpleDateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String timeInMilliseconds = String.valueOf(mDate.getTime());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("message", edt_message.getText().toString());
            hashMap.put("messageDate", timeInMilliseconds);
            hashMap.put("isEdited", true);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aaa");
            String time = dateFormat.format(new Date(System.currentTimeMillis()));
            hashMap.put("time", time);
            reference.updateChildren(hashMap);
            dismiss();
        } catch (Exception e) {
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
