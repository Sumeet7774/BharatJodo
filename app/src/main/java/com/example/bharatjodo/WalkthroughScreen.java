package com.example.bharatjodo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class WalkthroughScreen extends AppCompatActivity {

    ViewPager mSLideViewPager;
    LinearLayout mDotLayout;
    Button backbtn, nextbtn, skipbtn, finishbtn;
    private static final int PERMISSION_REQUEST_CODE = 123;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_walkthrough_screen);

        backbtn = findViewById(R.id.backButton);
        nextbtn = findViewById(R.id.nextButton);
        skipbtn = findViewById(R.id.skipButton);
        finishbtn = findViewById(R.id.finishButton);
        mSLideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.indicator_layout);

        backbtn.setVisibility(View.GONE);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getitem(0) > 0) {
                    mSLideViewPager.setCurrentItem(getitem(-1), true);
                }
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getitem(0) < 4) {
                    mSLideViewPager.setCurrentItem(getitem(1), true);
                }
            }
        });

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
            }
        });

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
            }
        });

        viewPagerAdapter = new ViewPagerAdapter(this);
        mSLideViewPager.setAdapter(viewPagerAdapter);

        setUpindicator(0);
        mSLideViewPager.addOnPageChangeListener(viewListener);
    }

    public void setUpindicator(int position) {
        dots = new TextView[5];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.inactive, getApplicationContext().getTheme()));
            mDotLayout.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.active, getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setUpindicator(position);

            if (position <= 0) {
                backbtn.setVisibility(View.GONE);
                nextbtn.setVisibility(View.VISIBLE);
                skipbtn.setVisibility(View.VISIBLE);
                finishbtn.setVisibility(View.GONE);
            } else if (position == 4) {
                backbtn.setVisibility(View.VISIBLE);
                nextbtn.setVisibility(View.GONE);
                skipbtn.setVisibility(View.GONE);
                finishbtn.setVisibility(View.VISIBLE);
            } else {
                backbtn.setVisibility(View.VISIBLE);
                nextbtn.setVisibility(View.VISIBLE);
                skipbtn.setVisibility(View.VISIBLE);
                finishbtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private int getitem(int i) {
        return mSLideViewPager.getCurrentItem() + i;
    }

    private boolean checkPermissions() {
        int resultSendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int resultReadSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int resultCallPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return resultSendSMS == PackageManager.PERMISSION_GRANTED &&
                resultReadSMS == PackageManager.PERMISSION_GRANTED &&
                resultCallPhone == PackageManager.PERMISSION_GRANTED &&
                resultCamera == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA
        }, PERMISSION_REQUEST_CODE);
    }

    private void checkAndRequestPermissions() {
        if (checkPermissions()) {
            proceedToNextActivity();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean sendSMS = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readSMS = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean callPhone = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean camera = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                if (sendSMS && readSMS && callPhone && camera) {
                    proceedToNextActivity();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        showSettingsDialog();
                    } else {
                        MotionToast.Companion.createColorToast(WalkthroughScreen.this,
                                "Permissions", "All permissions are required",
                                MotionToastStyle.INFO,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(WalkthroughScreen.this, R.font.montserrat_semibold));
                    }
                }
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WalkthroughScreen.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permissions to use certain features. You can grant them in app settings.");
        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void proceedToNextActivity() {
        Intent intent = new Intent(WalkthroughScreen.this, IndexPage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}
