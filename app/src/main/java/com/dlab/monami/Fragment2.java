package com.dlab.monami;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

public class Fragment2 extends Fragment {

    GridView gridView;
    static int screenWidth;
    private ImageView imageView;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    private GalleryManager mGalleryManager;
    public ArrayList<ImageFormat> localPhotoList;

    private ImageButton bookmark;

    String signupUsername="";

    private DatabaseReference mDatabaseRef;
    ImageAdapter imgAdapter;


    //    private ArrayList<Item> list = new ArrayList<>();
    static ArrayList<RecordItem> arrayList;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference mPostReference;
    private FirebaseDatabase database;

    FirebaseStorage storage;
    StorageReference storageReferencae;

    private StorageReference mStorageRef;

    public Fragment2() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("GalleryActivity","onCreateView");
        final View v = inflater.inflate(R.layout.fragment2, container, false);
        arrayList = new ArrayList<>(); // User ????????? ?????? ????????? ????????? (??????????????????)

//        //Intent
//        Intent postPageIntent = getActivity().getIntent();
//        String username = postPageIntent.getStringExtra("Username");

//        signupUsername=username;

//        bookmark = v.findViewById(R.id.bookmark);
        imageView = (ImageView) v.findViewById(R.id.imageItem);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true); // ?????????????????? ???????????? ??????
        gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        mGalleryManager = new GalleryManager(getActivity().getApplicationContext());
        localPhotoList = mGalleryManager.getAllPhotoPathList();

        mPostReference = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????
        databaseReference = database.getReference().child("patient_list"); // DB ????????? ??????

        final Query query = databaseReference.orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    getFirebaseDatabase();
                } else {
                    //user not found
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(fragement,"Error", Toast.LENGTH_SHORT).show();
            }
        });

        imgAdapter = new ImageAdapter(getActivity().getApplicationContext(), arrayList, localPhotoList);
        imgAdapter.notifyDataSetChanged();     // ???????????? ??????
        recyclerView.setAdapter(imgAdapter); // ????????????????????? ????????? ??????

//        bookmark.setOnClickListener(new View.OnClickListener() { // ????????????
//            @Override
//            public void onClick(View view) {
//                bookmark.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark2));
////                Intent intent = new Intent(getContext(), NewRecord.class);
////                getContext().startActivity(intent);
//            }
//        });

        return v;
    }


    public void getFirebaseDatabase(){
        final ValueEventListener postListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange","Data is Updated");
                arrayList.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String key=postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    String[] info={get.time, get.writer, get.title, get.symptom, get.img, get.comment, String.valueOf(get.type)};
                    RecordItem result= new RecordItem(info[0],info[1],info[2],info[3],info[4],info[5],get.type);

                    //list.add(result);
                    if(!info[4].equals("none")){  //??? ??????????????? ?????? ??? ???
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
                imgAdapter.notifyDataSetChanged();

                Collections.reverse(arrayList); // ???????????? ??????
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("patient_list").child(((MainActivity)getActivity()).user_name).addValueEventListener(postListener);
    }
}