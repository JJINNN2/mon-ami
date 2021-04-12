package com.dlab.monami;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private ArrayList<RecordItem> arrayList;
    private Context context;
    //어댑터에서 액티비티 액션을 가져올 때 context가 필요한데 어댑터에는 context가 없다.
    //선택한 액티비티에 대한 context를 가져올 때 필요하다.

    //private ArrayList<ImageInfo> imageUrls;
    //private OnItemClickListener onItemClickListener;

    public RecordAdapter(ArrayList<RecordItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
//        this.mListener = listener;
    }

    @NonNull
    @Override
    //실제 리스트뷰가 어댑터에 연결된 다음에 뷰 홀더를 최초로 만들어낸다.
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        RecordAdapter.RecordViewHolder holder = new RecordAdapter.RecordViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getProfile())
//                .into(holder.iv_profile);
        int itemposition = position;
        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_writer.setText(arrayList.get(position).getWriter());

        String[] dateformat = arrayList.get(position).getTime().split(" ");
        String[] dateformat1 = dateformat[0].split("-");
        String[] timeformat = dateformat[1].split(":");
        holder.tv_time.setText( dateformat1[1]+"월 "+dateformat1[2]+"일, "+timeformat[0]+":"+timeformat[1] );

        // 기록 Type 별로 아이콘 이미지 변경. 0:활동기록, 1:진료기록, 2:수면기록
        if(arrayList.get(position).getType()==0){
            holder.iconimage.setImageResource(R.drawable.pencil_270f);
        }
        else if(arrayList.get(position).getType()==1){
            holder.iconimage.setImageResource(R.drawable.hospital_1f3e5);
        }
        else if(arrayList.get(position).getType()==2){
            holder.iconimage.setImageResource(R.drawable.sleeping_face);
        }

    }

    @Override
    public int getItemCount() {
        // 삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_time;
        TextView tv_writer;
        ImageView iconimage;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_time = itemView.findViewById(R.id.tv_time);
            this.tv_writer = itemView.findViewById(R.id.tv_writer);
            this.iconimage = itemView.findViewById(R.id.itemicon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    int position = getAdapterPosition();
                    Intent popupIntent = new Intent(v.getContext(), RecordPopUp.class);

                    popupIntent.putExtra("time", arrayList.get(position).getTime());
                    popupIntent.putExtra("writer", arrayList.get(position).getWriter());
                    popupIntent.putExtra("title", arrayList.get(position).getTitle());
                    popupIntent.putExtra("symptom", arrayList.get(position).getSymptom());
                    popupIntent.putExtra("imgurl", arrayList.get(position).getImgUrl());
                    popupIntent.putExtra("comment", arrayList.get(position).getComment());
                    popupIntent.putExtra("type", arrayList.get(position).getType());
                    Log.d("FULL","image tile1="+arrayList.get(position).getTitle());
                    context.startActivity(popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

//                    arrayList.get(position).
//
//                    intent.putExtra("position", position);
//                    v.getContext().startActivity(intent);
//                    mListener.onItemSelected(v, position);
                    Log.d("Contact", "clicked "+getAdapterPosition());


                }
            });
        }
    }
}