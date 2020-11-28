package com.example.nhaccuato;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.nhaccuato.offline.OfflineFragment;
import com.example.nhaccuato.play.PassData;
import com.example.nhaccuato.play.PlayFragment;
import com.example.nhaccuato.play.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayoutMain;
    private ViewPager viewPagerMain;
    OfflineFragment offlineFragment;
    public PassData mPassData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_main);
        // find view by id in activity_main
        tabLayoutMain = findViewById(R.id.tl_main);
        viewPagerMain = findViewById(R.id.vp_main);

        // create new fragment
        offlineFragment = new OfflineFragment();
        // add new fragment in function setup view pager
        setupViewPager();
        tabLayoutMain.setupWithViewPager(viewPagerMain);
        // section for fragment tabLayout.getTabAt(i).setIcon(....);
    }

    public void passVal(PassData passData) {
        this.mPassData = passData;
    }

    private void setupViewPager(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        // add new fragment
        viewPagerAdapter.addFragment(offlineFragment, "Offline Fragment");
        viewPagerMain.setAdapter(viewPagerAdapter);
    }
}