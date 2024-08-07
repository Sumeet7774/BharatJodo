package com.example.bharatjodo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class OTP_verification extends AppCompatActivity {

    private EditText otpEditText1, otpEditText2, otpEditText3, otpEditText4, otpEditText5, otpEditText6;
    private Button verifyButton, backButton_otpverificationpage;
    private ProgressBar progressBar;
    TextView mobile_number_textview, resendOtp;
    private FirebaseAuth mAuth;
    private String verificationId;
    private ProgressDialog progressDialog;
    private String phoneNumber;


    private static final long RESEND_OTP_TIMEOUT = 60 * 1000; // 1 minute
    private long lastResendTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpEditText1 = findViewById(R.id.otp_et1);
        otpEditText2 = findViewById(R.id.otp_et2);
        otpEditText3 = findViewById(R.id.otp_et3);
        otpEditText4 = findViewById(R.id.otp_et4);
        otpEditText5 = findViewById(R.id.otp_et5);
        otpEditText6 = findViewById(R.id.otp_et6);
        verifyButton = findViewById(R.id.verifybutton_otpverification);
        progressBar = findViewById(R.id.progress_bar);
        mobile_number_textview = findViewById(R.id.mobile_num_text);
        backButton_otpverificationpage = findViewById(R.id.back_button_otpverificationpage);
        resendOtp = findViewById(R.id.resend_otp_textview);

        mAuth = FirebaseAuth.getInstance();

        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        if (phoneNumber != null)
        {
            Log.d("OTP_VERIFICATION", "Received phone number: " + phoneNumber);
            mobile_number_textview.setText(phoneNumber);
            sendVerificationCode(phoneNumber);
        }
        else {
            Log.d("OTP_VERIFICATION", "No phone number received");
        }

        setupOTPInputs();
        setupResendOtp();

        verifyButton.setOnClickListener(view -> {
            String code = otpEditText1.getText().toString().trim() +
                    otpEditText2.getText().toString().trim() +
                    otpEditText3.getText().toString().trim() +
                    otpEditText4.getText().toString().trim() +
                    otpEditText5.getText().toString().trim() +
                    otpEditText6.getText().toString().trim();

            if (code.length() == 6) {
                verifyCode(code);
            } else {
                MotionToast.Companion.createColorToast(OTP_verification.this,
                        "Error", "Please enter a valid OTP",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
            }
        });


        backButton_otpverificationpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OTP_verification.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
    }

    private void setupResendOtp() {
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastResendTime >= RESEND_OTP_TIMEOUT) {
                    sendVerificationCode(phoneNumber);
                    lastResendTime = currentTime;
                } else {
                    long timeLeft = (RESEND_OTP_TIMEOUT - (currentTime - lastResendTime)) / 1000;
                    MotionToast.Companion.createColorToast(OTP_verification.this,
                            "Error", "Please wait " + timeLeft + " seconds before requesting again.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
                }
            }
        });
    }

    private void setupOTPInputs() {
        otpEditText1.addTextChangedListener(new GenericTextWatcher(otpEditText1));
        otpEditText2.addTextChangedListener(new GenericTextWatcher(otpEditText2));
        otpEditText3.addTextChangedListener(new GenericTextWatcher(otpEditText3));
        otpEditText4.addTextChangedListener(new GenericTextWatcher(otpEditText4));
        otpEditText5.addTextChangedListener(new GenericTextWatcher(otpEditText5));
        otpEditText6.addTextChangedListener(new GenericTextWatcher(otpEditText6));
    }

    private void handleVerificationFailure(FirebaseException e) {
        if (e.getMessage().contains("blocked")) {
            MotionToast.Companion.createColorToast(OTP_verification.this,
                    "Error", "Too many requests. Please try again later.",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
        } else {
            MotionToast.Companion.createColorToast(OTP_verification.this,
                    "Error", "Verification failed",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
        }
    }


    private void sendVerificationCode(String phoneNumber) {
        MotionToast.Companion.createColorToast(OTP_verification.this,
                "Info", "Sending OTP...",
                MotionToastStyle.INFO,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending OTP...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        progressDialog.dismiss();
                        String code = credential.getSmsCode();
                        if (code != null) {
                            fillOTPInputs(code);
                            verifyCode(code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressDialog.dismiss();
                        handleVerificationFailure(e);
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(s, token);
                        progressDialog.dismiss();
                        verificationId = s;
                        MotionToast.Companion.createColorToast(OTP_verification.this,
                                "Success", "OTP sent successfully.",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void fillOTPInputs(String code) {
        if (code.length() == 6) {
            otpEditText1.setText(String.valueOf(code.charAt(0)));
            otpEditText2.setText(String.valueOf(code.charAt(1)));
            otpEditText3.setText(String.valueOf(code.charAt(2)));
            otpEditText4.setText(String.valueOf(code.charAt(3)));
            otpEditText5.setText(String.valueOf(code.charAt(4)));
            otpEditText6.setText(String.valueOf(code.charAt(5)));
        }
    }

    private void verifyCode(String code) {
        if (verificationId != null) {
            progressBar.setVisibility(View.VISIBLE);
            verifyButton.setEnabled(false);

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(OTP_verification.this, IndexPage.class);
                    startActivity(intent);
                    finish();
                } else {
                    MotionToast.Companion.createColorToast(OTP_verification.this,
                            "Error", "Verification failed. Try again.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
                }
                progressBar.setVisibility(View.GONE);
                verifyButton.setEnabled(true);
            });
        } else {
            MotionToast.Companion.createColorToast(OTP_verification.this,
                    "Error", "Verification Id is null",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
        }
    }

    private class GenericTextWatcher implements TextWatcher {

        private final EditText editText;

        public GenericTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (editText.getText().toString().length() == 1) {
                if (editText == otpEditText1) {
                    otpEditText2.requestFocus();
                } else if (editText == otpEditText2) {
                    otpEditText3.requestFocus();
                } else if (editText == otpEditText3) {
                    otpEditText4.requestFocus();
                } else if (editText == otpEditText4) {
                    otpEditText5.requestFocus();
                } else if (editText == otpEditText5) {
                    otpEditText6.requestFocus();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }
}
