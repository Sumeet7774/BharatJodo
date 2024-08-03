package com.example.bharatjodo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.bharatjodo.IndexPage;
import com.example.bharatjodo.R;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;


public class SignUpPage extends AppCompatActivity {

    Button backButton_signupPage, signup_button;
    EditText usernameEdittext, emailEdittext, passwordEdittext, phonenumberEdittext;

    private final String PHONE_PREFIX = "+91";
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        usernameEdittext = findViewById(R.id.username_edittext);
        emailEdittext = findViewById(R.id.email_edittext);
        passwordEdittext = findViewById(R.id.password_edittext);
        phonenumberEdittext = findViewById(R.id.phonenumber_edittext);
        backButton_signupPage = findViewById(R.id.back_button_signuppage);
        signup_button = findViewById(R.id.signup_button_signuppage);


        InputFilter noSpacesFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
                {
                if (source.toString().contains(" "))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Spaces are not allowed.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                    return source.toString().replace(" ", "");
                }
                return null;
            }
        };

        passwordEdittext.setFilters(new InputFilter[]
        {
                new InputFilter.LengthFilter(12),
                noSpacesFilter
        });

        usernameEdittext.setFilters(new InputFilter[]{noSpacesFilter, new InputFilter.LengthFilter(12)});
        emailEdittext.setFilters(new InputFilter[]{noSpacesFilter});
        passwordEdittext.setFilters(new InputFilter[]{noSpacesFilter});
        phonenumberEdittext.setFilters(new InputFilter[]{noSpacesFilter});

        phonenumberEdittext.setText(PHONE_PREFIX);
        phonenumberEdittext.setSelection(PHONE_PREFIX.length());
        phonenumberEdittext.addTextChangedListener(new TextWatcher() {
            boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) return;
                isFormatting = true;

                if (!s.toString().startsWith(PHONE_PREFIX)) {
                    phonenumberEdittext.setText(PHONE_PREFIX);
                    phonenumberEdittext.setSelection(PHONE_PREFIX.length());
                }

                isFormatting = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        backButton_signupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpPage.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_txt = usernameEdittext.getText().toString().trim();
                String email_txt = emailEdittext.getText().toString().trim();
                String password_txt = passwordEdittext.getText().toString().trim();
                String phonenumber_txt = phonenumberEdittext.getText().toString().trim();

                if (TextUtils.isEmpty(username_txt) || TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt) || TextUtils.isEmpty(phonenumber_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Please provide all of your credentials.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidUsername(username_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Username must contain only letters.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidEmail(email_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Please enter a valid email address",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidPassword(password_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Password must be 8 chars with A-Z,0-9 and symbols",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else if (!isValidPhoneNumber(phonenumber_txt))
                {
                    MotionToast.Companion.createColorToast(SignUpPage.this,
                            "Error", "Enter a 10-digit phone number.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignUpPage.this, R.font.montserrat_semibold));
                }
                else
                {
                    // Implement signup logic here
                }
            }
        });

        passwordEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEdittext.getRight() - passwordEdittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(passwordEdittext);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z]+");
    }


    private boolean isValidEmail(CharSequence target) {
        String emailPattern = "^[a-z][a-z0-9]*@gmail\\.com$";
        return (!TextUtils.isEmpty(target) && target.toString().matches(emailPattern));
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith(PHONE_PREFIX) && phoneNumber.length() == (PHONE_PREFIX.length() + 10);
    }

    private void togglePasswordVisibility(EditText passwordEditText)
    {
        if (isPasswordVisible)
        {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.password_hide, 0);
        }
        else
        {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.password_show, 0);
        }
        passwordEditText.setSelection(passwordEditText.length());
        isPasswordVisible = !isPasswordVisible;

        passwordEditText.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_light));
    }
}
