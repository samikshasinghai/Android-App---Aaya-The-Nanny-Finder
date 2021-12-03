package edu.oakland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


public class KidsInfoActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences savedValues;
    Button btnIncNewBorn,btnDecNewBorn,btnIncToddler,btnDecToddler,
            btnIncErlScAge,btnDecErlScAge,btnIncScAge,btnDecScAge, btnPayment;
    ImageView menu_icon;
    TextView noOfNewborn, noOfToddler, noOfEarlySchool, noOfSchool, totalKids, totalAmount;
    int newBorns, toddlers, erlScAges, scAges;
    int total_amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_info);

        savedValues = getSharedPreferences("SavedValues",MODE_PRIVATE);

        btnIncNewBorn = findViewById(R.id.btnIncNewBorn);
        btnDecNewBorn = findViewById(R.id.btnDecNewBorn);

        btnIncToddler = findViewById(R.id.btnIncToddler);
        btnDecToddler = findViewById(R.id.btnDecToddler);
        btnIncErlScAge = findViewById(R.id.btnIncErlScAge);
        btnDecErlScAge = findViewById(R.id.btnDecErlScAge);
        btnIncScAge = findViewById(R.id.btnIncScAge);
        btnDecScAge = findViewById(R.id.btnDecScAge);

        btnPayment = findViewById(R.id.btnPayment);

        noOfNewborn = findViewById(R.id.noOfNewborn);
        noOfToddler = findViewById(R.id.noOfToddler);
        noOfEarlySchool = findViewById(R.id.noOfEarlySchool);
        noOfSchool = findViewById(R.id.noOfSchool);
        totalAmount = findViewById(R.id.totalAmount);
        totalKids = findViewById(R.id.totalKids);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);

        newBorns = toddlers = erlScAges = scAges = 0;

        btnIncNewBorn.setOnClickListener(this);
        btnDecNewBorn.setOnClickListener(this);
        btnIncToddler.setOnClickListener(this);
        btnDecToddler.setOnClickListener(this);
        btnIncErlScAge.setOnClickListener(this);
        btnDecErlScAge.setOnClickListener(this);
        btnIncScAge.setOnClickListener(this);
        btnDecScAge.setOnClickListener(this);
        btnPayment.setOnClickListener(this);
        menu_icon.setOnClickListener(this);
        menu_icon.bringToFront();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()){
            case R.id.btnIncNewBorn:
                newBorns++;
                process_button_actions();
                break;
            case R.id.btnDecNewBorn:
                if(newBorns > 0){
                    newBorns--;
                    process_button_actions();
                }
                break;
            case R.id.btnIncToddler:
                toddlers++;
                process_button_actions();
                break;
            case R.id.btnDecToddler:
                if(toddlers > 0){
                    toddlers--;
                    process_button_actions();
                }
                break;
            case R.id.btnIncErlScAge:
                erlScAges++;
                process_button_actions();
                break;
            case R.id.btnDecErlScAge:
                if(erlScAges > 0){
                    erlScAges--;
                    process_button_actions();
                }
                break;
            case R.id.btnIncScAge:
                scAges++;
                process_button_actions();
                break;
            case R.id.btnDecScAge:
                if(scAges > 0) {
                    scAges--;
                    process_button_actions();
                }
                break;
            case R.id.btnPayment:
                processKidsInformation();
                break;
            case R.id.menu_icon:
            {
                System.out.println("Menu Icon Clicked");
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),menu_icon);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Intent intent;
                        switch (item.getItemId()){

                            case R.id.myBookings:
                                intent = new Intent(KidsInfoActivity.this, MyBookings.class);
                                startActivity(intent);
                                return true;

                            case R.id.logout:
                                intent = new Intent(KidsInfoActivity.this, MainActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.preferences:
                                intent = new Intent(KidsInfoActivity.this, NannyPreferences.class);
                                intent.putExtra("phoneNumber", savedValues.getString("userPhnNo",""));
                                startActivity(intent);
                                return true;

                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
            }

        }

    }



    public void processKidsInformation(){
        //Toast.makeText(KidsInformation.this,"new Borns: "+newBorns+","+"toddlers: "+toddlers+","+"erl school age: "+erlScAges+",sc Age: "+scAges, Toast.LENGTH_LONG).show();
        //check if no kids are chosen, we should give a toast. int newBorns, toddlers, erlScAges, scAges;
        if(newBorns == 0 && toddlers == 0 && erlScAges == 0 && scAges == 0){
            Toast.makeText(getApplicationContext(),"Please select atleast one kid ",Toast.LENGTH_SHORT).show();
            return;
        }
        //Save to Preferences.
        saveDataToPreferences();
        //start the activity.
        Intent newIntent = new Intent(KidsInfoActivity.this, Payment.class);
        startActivity(newIntent);
    }

    public void saveDataToPreferences(){
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putInt("newBorns",newBorns);
        editor.putInt("toddlers",toddlers);
        editor.putInt("earlyScAges",erlScAges);
        editor.putInt("scAges",scAges);
        editor.putInt("totalAmount",total_amount);
        editor.apply();
    }

    public void process_button_actions(){

        noOfNewborn.setText(String.valueOf(newBorns));
        noOfToddler.setText(String.valueOf(toddlers));
        noOfEarlySchool.setText(String.valueOf(erlScAges));
        noOfSchool.setText(String.valueOf(scAges));

        totalKids.setText("Total Kids: " + String.valueOf(newBorns + toddlers + erlScAges +scAges));
        total_amount = (newBorns + toddlers + erlScAges +scAges)*11;
        totalAmount.setText("Rate per hour: $" + String.valueOf(total_amount));

    }
}
