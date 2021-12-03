package edu.oakland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.os.StrictMode;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    Button btnStart;
    EditText phoneET;

    SQLiteDatabase db;
    DBHelper myDbHelper;

    public static final String ACCOUNT_SID = "AC3a977c4530d1b6db66055c5442debfd9";
    public static final String AUTH_TOKEN = "8a9a5863c501b899f98fd85f30a118f0";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneET = findViewById(R.id.phoneET);
        btnStart = findViewById(R.id.btnStart);

        myDbHelper = new DBHelper(this);
        try {
            myDbHelper.createDatabase();

        }catch(IOException e){
            throw new Error("Unable to create database");
        }

         db = myDbHelper.getWritableDatabase();

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy); }





//      Functionality for sending a validation code on the given user's phone number
        btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String phoneNumber = phoneET.getText().toString().trim();

                if(phoneNumber.isEmpty() || phoneNumber.length() < 10)
                {
                    phoneET.setError("Valid number is required");
                    phoneET.requestFocus();
                    return;
                }
                Random rand = new Random();
                String verificationCode = String.valueOf(rand.nextInt(999999) + 100000);
                System.out.println(verificationCode);

               sendSms(phoneNumber,verificationCode);
               //For Testing Purpose
                //verificationCode = "123456";
                Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("verificationCode", verificationCode);
                startActivity(intent);
            }
        });
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