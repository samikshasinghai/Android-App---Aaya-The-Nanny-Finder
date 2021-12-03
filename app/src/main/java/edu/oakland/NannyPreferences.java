package edu.oakland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class NannyPreferences extends AppCompatActivity {

    SharedPreferences savedValues;
    String experienceReq = "";
    String genderSelected = "";
    String dateSelected = "";
    String timings = "";
    String qualities = "";
    int hour, minute;
    DBHelper db;
    StringBuilder sb_qualities = new StringBuilder();
    StringBuilder sb_timings = new StringBuilder();

    String btnDefaultClr = "#FFFFFF";
    String btnClickedClr = "#FFFF00";
    String startTimeselected = "";
    String endTimeselected = "";

    int btnDefaultClrInt = R.color.white;
    AutoCompleteTextView zipCity;
    DatePicker datePicker;
    Button startTimeBtn, endTimeBtn;
    RadioGroup genderGroup;
    RadioButton maleRB, femaleRB;
    RadioButton exp3RB, exp5RB, expAnyRB;
    CheckBox firstAidCB, petsCB, transCB, nonSmokerCB, vaccineCB;
    Button btnFind;
    ImageView menu_icon;

    Intent kidsInformationActivity;

    int startHour,startMin, endHour, endMin;

    String phnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nanny_preferences);
        String[] zipCitiesList = getResources().getStringArray(R.array.zip_City);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, zipCitiesList);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        zipCity = findViewById(R.id.zipCityAC);
        datePicker = findViewById(R.id.preferredDate);
        startTimeBtn = findViewById(R.id.startTimeBtn);
        endTimeBtn = findViewById(R.id.endTimeBtn);
        maleRB = findViewById(R.id.maleRB);
        femaleRB = findViewById(R.id.femaleRB);
        exp3RB = findViewById(R.id.exp3RB);
        exp5RB = findViewById(R.id.exp5RB);
        expAnyRB = findViewById(R.id.expAnyRB);
        firstAidCB = findViewById(R.id.firstAidCB);
        petsCB = findViewById(R.id.petsCB);
        transCB = findViewById(R.id.transCB);
        nonSmokerCB = findViewById(R.id.nonSmokerCB);
        vaccineCB = findViewById(R.id.vaccineCB);
        btnFind = findViewById(R.id.btnFind);
        genderGroup = findViewById(R.id.genderRG);
        zipCity.setAdapter(adapter);
        db = new DBHelper(this);

        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        menu_icon.bringToFront();

        phnNo = getIntent().getStringExtra("phoneNumber");
        Cursor preferences = db.checkUserPreference(phnNo);

        if (preferences != null)
        {

            //Gender Pre-populating
            if (preferences.getString(2) .equals("M")){

                maleRB.setChecked(true);
                femaleRB.setChecked(false);

            }
            else
            {
                femaleRB.setChecked(true);
                maleRB.setChecked(false);
            }

            //Experience Pre-populating
            if (preferences.getInt(1) == 3){

                exp3RB.setChecked(true);

            }
            else if (preferences.getInt(1) == 5)
            {
                exp5RB.setChecked(true);

            }
            else
            {
                expAnyRB.setChecked(true);
            }

            //Vaccine Check Box pre-populating

            if (preferences.getString(3).equals("Y")){

                vaccineCB.setChecked(true);

            }


            if (preferences.getString(4).equals("Y")){

                petsCB.setChecked(true);

            }

            if (preferences.getString(5).equals("Y")){

                transCB.setChecked(true);

            }

            if (preferences.getString(6).equals("Y")){

                nonSmokerCB.setChecked(true);

            }

            if (preferences.getString(7).equals("Y")){

                firstAidCB.setChecked(true);

            }

        }

        Cursor zipcode = db.checkZipCity(phnNo);

        if (zipcode != null){

            String textualdata = zipcode.getString(0 ) + "|" + zipcode.getString(1);
            int position = adapter.getPosition(textualdata);
            //zipCity.setText(zipcode.getString(0 ) + "|" + zipcode.getString(1));
            zipCity.setText(textualdata);
        }

        //menu


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
                                SharedPreferences.Editor editor = savedValues.edit();
                                editor.putString("userPhnNo", phnNo);
                                editor.apply();
                                intent = new Intent(NannyPreferences.this, MyBookings.class);
                                startActivity(intent);
                                return true;

                            case R.id.logout:
                                intent = new Intent(NannyPreferences.this, MainActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.preferences:
                                intent = new Intent(NannyPreferences.this, NannyPreferences.class);
                                intent.putExtra("phoneNumber", phnNo);
                                startActivity(intent);
                                return true;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // validation for date should be: input date greater than current date

                dateSelected = (datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear();

                GregorianCalendar GregorianCalendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                int dayOfWeek= GregorianCalendar.get(GregorianCalendar.DAY_OF_WEEK);

                //validation for time should  be: Start time less than End Time
                startTimeselected = startTimeBtn.getText().toString();
                endTimeselected = endTimeBtn.getText().toString();
                if(startTimeselected.equals("Start Time") || endTimeselected.equals("End Time"))
                {
                    Log.i("Nanny Preferences","Start and End time should be selected");
                    Toast.makeText(NannyPreferences.this, "Start and End time should be selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Get the current time and compare it with start time
                Date date = new Date();
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                String currdt_str = sdf1.format(date);
                Date curdate_final = null;
                try{
                    curdate_final = sdf1.parse(currdt_str);
                }
             catch (ParseException e) {
                e.printStackTrace();
            }
              System.out.println("current time is: "+curdate_final);
                //for selected start and end times.
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                Date d1 = null;
                try {
                    d1 = sdf.parse(startTimeselected);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date d2 = null;
                try {
                    d2 = sdf.parse(endTimeselected);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if( d2.getTime() <=  d1.getTime())
                {
                    Toast.makeText(NannyPreferences.this, "End time should be greater than start time", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if there is at least one hour of booking time.
                long timediff_millsecs = d2.getTime() - d1.getTime();
                int timediff_secs = (int) (timediff_millsecs/(1000));
                if(timediff_secs < 3600)
                {
                    Toast.makeText(NannyPreferences.this, "Booking time should be atleast one hour", Toast.LENGTH_SHORT).show();
                    return;
                }

               /* if(d1.getTime() <= curdate_final.getTime()){
                    Toast.makeText(NannyPreferences.this, "start time should be greater than current time", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                //create a datetime using appointment date and start time.
                GregorianCalendar GregorianCalendar1 = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),startHour,startMin);
                Date appointDate = GregorianCalendar1.getTime();
               //System.out.println("appointment date time is : "+ appointDate);

                //create a datetime using current datetime in needed format.
                Date dtnow = new Date();
                GregorianCalendar1 = new GregorianCalendar(dtnow.getYear() + 1900,dtnow.getMonth(),dtnow.getDate(),dtnow.getHours() - 5,dtnow.getMinutes());
                Date curdate = GregorianCalendar1.getTime();

                //check if the appointment requested date time is less than current date time.
                if(appointDate.getTime() <= curdate.getTime())
                {
                   Toast.makeText(NannyPreferences.this, "start time should be greater than current time", Toast.LENGTH_SHORT).show();
                   System.out.println("start time should be greater than current time");
                   return;
               }
               else{
                   System.out.println("appointment date: "+appointDate);
                   System.out.println("current date: "+ curdate);
               }

                //Toast.makeText(NannyPreferences.this, "appointment date time is : "+ appointDate, Toast.LENGTH_SHORT).show();

                boolean elapsed = d2.getTime() > d1.getTime();
                System.out.println("Time check:" + elapsed);

                String gender_screen = null;
                int exp_screen = 0;
                String vaccine_screen = null;
                String pets_screen = null;
                String trans_screen = null;
                String nonSmoker_screen = null;
                String firstAid_screen = null;

                if (maleRB.isChecked())
                {
                    gender_screen = "M";
                }else{
                    gender_screen = "F";
                }


                if(exp3RB.isChecked())
                {
                    exp_screen = 3;
                }else if(exp5RB.isChecked()){
                    exp_screen = 5;
                }else{
                    exp_screen = 0;
                }

                if(vaccineCB.isChecked())
                {
                    vaccine_screen = "Y";
                }
                else{
                    vaccine_screen = "N";
                }
                if(petsCB.isChecked())
                {
                    pets_screen = "Y";
                }
                else
                {
                    pets_screen = "N";
                }
                if(transCB.isChecked())
                {
                    trans_screen = "Y";
                }
                else
                {
                    trans_screen = "N";
                }

                if(nonSmokerCB.isChecked())
                {
                    nonSmoker_screen = "Y";
                }
                else
                {
                    nonSmoker_screen = "N";
                }

                if(firstAidCB.isChecked()){
                    firstAid_screen = "Y";
                }
                else
                {
                    firstAid_screen = "N";
                }

                String[] zip_search = zipCity.getText().toString().split("\\|");

                if (preferences != null)
                {

                    if((preferences.getString(2).equals(gender_screen)) && (preferences.getInt(1) == exp_screen) && (preferences.getString(3).equals(vaccine_screen)) && (preferences.getString(4).equals(pets_screen)) && (preferences.getString(5).equals(trans_screen)) && (preferences.getString(6).equals(nonSmoker_screen)) && (preferences.getString(7).equals(firstAid_screen)))
                    {
                       gotoIntent(phnNo, zip_search[0],dateSelected, startTimeselected, endTimeselected, dayOfWeek);
                    }
                    else
                    {
                        boolean update_success = db.updatePreference(phnNo, exp_screen, gender_screen, vaccine_screen, pets_screen, trans_screen, nonSmoker_screen, firstAid_screen);
                        if (update_success) {

                            gotoIntent(phnNo, zip_search[0],dateSelected, startTimeselected, endTimeselected, dayOfWeek);
                        }
                        else {
                            Toast.makeText(NannyPreferences.this, "System issue. Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else{

                       boolean insert_success = db.insertPreference(phnNo, exp_screen, gender_screen, vaccine_screen, pets_screen, trans_screen, nonSmoker_screen, firstAid_screen);
                       if (insert_success)
                       {
                           gotoIntent(phnNo, zip_search[0], dateSelected, startTimeselected, endTimeselected, dayOfWeek);
                       }
                       else {
                           Toast.makeText(NannyPreferences.this, "System issue. Please try again later", Toast.LENGTH_SHORT).show();
                        }
                   }

            }
        }

        );

        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popStartTimePicker();
            }
        });

        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popEndTimePicker();
            }
        });


    }

    public void processPreferencesInformation()
    {
        //Gender
        if (maleRB.isChecked())
            genderSelected = "Male";
        else if (femaleRB.isChecked())
            genderSelected = "Female";

        //Date

        dateSelected = (datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear();

        //Timings
        //popTimePicker();

        //Preferred Qualities  CheckBox cbFirstAID,cbPets, cbTransport, cbNonSmoker;
        if (firstAidCB.isChecked())
        sb_qualities.append(firstAidCB.getText().toString());
        if (petsCB.isChecked())
            sb_qualities.append(petsCB.getText().toString());
        if (transCB.isChecked())
            sb_qualities.append(transCB.getText().toString());
        if (nonSmokerCB.isChecked())
            sb_qualities.append(nonSmokerCB.getText().toString());
        if (vaccineCB.isChecked())
            sb_qualities.append(vaccineCB.getText().toString());

        //save the data to preferences.
        saveDataToPreferences();
        //start the next activity.
        NannyPreferences.this.startActivity(kidsInformationActivity);
    }

    public void saveDataToPreferences()
    {
        //Store this data into Shared Preferences
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("gender", genderSelected);
        editor.putString("experienceReq", experienceReq);
        editor.putString(" dateSelected", dateSelected);
        editor.putString("timingsSelected", sb_timings.toString());
        editor.putString("preferredQualities", sb_qualities.toString());
        editor.apply();
    }


    public void popStartTimePicker()
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                startHour = hour;
                startMin = minute;

                startTimeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Start Time");
        timePickerDialog.show();
    }

    public void popEndTimePicker()
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                endHour = hour;
                endMin = minute;


                endTimeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select End Time");
        timePickerDialog.show();
    }

    public void gotoIntent(String phnno, String ZipCode, String dateSelected, String startTimeselected, String endTimeselected, int dayOfWeek){

        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("dataSelected", dateSelected);
        editor.putString("startTimeselected", startTimeselected);
        editor.putString("endTimeselected", endTimeselected);
        editor.putString("userPhnNo", phnno);
        editor.putInt("dayOfWeek", dayOfWeek);
        editor.apply();


        Intent intent = new Intent(getApplicationContext(), Nannies.class);
        intent.putExtra("phoneNumber", phnno);
        intent.putExtra("zip_search", ZipCode);

        startActivity(intent);

    }
}