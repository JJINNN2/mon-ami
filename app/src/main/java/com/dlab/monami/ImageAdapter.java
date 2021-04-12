package com.dlab.monami;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<RecordItem> arrayList;
    private Context context;
    //private OnItemClickListener onItemClickListener;
    private ArrayList<ImageFormat> localPhotoList;
    private ImageButton bookmark;

    public ImageAdapter(Context context, ArrayList<RecordItem> arrayList, ArrayList<ImageFormat> localPhotoList) {
        this.context = context;
        this.arrayList = arrayList;
        this.localPhotoList = localPhotoList;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //Glide.with(context).load(imageUrls.get(i).getImageUrl()).centerCrop().into(viewHolder.img);
        //final imgFormat cur = mPhotoList.get(i).getImgPath();
        //Log.d("item_print", localPhotoList.get(i).getImgPath());
        Glide.with(context)
                .load(arrayList.get(i).getImgUrl()) // 웹 이미지 로드
                //.load(localPhotoList.get(i).getImgPath()) // 이미지 로드
                //.load("/storage/emulated/0/Download/Domestic_Goose.jpg")
                .error(null)
                .override(500,500) //해상도 최적화
                .thumbnail(0.3f) //섬네일 최적화. 지정한 %만큼 미리 이미지를 가져와 보여주기
                .centerCrop() // 중앙 크롭
                .into(viewHolder.img);
    }

    @Override
    public int getItemCount() {
        Log.d("getItemCount","list 개수: " + arrayList.size());
        return arrayList.size();
        //return localPhotoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageItem);
            //view.setOnCreateContextMenuListener(this);

            bookmark = view.findViewById(R.id.bookmark);
            bookmark.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    int position = getAdapterPosition();

                    bookmark.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_bookmark2));
                    Log.d("bookmarkTest","test");
                }
            });

            view.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    int position = getAdapterPosition();

                    Intent popupIntent = new Intent(v.getContext(), RecordPopUp.class);

                    popupIntent.putExtra("time", arrayList.get(position).getTime());
                    popupIntent.putExtra("writer", arrayList.get(position).getWriter());
                    popupIntent.putExtra("title", arrayList.get(position).getTitle());
                    popupIntent.putExtra("symptom", arrayList.get(position).getSymptom());
                    popupIntent.putExtra("imgurl", arrayList.get(position).getImgUrl());
                    popupIntent.putExtra("comment", arrayList.get(position).getComment());
                    Log.d("FULL","image tile1="+arrayList.get(position).getTitle());
                    context.startActivity(popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }
    }

}