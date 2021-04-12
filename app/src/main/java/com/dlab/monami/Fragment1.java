package com.dlab.monami;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Fragment1 extends Fragment {
    public Fragment1() {
        // Required empty public constructor

    }

    private DatabaseReference mPostReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecordItem> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ImageButton side_btn;

    private LinearLayout recordbtn1, recordbtn2;
    private ShimmerFrameLayout mShimmerViewContainer;

    private View view;                                    // 갤러리 이미지 탐색

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment1, container, false);
        Log.d("Fragment1","onCreateView");
        recordbtn1 = view.findViewById(R.id.recordbtn1);
        recordbtn2 = view.findViewById(R.id.recordbtn2);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);

        mPostReference = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference().child("patient_list"); // DB 테이블 연결

        final Query query = databaseReference.orderByChild("time");

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Signing in...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

        mShimmerViewContainer.startShimmer();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    getFirebaseDatabase(((MainActivity)getActivity()).user_name);
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                } else {
                    //user not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("Fragment1", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        recordbtn1.setOnClickListener(new View.OnClickListener() { // 활동기록
            @Override
            public void onClick(View view) {
                Log.e("userinfo",((MainActivity)getActivity()).user_name);
                if(((MainActivity)getActivity()).user_name!=null) {
                    Intent intent = new Intent(getContext(), NewRecord.class);
                    intent.putExtra("writer",((MainActivity)getActivity()).user_name);
                    getContext().startActivity(intent);
                }
            }
        });

        recordbtn2.setOnClickListener(new View.OnClickListener() { //진료기록
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "기능 개발중입니다", Toast.LENGTH_LONG).show();
            }
        });

        adapter = new RecordAdapter(arrayList, getContext());
        adapter.notifyDataSetChanged();     // 내림차순 정렬
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

        return view;
    }

    public void getFirebaseDatabase(String name){
        final ValueEventListener postListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange","Data is Updated");
                mShimmerViewContainer.setVisibility(View.VISIBLE);
                mShimmerViewContainer.startShimmer();
                arrayList.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String key=postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    String[] info={get.time, get.writer, get.title, get.symptom, get.img, get.comment, String.valueOf(get.type)};
                    RecordItem result= new RecordItem(info[0],info[1],info[2],info[3],info[4],info[5],get.type);

                    //list.add(result);
                    if(info[4]!=null){  //행 사진파일이 있을 때 실
                        //RecordItem recordItem = new RecordItem();
                        result.setTime(info[0]);
                        result.setWriter(info[1]);
                        result.setTitle(info[2]);
                        result.setSymptom(info[3]);
                        result.setImgUrl(info[4]);
                        result.setComment(info[5]);
                        result.setType(get.type);
                        arrayList.add(result);
                    }
                    Log.d("getFirebaseDatabase","key: "+key);
                    Log.d("getFirebaseDatabase","info: "+info[0]+" "+info[1]);
                    Log.d("ListSize",String.valueOf(arrayList.size()));

                }
                adapter.notifyDataSetChanged();

                Collections.reverse(arrayList); // 내림차순 정렬
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //Log.d("name is ",((MainActivity)getActivity()).user_name);
        Log.e("Fragment1 find list in ", name);
        mPostReference.child("patient_list").child(name).addValueEventListener(postListener);
    }




}
