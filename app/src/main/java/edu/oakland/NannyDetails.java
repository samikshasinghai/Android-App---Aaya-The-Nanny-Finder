package edu.oakland;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class NannyDetails extends AppCompatActivity
{
    DBHelper db;
    TextView name;
    TextView details;
    ImageView nannyImage;
    TextView priceAndRating;
    TextView  exp;
    TextView qualities;
    Button btnBooking;
    ImageView menu_icon;
    private Activity context;
    SharedPreferences savedValues;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        savedValues = getSharedPreferences("SavedValues",MODE_PRIVATE);
        setContentView(R.layout.activity_nanny_details);
        name = findViewById(R.id.name);
        details = findViewById(R.id.details);
        nannyImage = findViewById(R.id.nannyImage);
        priceAndRating = findViewById(R.id.price_and_Rating);
        exp = findViewById(R.id.exp);
        qualities = findViewById(R.id.qualities);
        btnBooking = findViewById(R.id.btnBooking);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        menu_icon.bringToFront();

        String phoneNb = getIntent().getStringExtra("user_phoneNumber");
        String nannyNb = getIntent().getStringExtra("nanny_phoneNumber");

        db = new DBHelper(this);

        hideSoftKeyboard();
        Cursor cursor = db.getNannyDetails(nannyNb);
        System.out.println("total no of rows:" + cursor.getCount());

        name.setText("Hi, I'm " + cursor.getString(2) + " " + cursor.getString(3));
        details.setText(cursor.getString(5) + " , MI");
        String nanny_image = cursor.getString(1);
        int i = nanny_image.lastIndexOf('.');
        String extension = null;
        if(i > 0)
        {
            extension = nanny_image.substring(i);
        }
        String nanny_Image_final = nanny_image.replace(extension, "");
        int info = getResources().getIdentifier(getPackageName() + ":drawable/" + nanny_Image_final, null, null);
        nannyImage.setImageResource(info);
        priceAndRating.setText("$11 per hr per kid | ⭐ " + String.valueOf(cursor.getInt(6)) + " stars");
        exp.setText("I have " + String.valueOf(cursor.getInt(4)) + " years of experience");

        String nannyVaccinated = cursor.getString(7);
        String nannyPetFriendly = cursor.getString(8);
        String nannyTransport = cursor.getString(9);
        String nannyNonSmoker = cursor.getString(10);
        String nannyFirstAid = cursor.getString(11);

        System.out.println("nannyVaccinated" + nannyVaccinated);
        System.out.println("nannyPetFriendly" + nannyPetFriendly);
        System.out.println("nannyTransport" + nannyTransport);
        System.out.println("nannyNonSmoker" + nannyNonSmoker);
        System.out.println("nannyFirstAid" + nannyFirstAid);

        String qualityText = "";

        if(nannyVaccinated.equals("Y"))
        {
            qualityText = qualityText + "✅ Covid-19 vaccinated     ";


        }
        else {
            qualityText = qualityText + "❌ Covid-19 vaccinated     ";
        }

        if(nannyPetFriendly.equals("Y"))
        {
            qualityText = qualityText + "✅ Pet-friendly\n\n";
        }
        else {
            qualityText = qualityText + "❌ Pet-friendly\n\n";
        }


        if(nannyTransport.equals("Y"))
        {
            qualityText = qualityText + "✅ Has my own transport  ";
        }
        else {
            qualityText = qualityText + "❌ Has my own transport  ";
        }

        if(nannyNonSmoker.equals("Y"))
        {
            qualityText = qualityText + "✅ Non-smoker\n\n";
        }
        else {
            qualityText = qualityText + "❌ Non-smoker\n\n";
        }

        if(nannyFirstAid.equals("Y"))
        {
            qualityText = qualityText + "✅ First-Aid trained";
        }
        else {
            qualityText = qualityText + "❌ First-Aid trained";
        }

        qualities.setText(qualityText);

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = savedValues.edit();
                editor.putString("nannyPhone", cursor.getString(0));
                editor.putString("nannyName", cursor.getString(2) + " " + cursor.getString(3));
                editor.apply();

                Intent intent = new Intent(NannyDetails.this, KidsInfoActivity.class);
                startActivity(intent);
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
                                intent = new Intent(NannyDetails.this, MyBookings.class);
                                startActivity(intent);
                                return true;

                            case R.id.logout:
                                intent = new Intent(NannyDetails.this, MainActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.preferences:
                                intent = new Intent(NannyDetails.this, NannyPreferences.class);
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

    private void hideSoftKeyboard()
    {
        if(getCurrentFocus() != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


}