package chat.chitchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import chat.chitchat.R;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.img_fullImage);
        Glide.with(this).load(getIntent().getStringExtra("fullImage")).into(imageView);
    }
}
