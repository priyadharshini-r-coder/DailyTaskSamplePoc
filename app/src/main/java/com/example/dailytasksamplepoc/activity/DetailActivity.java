package com.example.dailytasksamplepoc.activity;

import static com.example.dailytasksamplepoc.activity.RecyclerViewJSON.EXTRA_CREATOR;
import static com.example.dailytasksamplepoc.activity.RecyclerViewJSON.EXTRA_LIKES;
import static com.example.dailytasksamplepoc.activity.RecyclerViewJSON.EXTRA_URL;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailytasksamplepoc.R;
import com.squareup.picasso.Picasso;

class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(EXTRA_URL);
        String creatorName = intent.getStringExtra(EXTRA_CREATOR);
        int likeCount = intent.getIntExtra(EXTRA_LIKES, 0);
        ImageView imageView = findViewById(R.id.image_view_detail);
        TextView textViewCreator = findViewById(R.id.text_view_creator_detail);
        TextView textViewLikes = findViewById(R.id.text_view_like_detail);
        Picasso.with(this).load(imageUrl).fit().centerInside().into(imageView);
        textViewCreator.setText(creatorName);
        textViewLikes.setText("Likes: " + likeCount);
    }
}
