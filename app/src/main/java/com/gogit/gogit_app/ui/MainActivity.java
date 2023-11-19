package com.gogit.gogit_app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.fragment.main.HomeFragment;
import com.gogit.gogit_app.fragment.issue.IssuesFragment;
import com.gogit.gogit_app.fragment.user.MyPageFragment;
import com.gogit.gogit_app.fragment.repo.PostAndRepositoryFragment;
import com.gogit.gogit_app.util.FragmentHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentContainer;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        fragmentContainer = findViewById(R.id.containers);

        FragmentHelper.replaceFragment(
                getSupportFragmentManager(), R.id.containers, new HomeFragment()
        );

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements NavigationBarView.OnItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.post) {
                selectedFragment = new PostAndRepositoryFragment();
            } else if (item.getItemId() == R.id.issues) {
                selectedFragment = new IssuesFragment();
            } else if (item.getItemId() == R.id.myPage) {
                selectedFragment = new MyPageFragment();
            }
            return FragmentHelper.replaceFragment(getSupportFragmentManager(), R.id.containers, selectedFragment);
        }
    }

}