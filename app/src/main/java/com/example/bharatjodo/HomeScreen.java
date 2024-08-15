package com.example.bharatjodo;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {
    SessionManagement sessionManagement;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        sessionManagement = new SessionManagement(getApplicationContext());

        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), true, false);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Fragment currentFragment = getCurrentFragment();
                boolean isNavigatingBack = false;

                if (itemId == R.id.home_menu) {
                    if (!(currentFragment instanceof HomeFragment)) {
                        isNavigatingBack = currentFragment != null &&
                                (currentFragment instanceof SearchFragment ||
                                        currentFragment instanceof CallFragment ||
                                        currentFragment instanceof FriendsFragment ||
                                        currentFragment instanceof ProfileFragment);
                        loadFragment(new HomeFragment(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.search_menu) {
                    if (!(currentFragment instanceof SearchFragment)) {
                        isNavigatingBack = currentFragment != null &&
                                (currentFragment instanceof CallFragment ||
                                        currentFragment instanceof FriendsFragment ||
                                        currentFragment instanceof ProfileFragment);
                        loadFragment(new SearchFragment(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.call_menu) {
                    if (!(currentFragment instanceof CallFragment)) {
                        isNavigatingBack = currentFragment != null &&
                                (currentFragment instanceof FriendsFragment ||
                                        currentFragment instanceof ProfileFragment);
                        loadFragment(new CallFragment(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.friends_menu) {
                    if (!(currentFragment instanceof FriendsFragment)) {
                        isNavigatingBack = currentFragment != null &&
                                (currentFragment instanceof ProfileFragment);
                        loadFragment(new FriendsFragment(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.profile_menu) {
                    if (!(currentFragment instanceof ProfileFragment)) {
                        loadFragment(new ProfileFragment(), false, false);
                    }
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment newFragment, boolean isAppInitialized, boolean isNavigatingBack) {
        Fragment currentFragment = getCurrentFragment();
        loadFragment(newFragment, currentFragment, isAppInitialized, isNavigatingBack);
    }

    private void loadFragment(Fragment newFragment, Fragment currentFragment, boolean isAppInitialized, boolean isNavigatingBack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null) {
            if (isNavigatingBack) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_left, R.anim.slide_out_right,
                        R.anim.slide_in_right, R.anim.slide_out_left
                );
            } else {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right
                );
            }
        }

        if (isAppInitialized && fragmentManager.findFragmentById(R.id.frameLayout) == null) {
            fragmentTransaction.add(R.id.frameLayout, newFragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, newFragment);
        }

        fragmentTransaction.commit();
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.frameLayout);
    }
}
