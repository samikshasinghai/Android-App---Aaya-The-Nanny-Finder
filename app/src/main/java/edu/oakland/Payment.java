package edu.oakland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Payment extends AppCompatActivity {

    TextView paymentText,cashText;
    RadioGroup paymentRG;
    RadioButton cardRB,cashRB;
    EditText cardNum,cardExp,cardCv,cardZip;
    TableLayout ccTable;
    Button btnBook;
    ImageView menu_icon;
    DBHelper db;

    SharedPreferences savedValues;
    //shared preferences variables
    String userPhone;
    String nannyPhone;
    String scheduledDate;
    String scheduledSTime;
    String scheduledETime;
    int noNewborn;
    int noToddler;
    int noEarlySchool;
    int noPreSchool;
    int totalAmount;
    String nannyName;

    StringBuilder emailBody;
    String emailSubject;
    String emailTo;
    String emailCC;
    String userName;
    String locationService;

    int paymentMode = 1;
    float total_amount_for_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        paymentText = findViewById(R.id.paymentText);
        paymentRG = findViewById(R.id.paymentRG);
        cardRB = findViewById(R.id.cardRB);
        cashRB = findViewById(R.id.cashRB);
        cardNum = findViewById(R.id.cardNum);
        cardExp = findViewById(R.id.cardExp);
        cardCv = findViewById(R.id.cardCv);
        cardZip = findViewById(R.id.cardZip);
        ccTable = findViewById(R.id.ccTable);
        btnBook = findViewById(R.id.btnBook);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        menu_icon.bringToFront();

        db = new DBHelper(this);

        emailBody = new StringBuilder();

        savedValues = getSharedPreferences("SavedValues",MODE_PRIVATE);
        LoadSharedPreferences();
        LoadUserInfo();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Date d1 = null;
        try {
            d1 = sdf.parse(scheduledSTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d2 = null;
        try {
            d2 = sdf.parse(scheduledETime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long timediff_millsecs = d2.getTime() - d1.getTime();
        System.out.println("Total millsecs: " + timediff_millsecs);
        float hour = ((float) timediff_millsecs/(1000*60*60));

        System.out.println("Total Hours: " + hour);
        total_amount_for_time = totalAmount * hour;

        paymentText.setText("$" + String.valueOf(total_amount_for_time));

        paymentRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if( i == R.id.cardRB){
                    ccTable.setVisibility(View.VISIBLE);
                    paymentMode = Constants.cardPayment;
                }
                else{
                    ccTable.setVisibility(View.GONE);
                    paymentMode = Constants.cashPayment;
                }
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //saveDataToPreferences();
                //buildEmailBody();
                //sendEmail();
                if(cardRB.isChecked())
                {
                    if(validateCardInfo())
                    {
                        performBooking();
                    }
                }
                else
                {
                    performBooking();
                }

            }
        });

        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Menu Icon Clicked");
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),menu_icon);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()){

                            case R.id.myBookings:
                                intent = new Intent(Payment.this, MyBookings.class);
                                startActivity(intent);
                                return true;

                            case R.id.logout:
                                intent = new Intent(Payment.this, MainActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.preferences:
                                intent = new Intent(Payment.this, NannyPreferences.class);
                                intent.putExtra("phoneNumber", savedValues.getString("userPhnNo",""));
                                startActivity(intent);
                                return true;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }



    public void performBooking(){
        int bookingID = db.createBooking(userPhone, nannyPhone, scheduledDate, scheduledSTime, scheduledETime, noNewborn, noToddler, noEarlySchool, noPreSchool, paymentMode, 11);

        System.out.println("Booking ID =" + bookingID);
        if (bookingID > 0)
        {

            emailSubject ="Your Nanny is Confirmed - Booking ID: " + String.valueOf(bookingID);
            buildEmailBody();
            sendEmail();

            Intent intent = new Intent(Payment.this, BookingConfirmation.class);
            intent.putExtra("BookingID",bookingID );
            startActivity(intent);
        }
        else {
            Toast.makeText(Payment.this, "System issue. Please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean validateCardInfo()
    {
        //card number. cardNum,cardExp,cardCv,cardZip;

        if (cardNum.getText().toString().trim().isEmpty() || cardExp.getText().toString().trim().isEmpty() || cardCv.getText().toString().trim().isEmpty() || cardZip.getText().toString().trim().isEmpty()) {
            Toast.makeText(Payment.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cardNum.getText().toString().trim().length() < 16){
            Toast.makeText(Payment.this, "Invalid Card Number", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(cardCv.getText().toString().trim().length() < 4){
            Toast.makeText(Payment.this, "Invalid CVV", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(cardZip.getText().toString().trim().length() < 5){
            Toast.makeText(Payment.this, "Invalid Zip", Toast.LENGTH_SHORT).show();
            return  false;
        }
        String cardExpDet = cardExp.getText().toString();
        String expPattern = "(0[1-9]|1[0-2])/[0-9]{4}";

        if(!cardExpDet.matches(expPattern))
        {
            Toast.makeText(Payment.this, "Invalid Expiry Date", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            String[] cardsplit = cardExpDet.split("/");
            if(cardsplit.length == 2)
            {
                int mon = Integer.parseInt(cardsplit[0]);
                int year= Integer.parseInt(cardsplit[1]);
                if(String.valueOf(mon).length() > 2){
                    Toast.makeText(Payment.this, "Invalid Month", Toast.LENGTH_SHORT).show();
                    return false;
                }
                int curYear = new Date().getYear() + 1900;
                if(String.valueOf(year).length() > 4 ||  year < curYear) {
                    Toast.makeText(Payment.this, "Invalid Year", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Invalid Expiration Date", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }




    public void LoadSharedPreferences(){
        userPhone = savedValues.getString("userPhnNo","");
        nannyPhone = savedValues.getString("nannyPhone","");
        scheduledDate = savedValues.getString("dataSelected","");
        scheduledSTime = savedValues.getString("startTimeselected","");
        scheduledETime = savedValues.getString("endTimeselected","");
        noNewborn = savedValues.getInt("newBorns",0);
        noToddler = savedValues.getInt("toddlers",0);
        noEarlySchool =savedValues.getInt("earlyScAges",0);
        noPreSchool = savedValues.getInt("scAges",0);
        totalAmount = savedValues.getInt("totalAmount",0);
        nannyName = savedValues.getString("nannyName", "");
    }

    public void sendEmail(){
        JavaMailAPI mailAPI = new JavaMailAPI(this,emailTo, emailSubject,emailBody.toString(), emailCC);
        mailAPI.execute();
    }

    public void LoadUserInfo(){
        Cursor cursor = db.getUserInfo(userPhone);
        userName = cursor.getString(0) + " " + cursor.getString(1);
        locationService = cursor.getString(4) + ", " + cursor.getString(5) + ", " + cursor.getString(6) + ", " + cursor.getString(7);
        emailTo = cursor.getString(2);
        emailCC = "pparvathaneni@oakland.edu";

    }

    public void buildEmailBody(){

        emailBody.append("Hi "+userName +",");
        emailBody.append("\n\n");

        emailBody.append("Thank you for choosing Aaya to book a nanny.");
        emailBody.append("\n\n");

        emailBody.append("Please see below for confirmation details:");
        emailBody.append("\n\n");

        emailBody.append("Nanny Name: "+nannyName);
        emailBody.append("\n\n");

        emailBody.append("Location of service: "+ locationService);
        emailBody.append("\n\n");

        emailBody.append("Scheduled Date: "+scheduledDate);
        emailBody.append("\n\n");

        emailBody.append("Schedule Time: "+scheduledSTime + " - " + scheduledETime);
        emailBody.append("\n\n");

        emailBody.append("Rate per Hour: $"+ totalAmount);
        emailBody.append("\n\n");

        emailBody.append("Total Amount: $"+ total_amount_for_time);
        emailBody.append("\n\n");

        if (paymentMode == 1){

            emailBody.append("Mode of Payment: Credit Card - Paid");
            emailBody.append("\n\n");
        }
        else
        {
            emailBody.append("Mode of Payment: Cash - Pending Payment");
            emailBody.append("\n\n");
        }

        emailBody.append("Please feel free to reach us on testemail@nannyfinder.com for any further questions.");
        emailBody.append("\n\n");

        emailBody.append("Thanks & Regards");
        emailBody.append("\n");
        emailBody.append("Aaya - The Nanny Finder Team");

    }
}