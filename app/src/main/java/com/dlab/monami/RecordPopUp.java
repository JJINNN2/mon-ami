package com.dlab.monami;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
//import com.github.chrisbanes.photoview.PhotoView;

public class RecordPopUp extends Activity {

    private TextView tv_time;
    private TextView tv_writer;
    private TextView tv_title;
    private TextView tv_symptom;
    private ImageView iv_img;
    private TextView tv_comment;
    private TextView tv_type;
    private ImageView iconbox;

    String time;
    String writer;
    String title;
    String symptom;
    String imgUrl;
    String comment;
    int type;

    Button btnDel, btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_record_pop_up);

        tv_time = findViewById(R.id.bar_time);
        tv_writer = findViewById(R.id.bar_writer);
        tv_title = findViewById(R.id.title);
        tv_symptom = findViewById(R.id.symptom);
        iv_img = findViewById(R.id.img);
        tv_comment = findViewById(R.id.comment);
        iconbox = findViewById(R.id.icon);
        tv_type = findViewById(R.id.tv_type);
        btnDel = findViewById(R.id.btn_delete);
        btnEdit = findViewById(R.id.btn_edit);

        Intent intent = getIntent();

        time = intent.getExtras().getString("time");
        writer = intent.getExtras().getString("writer");
        title = intent.getExtras().getString("title");
        symptom = intent.getExtras().getString("symptom");
        imgUrl = intent.getExtras().getString("imgurl");
        comment = intent.getExtras().getString("comment");
        type = intent.getIntExtra("type",0);

        Log.d("popup", time);
        Log.d("popup", writer);

        Glide.with(this)
                //.load(imageUrls.get(i).getImageUrl()) // 웹 이미지 로드
                .load(imgUrl) // imageUrl
                .listener( new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .thumbnail(0.1f)
                .error(R.drawable.sample_img)   // 없을 때 뭘로 대체?
                .into(iv_img);  // 어디에 뿌릴 건지

        if(type==0){
            iconbox.setImageResource(R.drawable.pencil_270f);
            tv_type.setText("활동 기록");
        }
        else if(type==1){
            iconbox.setImageResource(R.drawable.hospital_1f3e5);
            tv_type.setText("진료 기록");
        }
        else if(type==2){
            iconbox.setImageResource(R.drawable.sleeping_face);
            tv_type.setText("수면 기록");
        }


        String[] dateformat = time.split(" ");
        String[] dateformat1 = dateformat[0].split("-");
        String[] timeformat = dateformat[1].split(":");
        tv_time.setText( dateformat1[1]+"월 "+dateformat1[2]+"일, "+timeformat[0]+":"+timeformat[1] );
        //tv_time.setText(time);
        tv_writer.setText(writer);
        tv_title.setText(title);
        tv_symptom.setText(symptom);
        tv_comment.setText(comment);

        btnEdit.setOnClickListener(new View.OnClickListener() { // Edit button click action
            @Override
            public void onClick(View view) {
                Toast.makeText(RecordPopUp.this, "기능 개발중입니다", Toast.LENGTH_LONG).show();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() { // Delete Button click action
            @Override
            public void onClick(View view) {
                Toast.makeText(RecordPopUp.this, "기능 개발중입니다", Toast.LENGTH_LONG).show();
            }
        });

    }
}