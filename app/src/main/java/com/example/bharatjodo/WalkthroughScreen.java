package com.example.bharatjodo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

public class WalkthroughScreen extends AppCompatActivity {

    ViewPager mSLideViewPager;
    LinearLayout mDotLayout;
    Button backbtn, nextbtn, skipbtn, finishbtn;

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
        mSLideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getitem(0)>0)
                {
                    mSLideViewPager.setCurrentItem(getitem(-1),true);
                }
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getitem(0) < 4)
                {
                    mSLideViewPager.setCurrentItem(getitem(1),true);
                }
            }
        });

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalkthroughScreen.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalkthroughScreen.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        viewPagerAdapter = new ViewPagerAdapter(this);
        mSLideViewPager.setAdapter(viewPagerAdapter);

        setUpindicator(0);
        mSLideViewPager.addOnPageChangeListener(viewListener);
    }

    public void setUpindicator(int position){

        dots = new TextView[5];
        mDotLayout.removeAllViews();

        for (int i = 0 ; i < dots.length ; i++)
        {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.inactive,getApplicationContext().getTheme()));
            mDotLayout.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.active,getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            setUpindicator(position);

            if (position == 0)
            {
                backbtn.setVisibility(View.INVISIBLE);
                nextbtn.setVisibility(View.VISIBLE);
                skipbtn.setVisibility(View.VISIBLE);
                finishbtn.setVisibility(View.GONE);
            }
            else if (position == 4)
            {
                backbtn.setVisibility(View.VISIBLE);
                nextbtn.setVisibility(View.GONE);
                skipbtn.setVisibility(View.GONE);
                finishbtn.setVisibility(View.VISIBLE);
            }
            else
            {
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

    private int getitem(int i){
        return mSLideViewPager.getCurrentItem() + i;
    }
}