package edu.oakland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerifyPhoneActivity extends AppCompatActivity
{

    Button buttonContinue;
    EditText editTextCode;
    TextView resendCode;
    DBHelper myDB;
    SQLiteDatabase sqLiteDatabase;
    private String verificationID;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth mAuth;
    private Object EGLifeStyleApplication;
    private String phnNo, verificationCd;
    TextView tvMin, tvSec,tvTimeInfo;
    CountDownTimer timer;
    boolean isTimerRunning = false;
    int min = 1;
    int sec = 60;
    public static final String ACCOUNT_SID = "AC3a977c4530d1b6db66055c5442debfd9";
    public static final String AUTH_TOKEN = "8a9a5863c501b899f98fd85f30a118f0";
    private static final String TAG = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        myDB = new DBHelper(this);
        buttonContinue = findViewById(R.id.buttonContinue);
        editTextCode = findViewById(R.id.editTextCode);
        resendCode = findViewById(R.id.resendCode);
        tvMin = findViewById(R.id.tvMin);
        tvSec = findViewById(R.id.tvSec);
        tvTimeInfo = findViewById(R.id.tvTimeInfo);
        min = 1;
        sec = 60;
        tvTimeInfo.setText("The Code will expire within a minute.");

        String phnNo = getIntent().getStringExtra("phoneNumber");
        //final String[] verificationCd = {getIntent().getStringExtra("verificationCode").toString()};
        //System.out.println("verification code " + verificationCd[0]);
        verificationCd = getIntent().getStringExtra("verificationCode").toString();
        System.out.println("verification code " + verificationCd);

        //start the timer.
        StartTimer();

        buttonContinue.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String code = editTextCode.getText().toString();

                if(code.isEmpty() || code.length() < 6)
                {
                    editTextCode.setError("Invalid Code");
                    editTextCode.requestFocus();
                    return;
                }



                //if (code.equals(verificationCd[0]))
                if (code.equals(verificationCd))
                {
                    //System.out.println("Here I am Code: "+ verificationCd[0]);
                    System.out.println("Here I am Code: "+ verificationCd);
                    boolean phoneNoExists = myDB.checkPhone(phnNo);
                    if (phoneNoExists == true){
                        Intent intent = new Intent(VerifyPhoneActivity.this,NannyPreferences.class);
                        intent.putExtra("phoneNumber", phnNo);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(VerifyPhoneActivity.this, SignUpActivity.class);
                        intent.putExtra("phoneNumber", phnNo);
                        startActivity(intent);
                    }
                }
                else {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }
            }
        });

        resendCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //generate the random number.
                editTextCode.setFocusable(true);
                buttonContinue.setFocusable(true);
                Random rand = new Random();
                String verificationCode = String.valueOf(rand.nextInt(999999) + 100000);
                //store this new verification code into this variable.
                verificationCd = verificationCode;
                //verificationCd = "123456"; //for testing. should be commented when testing in real.
                //System.out.println(verificationCode);

                //send the SMS. call the method in Main Activity.
                sendSms(phnNo,verificationCode);
                // MainActivity.sendSms(phnNo,verificationCd); //uncomment for testing. commented as we have limited messages.
                if(isTimerRunning)
                    timer.cancel();

                min = 1;
                sec = 60;
                tvMin.setText(String.valueOf("01"));
                tvSec.setText(String.valueOf("00"));
                tvTimeInfo.setText("Please enter the code within a minute");
                StartTimer();

            }
        });

    }

    public void StartTimer(){
        isTimerRunning = true;
        timer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {
                if(min > 0)
                {
                    min--;
                    tvMin.setText(String.valueOf(min));
                }
                if(sec > 0)
                {
                    sec--;
                    if(sec > 9)
                        tvSec.setText(String.valueOf(sec));
                    else
                        tvSec.setText("0"+String.valueOf(sec));
                }
            }

            @Override
            public void onFinish() {
                StopTimer();
            }
        }.start();
    }
    public void StopTimer(){
        isTimerRunning = false;
        //set verification code with a different code.
        //generate the random number.
        Random rand = new Random();
        String verificationCode = String.valueOf(rand.nextInt(999999) + 100000);
        //store this new verification code into this variable.
        verificationCd = verificationCode;
        // verificationCd = "123456"; //for testing. should be commented when testing in real.

        tvTimeInfo.setText("Timeout expired !. Please request a new code");
        editTextCode.setFocusable(false);
        editTextCode.setText("");
        buttonContinue.setFocusable(false);
        System.out.println(verificationCode);
    }

    private void sendSms(String toPhoneNumber, String message){
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.twilio.com/2010-04-01/Accounts/"+ACCOUNT_SID+"/SMS/Messages";
        String base64EncodedCredentials = "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);
        String fromPhoneNumber = "+17194517297";
        RequestBody body = new FormBody.Builder()
                .add("From", fromPhoneNumber)
                .add("To", toPhoneNumber)
                .add("Body", message)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", base64EncodedCredentials)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, "sendSms: "+ response.body().string());
        } catch (IOException e) { e.printStackTrace(); }

    }
}