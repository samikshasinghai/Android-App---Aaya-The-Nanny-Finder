package edu.oakland;

import androidx.appcompat.app.AppCompatActivity;

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

import org.w3c.dom.Text;

public class BookingConfirmation extends AppCompatActivity
{
    DBHelper db;
    TextView nannyName;
    TextView nannyPhone;
    ImageView nannyImage;
    TextView bookingDetails;
    TextView confirmedTxt;
    Button btnManageBooking, btnCancelBooking;
    ImageView menu_icon;
    SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking__confirmation);
        nannyName = findViewById(R.id.nannyName);
        nannyPhone = findViewById(R.id.nannyPhone);
        nannyImage = findViewById(R.id.nannyImage);
        bookingDetails = findViewById(R.id.bookingDetails);
        confirmedTxt = findViewById(R.id.confirmedTxt);
        btnManageBooking = findViewById(R.id.btnManageBooking);
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        menu_icon.bringToFront();

        int BookingID = getIntent().getIntExtra("BookingID",0);
        System.out.println("Booking ID: " + BookingID);

        db = new DBHelper(this);
        hideSoftKeyboard();
        savedValues = getSharedPreferences("SavedValues",MODE_PRIVATE);
        Cursor cursor = db.confirmBooking(BookingID);

        confirmedTxt.setText("Booking " + cursor.getInt(0) + " confirmed.");
        nannyName.setText(cursor.getString(4) + " " + cursor.getString(5));
        nannyPhone.setText(cursor.getString(3));
        bookingDetails.setText(cursor.getString(7) + ", " + cursor.getString(8) + ", " + cursor.getString(9) + ", " + cursor.getString(10) + " from " + cursor.getString(1) +
                " - " +cursor.getString(2) + " on " + cursor.getString(11));
        String nanny_image = cursor.getString(6);
        int i = nanny_image.lastIndexOf('.');
        String extension = null;
        if(i > 0)
        {
            extension = nanny_image.substring(i);
        }
        String nanny_Image_final = nanny_image.replace(extension, "");
        int info = getResources().getIdentifier(getPackageName() + ":drawable/" + nanny_Image_final, null, null);
        nannyImage.setImageResource(info);

        btnManageBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingConfirmation.this, MyBookings.class);
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
                                intent = new Intent(BookingConfirmation.this, MyBookings.class);
                                startActivity(intent);
                                return true;

                            case R.id.logout:
                                intent = new Intent(BookingConfirmation.this, MainActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.preferences:
                                intent = new Intent(BookingConfirmation.this, NannyPreferences.class);
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