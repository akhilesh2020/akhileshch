package com.example.apipratice1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.apipratice1.MainActivity.EXTRA_Titel;
import static com.example.apipratice1.MainActivity.EXTRA_URL;
import static com.example.apipratice1.MainActivity.EXTRA_des;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent=getIntent();
        String imageurl=intent.getStringExtra(EXTRA_URL);
        String title=intent.getStringExtra(EXTRA_Titel);
        String des=intent.getStringExtra(EXTRA_des);

        ImageView imageView=findViewById(R.id.imgev);
        TextView textView=findViewById(R.id.titlet);
        TextView textView1=findViewById(R.id.subs);

        Picasso.with(this).load(imageurl).fit().centerInside().into(imageView);
        textView.setText(title);
        textView1.setText(des);
    }
}
