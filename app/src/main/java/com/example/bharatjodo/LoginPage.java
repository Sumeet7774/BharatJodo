package com.example.bharatjodo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class LoginPage extends AppCompatActivity {

    EditText username_editText, phonenumber_editText;
    Button backButton_login, log_in;

    private final String PHONE_PREFIX = "+91";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        username_editText = findViewById(R.id.username_login_edittext);
        phonenumber_editText = findViewById(R.id.phonenumber_login_edittext);
        backButton_login = findViewById(R.id.back_button_loginpage);
        log_in = findViewById(R.id.login_button_loginpage);

        InputFilter noSpacesFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
            {
                if (source.toString().contains(" "))
                {
                    MotionToast.Companion.createColorToast(LoginPage.this,
                            "Error", "Spaces are not allowed.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                    return source.toString().replace(" ", "");
                }
                return null;
            }
        };


        username_editText.setFilters(new InputFilter[]{noSpacesFilter, new InputFilter.LengthFilter(12)});
        phonenumber_editText.setFilters(new InputFilter[]{noSpacesFilter});

        phonenumber_editText.setText(PHONE_PREFIX);
        phonenumber_editText.setSelection(PHONE_PREFIX.length());
        phonenumber_editText.addTextChangedListener(new TextWatcher() {
            boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) return;
                isFormatting = true;

                if (!s.toString().startsWith(PHONE_PREFIX)) {
                    phonenumber_editText.setText(PHONE_PREFIX);
                    phonenumber_editText.setSelection(PHONE_PREFIX.length());
                }

                isFormatting = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        backButton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_txt = username_editText.getText().toString().trim();
                String phonenumber_txt = phonenumber_editText.getText().toString().trim();

                if (TextUtils.isEmpty(username_txt) || TextUtils.isEmpty(phonenumber_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPage.this,
                            "Error", "Please provide all of your credentials.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidUsername(username_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPage.this,
                            "Error", "Username must contain only letters.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidPhoneNumber(phonenumber_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPage.this,
                            "Error", "Enter a 10-digit phone number.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPage.this, R.font.montserrat_semibold));
                }
                else
                {

                }
            }
        });
    }

    private boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z]+");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith(PHONE_PREFIX) && phoneNumber.length() == (PHONE_PREFIX.length() + 10);
    }
}