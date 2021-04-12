package com.dlab.monami;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView btn_logout;
    private ImageView profile;
    private TextView txt_username;
    private ImageButton side_btn;
    String user_name;
    String user_email;

    private TextView userEmail_tv;
    private TextView userNick_tv;

    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment4 fragment4;

    private DrawerLayout drawerLayout;
    private View drawerView;
    private View frag1;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference mPostReference;

    private AccountInfo currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userNick_tv = findViewById(R.id.nickname);
        userEmail_tv = findViewById(R.id.useremail);

        viewPager = findViewById(R.id.view_pager); //탭별 화면 보이는 view pager
        tabLayout = findViewById(R.id.tab_layout); //탭바

        tabLayout.setupWithViewPager(viewPager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawerView);

        mPostReference = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference().child("account_list"); // DB 테이블 연결

        currentAccount = new AccountInfo("","");

        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            user_email = intent.getExtras().getString("email", "");
            userEmail_tv.setText(user_email);
        }

        final Query query = databaseReference.orderByChild("name");
        query.addListenerForSingleValueEvent(new ValueEventListener() { // User name 가져오기
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    getFirebaseDatabase();

                    fragment1 = new Fragment1();
                    fragment2 = new Fragment2();
                    fragment3 = new Fragment3();
                    fragment4 = new Fragment4();

//        final LayoutInflater factory = getLayoutInflater();
//        frag1 = factory.inflate(R.layout.fragment1, null);
                    side_btn = findViewById(R.id.sideBtn);
                    side_btn.setOnClickListener(new ImageButton.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("main","clicked drawerbutton");
                            drawerLayout.openDrawer(drawerView);
                        }
                    });

                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
                    viewPagerAdapter.addFragment(fragment1, "");
                    viewPagerAdapter.addFragment(fragment2, "");
                    viewPagerAdapter.addFragment(fragment3, "");
                    viewPagerAdapter.addFragment(fragment4, "");
                    viewPager.setAdapter(viewPagerAdapter);

                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab1);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab2);
                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_tab3);
                    tabLayout.getTabAt(3).setIcon(R.drawable.ic_tab4);
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


    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();
/*
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Fragment1.newinstance();
                case 1:
                    return Fragment2.newinstance(user_email);
                case 2:
                    return Fragment3.newinstance(user_email);
                default:
                    return null;
            }
        }
    */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        /*
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        */


        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }

    public void getFirebaseDatabase(){
        final ValueEventListener postListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange","Data is Updated");
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    FirebaseAccountPost get = postSnapshot.getValue(FirebaseAccountPost.class);
                    String[] info={get.email, get.name};

                    if(info[0].equals(user_email)){
                        user_name = get.name;
                        Log.e("Founduser",user_name);
                        userNick_tv.setText(user_name);
                        currentAccount.setName(user_name);
                        currentAccount.setEmail(user_email);
                        Log.d("MainActivity","user info = "+user_email+" "+user_name);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("account_list").addValueEventListener(postListener);
    }

/*
    public void SetStatusBarColor(System.Drawing.Color color, bool darkStatusBarTint)
    {
        if (Build.VERSION.SdkInt < Android.OS.BuildVersionCodes.Lollipop)
            return;

        var activity = Plugin.CurrentActivity.CrossCurrentActivity.Current.Activity;
        var window = activity.Window;
        window.AddFlags(Android.Views.WindowManagerFlags.DrawsSystemBarBackgrounds);
        window.ClearFlags(Android.Views.WindowManagerFlags.TranslucentStatus);
        window.SetStatusBarColor(color.ToPlatformColor());
    }*/
}