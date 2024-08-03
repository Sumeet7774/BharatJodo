package com.example.bharatjodo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

    Button otpverification_backbutton, verifyOTP_button;
    TextView mobile_number_textview, resendOTP_textview;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private String verificationID;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_verification);

        otpverification_backbutton = findViewById(R.id.back_button_otpverificationpage);
        mobile_number_textview = findViewById(R.id.mobile_num_text);
        otp1 = findViewById(R.id.otp_et1);
        otp2 = findViewById(R.id.otp_et2);
        otp3 = findViewById(R.id.otp_et3);
        otp4 = findViewById(R.id.otp_et4);
        otp5 = findViewById(R.id.otp_et5);
        otp6 = findViewById(R.id.otp_et6);
        verifyOTP_button = findViewById(R.id.verifybutton_otpverification);
        resendOTP_textview = findViewById(R.id.resend_otp_textview);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("PHONE_NUMBER");
        verificationID = intent.getStringExtra("VERIFICATION_ID");
        mobile_number_textview.setText(phoneNumber);

        // Set up TextWatchers for OTP fields
        otp1.addTextChangedListener(new OTPTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new OTPTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new OTPTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new OTPTextWatcher(otp4, otp5));
        otp5.addTextChangedListener(new OTPTextWatcher(otp5, otp6));
        otp6.addTextChangedListener(new OTPTextWatcher(otp6, null));

        // Handle verify OTP button click
        verifyOTP_button.setOnClickListener(view -> {
            String otp = otp1.getText().toString() +
                    otp2.getText().toString() +
                    otp3.getText().toString() +
                    otp4.getText().toString() +
                    otp5.getText().toString() +
                    otp6.getText().toString();

            if (otp.length() < 6) {
                MotionToast.Companion.createColorToast(OTP_verification.this,
                        "Error", "Please enter the full OTP",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
                return;
            }

            verifyOTP(otp);
        });

        // Handle back button click
        otpverification_backbutton.setOnClickListener(view -> {
            Intent intent2 = new Intent(OTP_verification.this, SignUpPage.class);
            startActivity(intent2);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        // Handle resend OTP button click
        resendOTP_textview.setOnClickListener(view -> resendOTP());

        // Start the countdown timer for resend OTP
        startResendTimer();
    }

    private void verifyOTP(String otp) {
        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, otp);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        MotionToast.Companion.createColorToast(OTP_verification.this,
                                "Success", "Phone number verified successfully.",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));

                        // Redirect to IndexPage after successful verification
                        Intent intent = new Intent(OTP_verification.this, IndexPage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        MotionToast.Companion.createColorToast(OTP_verification.this,
                                "Error", "Invalid OTP. Please try again.",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
                    }
                });
    }

    private void resendOTP() {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(getIntent().getStringExtra("PHONE_NUMBER"))
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // Handle automatic OTP verification if needed
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        MotionToast.Companion.createColorToast(OTP_verification.this,
                                "Error", "Failed to resend OTP. Please try again.",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        verificationID = verificationId;
                        resendToken = token;
                        MotionToast.Companion.createColorToast(OTP_verification.this,
                                "Success", "OTP resent successfully.",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(OTP_verification.this, R.font.montserrat_semibold));
                        startResendTimer();
                    }
                })
                .build();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(options);
    }

    private void startResendTimer() {
        resendOTP_textview.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendOTP_textview.setText("Resend OTP in " + millisUntilFinished / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                resendOTP_textview.setText("Resend OTP");
                resendOTP_textview.setEnabled(true);
            }
        }.start();
    }

    private class OTPTextWatcher implements TextWatcher {

        private final EditText currentEditText;
        private final EditText nextEditText;

        OTPTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
